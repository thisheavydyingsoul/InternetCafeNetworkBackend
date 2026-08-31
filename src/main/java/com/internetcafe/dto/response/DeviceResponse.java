package com.internetcafe.dto.response;

import com.internetcafe.enums.DeviceCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResponse {
    private String id;
    private String type;
    private String name;
    private DeviceCondition condition;
    private BigDecimal dayRate;
    private BigDecimal nightRate;
    private String description;
    private String imageUrl;
    private String officeId;
    private String officeAddress;
    private Set<String> gameNames;
}
