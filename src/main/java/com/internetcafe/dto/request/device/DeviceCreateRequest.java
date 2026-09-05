package com.internetcafe.dto.request.device;

import com.internetcafe.enums.DeviceCondition;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCreateRequest {

    @NotBlank(message = "Office id is required")
    private String officeId;

    @NotBlank(message = "Type is required")
    private String type;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Condition is required")
    private DeviceCondition condition;

    @NotNull(message = "Day rate is required")
    @DecimalMin(value = "0.01", message = "Day rate must be greater than zero")
    private BigDecimal dayRate;

    @NotNull(message = "Night rate is required")
    @DecimalMin(value = "0.01", message = "Night rate must be greater than zero")
    private BigDecimal nightRate;

    private String description;

    private String imageUrl;
}