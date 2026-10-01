package com.scentiva.modules.cms.service;

import com.scentiva.modules.cms.dto.BannerCreateRequest;
import com.scentiva.modules.cms.dto.BannerResponse;
import com.scentiva.modules.cms.model.BannerPlacement;

import java.util.List;

public interface BannerService {

    List<BannerResponse> getActiveBanners(BannerPlacement placement);

    List<BannerResponse> getAllBanners();

    BannerResponse createBanner(BannerCreateRequest request);

    void deleteBanner(Long id);
}
