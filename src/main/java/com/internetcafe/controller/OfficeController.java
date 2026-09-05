package com.internetcafe.controller;

import com.internetcafe.dto.request.office.OfficeCreateRequest;
import com.internetcafe.dto.request.office.OfficeUpdateRequest;
import com.internetcafe.dto.response.OfficeResponse;
import com.internetcafe.service.OfficeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offices")
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

    @PostMapping
    @Operation(summary = "Создать офис")
    public ResponseEntity<OfficeResponse> createOffice(@Valid @RequestBody OfficeCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(officeService.create(request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Обновить офис")
    public ResponseEntity<OfficeResponse> updateOffice(
            @PathVariable String id,
            @Valid @RequestBody OfficeUpdateRequest request
            ) {
        return ResponseEntity.ok(officeService.update(id, request));
    }


}