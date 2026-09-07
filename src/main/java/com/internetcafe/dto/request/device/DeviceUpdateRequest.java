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
public class DeviceUpdateRequest {

    private String officeId;

    private String type;

    private String name;

    private DeviceCondition condition;

    @DecimalMin(value = "0.01", message = "Day rate must be greater than zero")
    private BigDecimal dayRate;

    @DecimalMin(value = "0.01", message = "Night rate must be greater than zero")
    private BigDecimal nightRate;

    private String description;

    private String imageUrl;
}
