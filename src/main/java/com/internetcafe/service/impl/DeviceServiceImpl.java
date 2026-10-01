package com.internetcafe.service.impl;

import com.internetcafe.dto.request.device.DeviceCreateRequest;
import com.internetcafe.dto.request.device.DeviceUpdateRequest;
import com.internetcafe.dto.response.DeviceResponse;
import com.internetcafe.entity.Device;
import com.internetcafe.entity.Office;
import com.internetcafe.exception.ResourceNotFoundException;
import com.internetcafe.mapper.DeviceMapper;
import com.internetcafe.repository.DeviceRepository;
import com.internetcafe.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;

    @Override
    public List<DeviceResponse> getAllDevices() {
        return deviceRepository.findAll().stream()
                .map(deviceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DeviceResponse getDeviceById(String id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Device not found={}", id);
                    return new ResourceNotFoundException("Device not found with id: " + id);
                });
        return deviceMapper.toResponse(device);
    }

    @Override
    public List<DeviceResponse> getDevicesByOffice(String officeId) {
        List<DeviceResponse> list = deviceRepository.findByOfficeId(officeId).stream()
                .map(deviceMapper::toResponse)
                .collect(Collectors.toList());
        log.debug("Listing devices officeId={} count={}", officeId, list.size());
        return list;
    }

    @Override
    public List<DeviceResponse> getAvailableDevicesByOffice(String officeId) {
        List<DeviceResponse> list = deviceRepository.findAvailableDevicesByOffice(officeId).stream()
                .map(deviceMapper::toResponse)
                .collect(Collectors.toList());
        log.debug("Listing active devices officeId={} count={}", officeId, list.size());
        return list;
    }

    @Override
    public List<DeviceResponse> getAvailableDevicesByOfficeAndType(String officeId, String typeId) {
        List<DeviceResponse> list = deviceRepository.findAvailableDevicesByOfficeAndType(officeId, typeId).stream()
                .map(deviceMapper::toResponse)
                .collect(Collectors.toList());
        log.debug("Listing devices officeId={} typeId={} count={}", officeId, typeId, list.size());
        return list;
    }

    private Device getDeviceWithDetails(String id) {
        Device device =  deviceRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device", id));
        log.debug("Getting device with details deviceId={}", id);
        return device;
    }
}