package com.scentiva.modules.ai.dto;

import com.scentiva.modules.catalog.model.GenderTarget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScentQuizRequest {
    private String fragranceFamily; // "Woody", "Floral", "Oriental", "Fresh", "Gourmand", "Citrus"
    private String occasion;        // "Date Night", "Daily Signature", "Evening Gala", "Office / Professional", "Summer Vacation"
    private String intensity;       // "Light & Subtle", "Balanced & Elegant", "Bold & Intense"
    private String budgetTier;      // "budget", "mid", "luxury", "ultra_luxury", "any"
    private GenderTarget gender;    // FOR_HER, FOR_HIM, UNISEX
    private List<String> preferredNotes; // e.g. ["Vanilla", "Bergamot", "Oud", "Sandalwood"]
}
