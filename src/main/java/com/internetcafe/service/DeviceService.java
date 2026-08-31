package com.internetcafe.service;

import com.internetcafe.dto.response.DeviceResponse;

import java.util.List;

public interface DeviceService {
    List<DeviceResponse> getAllDevices();
    DeviceResponse getDeviceById(String id);
    List<DeviceResponse> getDevicesByOffice(String officeId);
    List<DeviceResponse> getAvailableDevicesByOffice(String officeId);
}