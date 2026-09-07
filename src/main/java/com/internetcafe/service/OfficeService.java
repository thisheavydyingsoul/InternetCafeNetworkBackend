package com.internetcafe.service;

import com.internetcafe.dto.request.office.OfficeCreateRequest;
import com.internetcafe.dto.request.office.OfficeUpdateRequest;
import com.internetcafe.dto.response.OfficeResponse;

import java.util.List;

public interface OfficeService {

    List<OfficeResponse> getAllOffices();

    OfficeResponse getOfficeById(String id);

    OfficeResponse create(OfficeCreateRequest request);

    OfficeResponse update(String id, OfficeUpdateRequest request);
}