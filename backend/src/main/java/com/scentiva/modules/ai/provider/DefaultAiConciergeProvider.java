package com.scentiva.modules.ai.provider;

import com.scentiva.modules.ai.dto.*;
import com.scentiva.modules.catalog.dto.ProductSummaryResponse;
import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.review.model.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DefaultAiConciergeProvider implements AiProvider {

    @Override
    public ScentQuizResponse evaluateScentQuiz(ScentQuizRequest request, List<Product> availableProducts) {
        String family = (request.getFragranceFamily() != null) ? request.getFragranceFamily().trim() : "Woody";
        String occasion = (request.getOccasion() != null) ? request.getOccasion().trim() : "Daily Signature";
        GenderTarget gender = request.getGender() != null ? request.getGender() : GenderTarget.UNISEX;

        // Filter and score products
        List<ScoredProduct> scored = new ArrayList<>();
        for (Product product : availableProducts) {
            if (!product.isActive() || product.isDeleted()) continue;

            // Gender compatibility
            if (gender != GenderTarget.UNISEX && product.getGender() != GenderTarget.UNISEX && product.getGender() != gender) {
                continue;
            }

            int score = calculateQuizMatchScore(product, request);
            List<String> matchingNotes = extractMatchingNotes(product, request.getPreferredNotes());

            String reason = String.format("Balances %s with bespoke %s sillage suitable for %s.",
                    product.getBrand().getName(),
                    product.getOlfactoryPyramid() != null ? product.getOlfactoryPyramid().getFragranceFamily() : "Haute Parfumerie",
                    occasion);

            scored.add(new ScoredProduct(product, score, reason, matchingNotes, occasion));
        }

        scored.sort(Comparator.comparingInt(ScoredProduct::score).reversed());

        List<ScentRecommendationDto> recommendations = scored.stream()
                .limit(4)
                .map(sp -> ScentRecommendationDto.builder()
                        .product(mapToSummary(sp.product()))
                        .matchScore(sp.score())
                        .matchReason(sp.reason())
                        .matchingNotes(sp.matchingNotes())
                        .idealOccasion(sp.idealOccasion())
                        .build())
                .toList();

        String personaTitle = getPersonaTitle(family, occasion);
        String personaDescription = getPersonaDescription(family, occasion);

        return ScentQuizResponse.builder()
                .personaTitle(personaTitle)
                .personaDescription(personaDescription)
                .recommendedFamily(family)
                .recommendations(recommendations)
                .build();
    }

    @Override
    public SemanticSearchResponse performSemanticSearch(String query, int limit, List<Product> availableProducts) {
        if (query == null || query.trim().isEmpty()) {
            return SemanticSearchResponse.builder()
                    .query(query)
                    .interpretedIntent("General luxury perfume curation")
                    .detectedNotes(List.of())
                    .detectedEmotions(List.of())
                    .results(List.of())
                    .build();
        }

        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        List<String> detectedNotes = extractNotesFromQuery(normalizedQuery);
        List<String> detectedEmotions = extractEmotionsFromQuery(normalizedQuery);

        List<SemanticProductMatchDto> matches = new ArrayList<>();

        for (Product product : availableProducts) {
            if (!product.isActive() || product.isDeleted()) continue;

            double score = computeSemanticScore(product, normalizedQuery, detectedNotes, detectedEmotions);
            if (score > 0.2) {
                String explanation = buildSemanticExplanation(product, detectedNotes, detectedEmotions);
                List<String> attributes = new ArrayList<>(detectedNotes);
                attributes.addAll(detectedEmotions);

                matches.add(SemanticProductMatchDto.builder()
                        .product(mapToSummary(product))
                        .relevanceScore(Math.min(1.0, score))
                        .matchExplanation(explanation)
                        .extractedAttributes(attributes)
                        .build());
            }
        }

        matches.sort(Comparator.comparingDouble(SemanticProductMatchDto::getRelevanceScore).reversed());
        int maxLimit = (limit <= 0) ? 10 : limit;
        List<SemanticProductMatchDto> topMatches = matches.stream().limit(maxLimit).toList();

        return SemanticSearchResponse.builder()
                .query(query)
                .interpretedIntent("Seeking fragrance harmonizing " + String.join(", ", detectedNotes.isEmpty() ? List.of("niche accords") : detectedNotes))
                .detectedNotes(detectedNotes)
                .detectedEmotions(detectedEmotions)
                .results(topMatches)
                .build();
    }

    @Override
    public AiChatResponse generateChatResponse(AiChatRequest request, List<Product> availableProducts) {
        String msg = request.getMessage().toLowerCase(Locale.ROOT);
        List<String> detectedNotes = extractNotesFromQuery(msg);

        List<Product> matched = availableProducts.stream()
                .filter(p -> p.isActive() && !p.isDeleted())
                .filter(p -> {
                    String fullText = (p.getName() + " " + p.getBrand().getName() + " " + p.getCategory().getName() + " " +
                            (p.getOlfactoryPyramid() != null ? p.getOlfactoryPyramid().getTopNotes() + " " + p.getOlfactoryPyramid().getHeartNotes() + " " + p.getOlfactoryPyramid().getBaseNotes() : ""))
                            .toLowerCase(Locale.ROOT);
                    return detectedNotes.stream().anyMatch(fullText::contains) || fullText.contains(msg);
                })
                .limit(3)
                .toList();

        if (matched.isEmpty()) {
            matched = availableProducts.stream().filter(p -> p.isActive() && !p.isDeleted()).limit(2).toList();
        }

        String reply = "Welcome to the Scentiva Haute Parfumerie Concierge. Based on your request, I recommend exploring compositions with exquisite natural absolutes and refined olfactory transitions. Here are our premier curations for you:";

        List<ProductSummaryResponse> suggested = matched.stream().map(this::mapToSummary).toList();
        List<String> suggestedQuestions = List.of(
                "Which fragrance has the longest longevity?",
                "What is the difference between Extrait de Parfum and Eau de Parfum?",
                "Recommend an evening gourmand fragrance with vanilla and amber."
        );

        return AiChatResponse.builder()
                .reply(reply)
                .suggestedFragrances(suggested)
                .highlightedNotes(detectedNotes)
                .suggestedQuestions(suggestedQuestions)
                .build();
    }

    @Override
    public ProductEditorialDescriptionResponse generateEditorialStory(Product product) {
        String brand = product.getBrand().getName();
        String name = product.getName();
        String notes = (product.getOlfactoryPyramid() != null)
                ? String.format("top notes of %s, blossoming into a heart of %s, anchored by %s",
                product.getOlfactoryPyramid().getTopNotes(),
                product.getOlfactoryPyramid().getHeartNotes(),
                product.getOlfactoryPyramid().getBaseNotes())
                : "hand-selected rare botanical absolutes and rare woods";

        String narrative = String.format("%s by %s is an olfactory sonnet celebrating timeless luxury. Opening with %s, it envelopes the wearer in an intoxicating aura that resonates from dusk till dawn.",
                name, brand, notes);

        return ProductEditorialDescriptionResponse.builder()
                .productId(product.getId())
                .productName(name)
                .brandName(brand)
                .editorialHeadline("The Quintessence of Haute Parfumerie Artistry")
                .olfactoryNarrative(narrative)
                .moodAndSillage("Sophisticated, Radiant, Intimate & Magnetic (12+ hours sillage)")
                .recommendedPairings(List.of("Silk Evening Wear", "Private Salon Soirées", "Crisp Autumn Nights"))
                .build();
    }

    @Override
    public ProductSentimentSummaryResponse summarizeSentiment(Long productId, List<Review> reviews) {
        if (reviews.isEmpty()) {
            return ProductSentimentSummaryResponse.builder()
                    .productId(productId)
                    .sentimentClassification("NEUTRAL")
                    .positivePercentage(100.0)
                    .topPraisedAttributes(List.of("Awaiting inaugural verified collector reviews"))
                    .topCritiques(List.of())
                    .syntheticEditorialConsensus("No client reviews submitted yet for this composition.")
                    .build();
        }

        long positiveCount = reviews.stream().filter(r -> r.getRating() >= 4).count();
        double positiveRatio = (double) positiveCount / reviews.size() * 100.0;

        String classification = (positiveRatio >= 85.0) ? "OVERWHELMINGLY_POSITIVE"
                : (positiveRatio >= 65.0) ? "POSITIVE"
                : (positiveRatio >= 40.0) ? "MIXED" : "CRITICAL";

        return ProductSentimentSummaryResponse.builder()
                .productId(productId)
                .sentimentClassification(classification)
                .positivePercentage(Math.round(positiveRatio * 10.0) / 10.0)
                .topPraisedAttributes(List.of("Exceptional longevity & projection", "Exquisite bottle craft", "Smooth natural ingredients"))
                .topCritiques(List.of("Strong initial projection for sensitive noses"))
                .syntheticEditorialConsensus("Revered by fragrance connoisseurs for its masterfully balanced notes and distinctive signature trail.")
                .build();
    }

    private int calculateQuizMatchScore(Product product, ScentQuizRequest request) {
        int score = 75; // baseline

        if (request.getFragranceFamily() != null) {
            String fam = request.getFragranceFamily().toLowerCase(Locale.ROOT);
            if (product.getCategory().getName().toLowerCase(Locale.ROOT).contains(fam)) {
                score += 15;
            }
            if (product.getOlfactoryPyramid() != null && product.getOlfactoryPyramid().getFragranceFamily() != null
                    && product.getOlfactoryPyramid().getFragranceFamily().toLowerCase(Locale.ROOT).contains(fam)) {
                score += 10;
            }
        }

        if (request.getPreferredNotes() != null && product.getOlfactoryPyramid() != null) {
            String allNotes = (product.getOlfactoryPyramid().getTopNotes() + " " +
                    product.getOlfactoryPyramid().getHeartNotes() + " " +
                    product.getOlfactoryPyramid().getBaseNotes()).toLowerCase(Locale.ROOT);

            for (String note : request.getPreferredNotes()) {
                if (allNotes.contains(note.toLowerCase(Locale.ROOT))) {
                    score += 5;
                }
            }
        }

        return Math.min(99, Math.max(70, score));
    }

    private List<String> extractMatchingNotes(Product product, List<String> preferred) {
        if (preferred == null || product.getOlfactoryPyramid() == null) return List.of("Bergamot", "Cedarwood", "Amber");
        String allNotes = (product.getOlfactoryPyramid().getTopNotes() + " " +
                product.getOlfactoryPyramid().getHeartNotes() + " " +
                product.getOlfactoryPyramid().getBaseNotes()).toLowerCase(Locale.ROOT);

        return preferred.stream()
                .filter(n -> allNotes.contains(n.toLowerCase(Locale.ROOT)))
                .toList();
    }

    private String getPersonaTitle(String family, String occasion) {
        return switch (family.toLowerCase(Locale.ROOT)) {
            case "woody" -> "The Velvet Nocturne";
            case "floral" -> "The Parisian Blossom";
            case "oriental" -> "The Amber Alchemist";
            case "fresh" -> "The Mediterranean Aristocrat";
            case "gourmand" -> "The Sweet Epicurean";
            default -> "The Haute Connoisseur";
        };
    }

    private String getPersonaDescription(String family, String occasion) {
        return "You gravitate toward sophisticated olfactory nuances that exude effortless confidence, subtle mystique, and memorable magnetism.";
    }

    private List<String> extractNotesFromQuery(String query) {
        List<String> known = List.of("vanilla", "oud", "rose", "bergamot", "amber", "sandalwood", "leather", "tobacco", "iris", "cardamom", "vetiver", "patchouli", "musk", "jasmine");
        return known.stream().filter(query::contains).toList();
    }

    private List<String> extractEmotionsFromQuery(String query) {
        List<String> known = List.of("warm", "fresh", "smoky", "sweet", "spicy", "sensual", "elegant", "dark", "romantic", "intense");
        return known.stream().filter(query::contains).toList();
    }

    private double computeSemanticScore(Product product, String query, List<String> notes, List<String> emotions) {
        double score = 0.0;
        String text = (product.getName() + " " + product.getBrand().getName() + " " + product.getCategory().getName() + " " +
                (product.getOlfactoryPyramid() != null ? product.getOlfactoryPyramid().getTopNotes() + " " + product.getOlfactoryPyramid().getHeartNotes() + " " + product.getOlfactoryPyramid().getBaseNotes() : "") + " " +
                (product.getDescription() != null ? product.getDescription() : ""))
                .toLowerCase(Locale.ROOT);

        if (text.contains(query)) score += 0.5;

        for (String note : notes) {
            if (text.contains(note)) score += 0.2;
        }

        for (String emotion : emotions) {
            if (text.contains(emotion)) score += 0.15;
        }

        return score;
    }

    private String buildSemanticExplanation(Product product, List<String> notes, List<String> emotions) {
        return String.format("Matches key notes [%s] and mood [%s] within %s olfactory profile.",
                String.join(", ", notes.isEmpty() ? List.of("refined accords") : notes),
                String.join(", ", emotions.isEmpty() ? List.of("luxury presence") : emotions),
                product.getName());
    }

    private ProductSummaryResponse mapToSummary(Product product) {
        List<ProductVariant> activeVariants = product.getVariants() != null
                ? product.getVariants().stream().filter(ProductVariant::isActive).toList()
                : List.of();

        BigDecimal minPrice = activeVariants.stream()
                .map(ProductVariant::getEffectivePrice)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        BigDecimal maxPrice = activeVariants.stream()
                .map(ProductVariant::getEffectivePrice)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);

        Concentration primaryConcentration = activeVariants.stream()
                .map(ProductVariant::getConcentration)
                .findFirst()
                .orElse(Concentration.EDP);

        String primaryImageUrl = (product.getImages() != null && !product.getImages().isEmpty())
                ? product.getImages().stream().filter(ProductImage::isPrimary).map(ProductImage::getImageUrl).findFirst().orElse(product.getImages().get(0).getImageUrl())
                : null;

        return ProductSummaryResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .brandName(product.getBrand().getName())
                .brandSlug(product.getBrand().getSlug())
                .categoryName(product.getCategory().getName())
                .categorySlug(product.getCategory().getSlug())
                .gender(product.getGender())
                .fragranceFamily(product.getOlfactoryPyramid() != null ? product.getOlfactoryPyramid().getFragranceFamily() : "Haute Parfumerie")
                .primaryConcentration(primaryConcentration)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .primaryImageUrl(primaryImageUrl)
                .isFeatured(product.isFeatured())
                .inStock(true)
                .variantCount(activeVariants.size())
                .build();
    }

    private record ScoredProduct(Product product, int score, String reason, List<String> matchingNotes, String idealOccasion) {}
}
