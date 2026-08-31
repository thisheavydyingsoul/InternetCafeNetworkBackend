package com.internetcafe.controller;

import com.internetcafe.dto.response.DeviceResponse;
import com.internetcafe.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
@Tag(name = "Devices", description = "API для управления устройствами")
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    @Operation(summary = "Получить все устройства")
    public ResponseEntity<List<DeviceResponse>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить устройство по ID")
    public ResponseEntity<DeviceResponse> getDeviceById(@PathVariable String id) {
        return ResponseEntity.ok(deviceService.getDeviceById(id));
    }

    @GetMapping("/office/{officeId}")
    @Operation(summary = "Получить устройства по офису")
    public ResponseEntity<List<DeviceResponse>> getDevicesByOffice(@PathVariable String officeId) {
        return ResponseEntity.ok(deviceService.getDevicesByOffice(officeId));
    }

    @GetMapping("/office/{officeId}/available")
    @Operation(summary = "Получить доступные устройства в офисе")
    public ResponseEntity<List<DeviceResponse>> getAvailableDevicesByOffice(@PathVariable String officeId) {
        return ResponseEntity.ok(deviceService.getAvailableDevicesByOffice(officeId));
    }
}