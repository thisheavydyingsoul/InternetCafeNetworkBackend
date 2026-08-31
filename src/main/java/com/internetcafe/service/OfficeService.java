package com.internetcafe.service;

import com.internetcafe.dto.response.OfficeResponse;

import java.util.List;

public interface OfficeService {
    List<OfficeResponse> getAllOffices();
    OfficeResponse getOfficeById(String id);
}