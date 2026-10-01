package com.scentiva.modules.promotion.service;

import com.scentiva.modules.promotion.dto.CampaignCreateRequest;
import com.scentiva.modules.promotion.dto.CampaignResponse;

import java.util.List;

public interface CampaignService {

    List<CampaignResponse> getActiveCampaigns();

    List<CampaignResponse> getAllCampaigns();

    CampaignResponse getCampaignBySlug(String slug);

    CampaignResponse createCampaign(CampaignCreateRequest request);

    void deleteCampaign(Long id);
}
