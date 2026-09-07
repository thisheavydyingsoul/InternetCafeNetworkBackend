package com.internetcafe.dto.request.office;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficeUpdateRequest {

    @NotBlank(message = "Address is required")
    private String address;
}
