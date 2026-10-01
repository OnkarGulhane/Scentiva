package com.scentiva.modules.admin.service;

import com.scentiva.modules.admin.dto.AdminSettingsResponse;
import com.scentiva.modules.admin.dto.AdminSettingsUpdateRequest;

public interface AdminSettingsService {

    AdminSettingsResponse getSettings();

    AdminSettingsResponse updateSettings(AdminSettingsUpdateRequest request);
}
