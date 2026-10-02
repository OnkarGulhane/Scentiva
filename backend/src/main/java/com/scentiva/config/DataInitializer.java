package com.scentiva.config;

import com.scentiva.modules.auth.model.Role;
import com.scentiva.modules.auth.model.User;
import com.scentiva.modules.auth.model.UserStatus;
import com.scentiva.modules.auth.repository.UserRepository;
import com.scentiva.modules.catalog.model.*;
import com.scentiva.modules.catalog.repository.*;
import com.scentiva.modules.cms.model.Banner;
import com.scentiva.modules.cms.model.BannerPlacement;
import com.scentiva.modules.cms.model.EditorialStory;
import com.scentiva.modules.cms.repository.BannerRepository;
import com.scentiva.modules.cms.repository.EditorialStoryRepository;
import com.scentiva.modules.customer.model.Address;
import com.scentiva.modules.customer.model.AddressType;
import com.scentiva.modules.customer.model.Customer;
import com.scentiva.modules.customer.model.LoyaltyTier;
import com.scentiva.modules.customer.repository.CustomerRepository;
import com.scentiva.modules.inventory.model.InventoryRecord;
import com.scentiva.modules.inventory.model.Warehouse;
import com.scentiva.modules.inventory.repository.InventoryRecordRepository;
import com.scentiva.modules.inventory.repository.WarehouseRepository;
import com.scentiva.modules.promotion.model.Coupon;
import com.scentiva.modules.promotion.model.DiscountType;
import com.scentiva.modules.promotion.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * High-fidelity Data Initializer & Database Seeder for SCENTIVA Haute Parfumerie.
 * Automatically seeds luxury brands, categories, products, multi-location inventory,
 * default admin & customer accounts, CMS stories, banners, and promotional coupons
 * on application startup when the database is empty.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OlfactoryPyramidRepository olfactoryPyramidRepository;
    private final ProductImageRepository productImageRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRecordRepository inventoryRecordRepository;
    private final CouponRepository couponRepository;
    private final EditorialStoryRepository editorialStoryRepository;
    private final BannerRepository bannerRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already contains data. Skipping initial seeding.");
            return;
        }

        log.info("Initializing SCENTIVA Haute Parfumerie Master Seed Data...");

        // 1. Seed Warehouses
        Warehouse mumbaiWh = warehouseRepository.save(Warehouse.builder()
                .code("MUMBAI_CENTRAL")
                .name("Scentiva Mumbai Central Vault")
                .city("Mumbai")
                .state("Maharashtra")
                .isActive(true)
                .build());

        Warehouse delhiWh = warehouseRepository.save(Warehouse.builder()
                .code("DELHI_FLAGSHIP")
                .name("Scentiva Delhi Atelier Warehouse")
                .city("New Delhi")
                .state("Delhi")
                .isActive(true)
                .build());

        // 2. Seed Default Accounts
        User adminUser = userRepository.save(User.builder()
                .email("admin@scentiva.com")
                .passwordHash(passwordEncoder.encode("Admin@123456"))
                .role(Role.ROLE_ADMIN)
                .status(UserStatus.ACTIVE)
                .build());

        User customerUser = userRepository.save(User.builder()
                .email("customer@scentiva.com")
                .passwordHash(passwordEncoder.encode("Customer@123456"))
                .role(Role.ROLE_CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build());

        Customer customer = Customer.builder()
                .user(customerUser)
                .firstName("Eleanor")
                .lastName("Vance")
                .phone("+919876543210")
                .loyaltyTier(LoyaltyTier.GOLD)
                .build();

        Address primaryAddr = Address.builder()
                .customer(customer)
                .fullName("Eleanor Vance")
                .phone("+919876543210")
                .addressLine1("Penthouse 42, Altamount Road")
                .addressLine2("Cumballa Hill")
                .city("Mumbai")
                .state("Maharashtra")
                .postalCode("400026")
                .country("India")
                .addressType(AddressType.HOME)
                .isDefault(true)
                .build();
        customer.addAddress(primaryAddr);
        customerRepository.save(customer);

        // 3. Seed Luxury Categories
        Category orientalCat = categoryRepository.save(Category.builder()
                .name("Oriental & Amber")
                .slug("oriental-and-amber")
                .description("Opulent, resinous, warm compositions infused with rare spices and balsamic undertones.")
                .imageUrl("https://images.unsplash.com/photo-1594035910387-fea47794261f?auto=format&fit=crop&q=80&w=800")
                .displayOrder(1)
                .isActive(true)
                .build());

        Category woodyCat = categoryRepository.save(Category.builder()
                .name("Woody & Earthy")
                .slug("woody-and-earthy")
                .description("Noble cedarwood, Mysore sandalwood, vetiver, and smoky dark oud facets.")
                .imageUrl("https://images.unsplash.com/photo-1547887537-6158d64c35b3?auto=format&fit=crop&q=80&w=800")
                .displayOrder(2)
                .isActive(true)
                .build());

        Category floralCat = categoryRepository.save(Category.builder()
                .name("Floral & Rose")
                .slug("floral-and-rose")
                .description("May rose, Damask blossoms, night-blooming jasmine, and white floral bouquets.")
                .imageUrl("https://images.unsplash.com/photo-1588405748880-12d1d2a59f75?auto=format&fit=crop&q=80&w=800")
                .displayOrder(3)
                .isActive(true)
                .build());

        Category freshCat = categoryRepository.save(Category.builder()
                .name("Fresh & Citrus")
                .slug("fresh-and-citrus")
                .description("Crisp Calabrian bergamot, marine ozone, and sparkling sunlit aromatics.")
                .imageUrl("https://images.unsplash.com/photo-1616949755610-8c9bbc08f138?auto=format&fit=crop&q=80&w=800")
                .displayOrder(4)
                .isActive(true)
                .build());

        Category gourmandCat = categoryRepository.save(Category.builder()
                .name("Gourmand & Warm")
                .slug("gourmand-and-warm")
                .description("Bourbon vanilla, roasted tonka bean, cognac cask oak, and decadent cacao.")
                .imageUrl("https://images.unsplash.com/photo-1523293182086-7651a899d37f?auto=format&fit=crop&q=80&w=800")
                .displayOrder(5)
                .isActive(true)
                .build());

        // 4. Seed Luxury Perfume Brands
        Brand mfk = brandRepository.save(Brand.builder()
                .name("Maison Francis Kurkdjian")
                .slug("maison-francis-kurkdjian")
                .originCountry("France")
                .tier(BrandTier.HERITAGE_MAISON)
                .description("Parisian haute parfumerie founded in 2009, celebrated for luminous mastery and poetic sillage.")
                .logoUrl("https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?auto=format&fit=crop&q=80&w=300")
                .isActive(true)
                .build());

        Brand creed = brandRepository.save(Brand.builder()
                .name("Creed")
                .slug("creed")
                .originCountry("France")
                .tier(BrandTier.HERITAGE_MAISON)
                .description("Historic dynastic fragrance house crafting artisan royal infusions since 1760.")
                .logoUrl("https://images.unsplash.com/photo-1523293182086-7651a899d37f?auto=format&fit=crop&q=80&w=300")
                .isActive(true)
                .build());

        Brand tomFord = brandRepository.save(Brand.builder()
                .name("Tom Ford")
                .slug("tom-ford")
                .originCountry("United States")
                .tier(BrandTier.PRESTIGE)
                .description("Private Blend artisanal scents capturing bold luxury and provocative sophistication.")
                .logoUrl("https://images.unsplash.com/photo-1547887537-6158d64c35b3?auto=format&fit=crop&q=80&w=300")
                .isActive(true)
                .build());

        Brand byredo = brandRepository.save(Brand.builder()
                .name("Byredo")
                .slug("byredo")
                .originCountry("Sweden")
                .tier(BrandTier.NICHE_ATELIER)
                .description("Contemporary European luxury brand translating memories and emotions into olfactory art.")
                .logoUrl("https://images.unsplash.com/photo-1588405748880-12d1d2a59f75?auto=format&fit=crop&q=80&w=300")
                .isActive(true)
                .build());

        Brand pdm = brandRepository.save(Brand.builder()
                .name("Parfums de Marly")
                .slug("parfums-de-marly")
                .originCountry("France")
                .tier(BrandTier.PRESTIGE)
                .description("Reviving the opulent spirit of the 18th-century French Royal Court of Louis XV.")
                .logoUrl("https://images.unsplash.com/photo-1594035910387-fea47794261f?auto=format&fit=crop&q=80&w=300")
                .isActive(true)
                .build());

        Brand kilian = brandRepository.save(Brand.builder()
                .name("Kilian Paris")
                .slug("kilian-paris")
                .originCountry("France")
                .tier(BrandTier.PRESTIGE)
                .description("Heir to French cognac dynasties crafting intoxicating, nocturnal luxury fragrances.")
                .logoUrl("https://images.unsplash.com/photo-1616949755610-8c9bbc08f138?auto=format&fit=crop&q=80&w=300")
                .isActive(true)
                .build());

        Brand xerjoff = brandRepository.save(Brand.builder()
                .name("Xerjoff")
                .slug("xerjoff")
                .originCountry("Italy")
                .tier(BrandTier.ARTISAN_EXCLUSIVE)
                .description("Italian bespoke perfumery blending the rarest natural raw essences with jewel-encrusted flacons.")
                .logoUrl("https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?auto=format&fit=crop&q=80&w=300")
                .isActive(true)
                .build());

        // 5. Seed Core Haute Parfumerie Products & Variants
        seedProduct(
                "Baccarat Rouge 540 Extrait de Parfum",
                "baccarat-rouge-540-extrait",
                mfk, orientalCat, GenderTarget.UNISEX, true,
                "Intensifying the radiance of the three auras found in the eau de parfum without betraying the original inspiration. Grandiflorum jasmine from Egypt, bitter almond from Morocco, and radiant ambergris accords.",
                "Oriental Floral Amber",
                "Bitter Almond from Morocco, Saffron",
                "Egyptian Jasmine Grandiflorum, Cedarwood",
                "Ambergris, Woody Musk Accord",
                "ETERNAL", 18,
                "https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?auto=format&fit=crop&q=80&w=800",
                List.of(
                        new VariantSeed("MFK-BR540-EXT-70ML", 70, Concentration.EXTRAIT, new BigDecimal("38500.00"), new BigDecimal("35900.00"), 25),
                        new VariantSeed("MFK-BR540-EXT-200ML", 200, Concentration.EXTRAIT, new BigDecimal("74000.00"), null, 10)
                ),
                mumbaiWh, delhiWh
        );

        seedProduct(
                "Aventus Millesime",
                "creed-aventus-millesime",
                creed, freshCat, GenderTarget.FOR_HIM, true,
                "The exceptional fragrance inspired by the dramatic life of a historic emperor celebrating strength, power, and triumph. Hand-selected blackcurrant, Italian bergamot, French apples, and royal birch.",
                "Fruity Chypre",
                "Calabrian Bergamot, Blackcurrant Leaves, French Apple, Pineapple",
                "Pink Berries, Birch, Patchouli, Jasmine",
                "Musk, Oakmoss, Ambergris, Vanilla",
                "LONG_LASTING", 12,
                "https://images.unsplash.com/photo-1523293182086-7651a899d37f?auto=format&fit=crop&q=80&w=800",
                List.of(
                        new VariantSeed("CRD-AVN-EDP-100ML", 100, Concentration.EDP, new BigDecimal("32000.00"), new BigDecimal("29800.00"), 30),
                        new VariantSeed("CRD-AVN-EDP-50ML", 50, Concentration.EDP, new BigDecimal("22500.00"), null, 15)
                ),
                mumbaiWh, delhiWh
        );

        seedProduct(
                "Tobacco Vanille Eau de Parfum",
                "tom-ford-tobacco-vanille",
                tomFord, gourmandCat, GenderTarget.UNISEX, true,
                "Opulent, warm, and iconic. Reminiscent of an English gentlemen's club, blended with rich spices, aromatic tobacco flower, rich vanilla, and cocoa.",
                "Warm Amber Gourmand",
                "Tobacco Leaf, Spicy Aromatic Notes",
                "Tonka Bean, Tobacco Blossom, Vanilla, Cacao",
                "Dry Fruit Accord, Rich Woods",
                "ETERNAL", 16,
                "https://images.unsplash.com/photo-1547887537-6158d64c35b3?auto=format&fit=crop&q=80&w=800",
                List.of(
                        new VariantSeed("TF-TOBVAN-EDP-50ML", 50, Concentration.EDP, new BigDecimal("24000.00"), null, 20),
                        new VariantSeed("TF-TOBVAN-EDP-100ML", 100, Concentration.EDP, new BigDecimal("38000.00"), new BigDecimal("34500.00"), 12)
                ),
                mumbaiWh, delhiWh
        );

        seedProduct(
                "Delina Exclusif",
                "parfums-de-marly-delina-exclusif",
                pdm, floralCat, GenderTarget.FOR_HER, true,
                "A captivating floral sillage dressed in a regal pink velvet aura. Turkish rose, lychee, incense, and glowing amber create an unforgettable queenly presence.",
                "Floral Amber",
                "Lychee, Pear, Bergamot",
                "Turkish Rose, Incense, Agarwood (Oud)",
                "Vanilla, Amber, Woody Notes",
                "ETERNAL", 14,
                "https://images.unsplash.com/photo-1588405748880-12d1d2a59f75?auto=format&fit=crop&q=80&w=800",
                List.of(
                        new VariantSeed("PDM-DELINA-EXT-75ML", 75, Concentration.EXTRAIT, new BigDecimal("29500.00"), new BigDecimal("27000.00"), 18)
                ),
                mumbaiWh, delhiWh
        );

        seedProduct(
                "Angels' Share by Kilian",
                "kilian-angels-share",
                kilian, gourmandCat, GenderTarget.UNISEX, true,
                "Inspired by the eighth generation of Hennessy cognac craftsmanship. A liquor accord infused with oak barrels, cinnamon essence, and praline.",
                "Amber Liquor Gourmand",
                "Cognac Oil Essence",
                "Cinnamon Bark, Oak Wood Absolute, Tonka Bean",
                "Sandalwood, Praline, Bourbon Vanilla",
                "LONG_LASTING", 12,
                "https://images.unsplash.com/photo-1594035910387-fea47794261f?auto=format&fit=crop&q=80&w=800",
                List.of(
                        new VariantSeed("KIL-ANGSHR-EDP-50ML", 50, Concentration.EDP, new BigDecimal("21000.00"), null, 25)
                ),
                mumbaiWh, delhiWh
        );

        seedProduct(
                "Gypsy Water Eau de Parfum",
                "byredo-gypsy-water",
                byredo, woodyCat, GenderTarget.UNISEX, false,
                "An ode to the beauty of Romani culture, its unique customs, intimate beliefs and distinguished way of living. Pine needles, sandalwood, and fresh lemon.",
                "Woody Aromatic",
                "Bergamot, Lemon, Pepper, Juniper Berries",
                "Incense, Pine Needles, Orris",
                "Amber, Vanilla, Sandalwood",
                "MODERATE", 8,
                "https://images.unsplash.com/photo-1616949755610-8c9bbc08f138?auto=format&fit=crop&q=80&w=800",
                List.of(
                        new VariantSeed("BYR-GYPWTR-EDP-100ML", 100, Concentration.EDP, new BigDecimal("23500.00"), null, 30)
                ),
                mumbaiWh, delhiWh
        );

        seedProduct(
                "Naxos Eau de Parfum",
                "xerjoff-naxos",
                xerjoff, orientalCat, GenderTarget.UNISEX, true,
                "Celebrating the deep, sensual heart of Sicily. Sunlit Mediterranean citrus juxtaposed with precious spices, sweet honey, and rich tobacco.",
                "Aromatic Spicy Amber",
                "Bergamot, Lemon, Lavender",
                "Jasmine Sambac, Cinnamon, Honey, Cashmeran",
                "Tobacco Leaf, Tonka Bean, Vanilla",
                "ETERNAL", 16,
                "https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?auto=format&fit=crop&q=80&w=800",
                List.of(
                        new VariantSeed("XER-NAXOS-EDP-100ML", 100, Concentration.EDP, new BigDecimal("26500.00"), new BigDecimal("24800.00"), 20)
                ),
                mumbaiWh, delhiWh
        );

        // 6. Seed Coupons
        couponRepository.save(Coupon.builder()
                .code("SCENTIVA10")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(new BigDecimal("10.00"))
                .minOrderValue(new BigDecimal("5000.00"))
                .maxDiscount(new BigDecimal("2500.00"))
                .usageLimitGlobal(1000)
                .usageLimitPerUser(2)
                .isActive(true)
                .expiresAt(LocalDateTime.now().plusMonths(6))
                .build());

        couponRepository.save(Coupon.builder()
                .code("LUXURY20")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(new BigDecimal("20.00"))
                .minOrderValue(new BigDecimal("15000.00"))
                .maxDiscount(new BigDecimal("6000.00"))
                .usageLimitGlobal(500)
                .usageLimitPerUser(1)
                .isActive(true)
                .expiresAt(LocalDateTime.now().plusMonths(6))
                .build());

        couponRepository.save(Coupon.builder()
                .code("WELCOME500")
                .discountType(DiscountType.FIXED_AMOUNT)
                .discountValue(new BigDecimal("500.00"))
                .minOrderValue(new BigDecimal("3000.00"))
                .maxDiscount(new BigDecimal("500.00"))
                .usageLimitGlobal(5000)
                .usageLimitPerUser(1)
                .isActive(true)
                .expiresAt(LocalDateTime.now().plusYears(1))
                .build());

        // 7. Seed Editorial Stories
        editorialStoryRepository.save(EditorialStory.builder()
                .title("The Alchemy of Ambergris & Saffron: Inside Baccarat Rouge 540")
                .slug("the-alchemy-of-ambergris-saffron")
                .subtitle("How Master Perfumer Francis Kurkdjian created a contemporary modern icon")
                .excerpt("A rare glimpse into the molecular harmony between cedarwood warmth, hedione radiance, and crystallized saffron.")
                .contentHtml("<p>Born of the encounter between Maison Francis Kurkdjian and Baccarat to celebrate the crystal manufacturer's 250th birthday, Baccarat Rouge 540 lays down an olfactory signature that is luminous, intense, and profoundly poetic...</p>")
                .coverImageUrl("https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?auto=format&fit=crop&q=80&w=1200")
                .authorName("Scentiva Guild of Master Noses")
                .readingTimeMinutes(5)
                .isFeatured(true)
                .publishedAt(LocalDateTime.now().minusDays(3))
                .tags("Masterclass,Francis Kurkdjian,Ambergris,Saffron")
                .build());

        editorialStoryRepository.save(EditorialStory.builder()
                .title("The Sovereign Sillage: Royal Perfumes of the French Court")
                .slug("sovereign-sillage-royal-perfumes")
                .subtitle("From Versailles to contemporary Haute Parfumerie")
                .excerpt("Discover how 18th-century equestrian nobility and the perfumed court of Louis XV inspire modern luxury creations.")
                .contentHtml("<p>During the reign of Louis XV, the French Court was renowned as 'la cour parfumée' — where scent flowed from marble fountains and infused every sovereign garment...</p>")
                .coverImageUrl("https://images.unsplash.com/photo-1588405748880-12d1d2a59f75?auto=format&fit=crop&q=80&w=1200")
                .authorName("Eleanor Vance")
                .readingTimeMinutes(4)
                .isFeatured(true)
                .publishedAt(LocalDateTime.now().minusDays(7))
                .tags("History,Parfums de Marly,Versailles")
                .build());

        // 8. Seed CMS Banners
        bannerRepository.save(Banner.builder()
                .title("Haute Parfumerie Private Reserve")
                .subtitle("Explore extraits de parfum, rare natural ouds, and master perfumer signatures")
                .ctaText("Discover The Collection")
                .ctaLink("/shop")
                .imageUrl("https://images.unsplash.com/photo-1594035910387-fea47794261f?auto=format&fit=crop&q=80&w=1920")
                .mobileImageUrl("https://images.unsplash.com/photo-1594035910387-fea47794261f?auto=format&fit=crop&q=80&w=800")
                .placement(BannerPlacement.HERO_CAROUSEL)
                .displayOrder(1)
                .isActive(true)
                .startsAt(LocalDateTime.now().minusDays(1))
                .endsAt(LocalDateTime.now().plusYears(1))
                .build());

        log.info("SCENTIVA Haute Parfumerie Master Seed Data successfully initialized!");
    }

    private void seedProduct(
            String name, String slug, Brand brand, Category category,
            GenderTarget gender, boolean isFeatured, String description,
            String family, String topNotes, String heartNotes, String baseNotes,
            String sillage, int longevity, String primaryImageUrl,
            List<VariantSeed> variants, Warehouse mumbaiWh, Warehouse delhiWh
    ) {
        Product product = productRepository.save(Product.builder()
                .name(name)
                .slug(slug)
                .brand(brand)
                .category(category)
                .gender(gender)
                .isFeatured(isFeatured)
                .description(description)
                .isActive(true)
                .build());

        OlfactoryPyramid pyramid = OlfactoryPyramid.builder()
                .product(product)
                .fragranceFamily(family)
                .topNotes(topNotes)
                .heartNotes(heartNotes)
                .baseNotes(baseNotes)
                .sillageRating(sillage)
                .longevityHours(longevity)
                .build();
        olfactoryPyramidRepository.save(pyramid);

        productImageRepository.save(ProductImage.builder()
                .product(product)
                .imageUrl(primaryImageUrl)
                .altText(name + " luxury flacon")
                .isPrimary(true)
                .displayOrder(0)
                .build());

        for (VariantSeed vs : variants) {
            ProductVariant variant = productVariantRepository.save(ProductVariant.builder()
                    .product(product)
                    .sku(vs.sku)
                    .volumeMl(vs.volumeMl)
                    .concentration(vs.concentration)
                    .basePrice(vs.basePrice)
                    .salePrice(vs.salePrice)
                    .weightGrams(350)
                    .isActive(true)
                    .build());

            // Allocate inventory across both hubs
            int halfQty = vs.initialStock / 2;
            inventoryRecordRepository.save(InventoryRecord.builder()
                    .variant(variant)
                    .warehouse(mumbaiWh)
                    .quantityOnHand(halfQty + (vs.initialStock % 2))
                    .quantityReserved(0)
                    .lowStockThreshold(5)
                    .build());

            inventoryRecordRepository.save(InventoryRecord.builder()
                    .variant(variant)
                    .warehouse(delhiWh)
                    .quantityOnHand(halfQty)
                    .quantityReserved(0)
                    .lowStockThreshold(5)
                    .build());
        }
    }

    private record VariantSeed(
            String sku,
            int volumeMl,
            Concentration concentration,
            BigDecimal basePrice,
            BigDecimal salePrice,
            int initialStock
    ) {}
}
