package com.internetcafe.controller;

import com.internetcafe.dto.response.OfficeResponse;
import com.internetcafe.service.OfficeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/offices")
@RequiredArgsConstructor
@Tag(name = "Offices", description = "API для управления офисами")
public class OfficeController {

    private final OfficeService officeService;

    @GetMapping
    @Operation(summary = "Получить все офисы")
    public ResponseEntity<List<OfficeResponse>> getAllOffices() {
        return ResponseEntity.ok(officeService.getAllOffices());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить офис по ID")
    public ResponseEntity<OfficeResponse> getOfficeById(@PathVariable String id) {
        return ResponseEntity.ok(officeService.getOfficeById(id));
    }
}