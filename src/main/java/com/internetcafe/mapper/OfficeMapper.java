package com.internetcafe.mapper;

import com.internetcafe.dto.response.OfficeResponse;
import com.internetcafe.entity.Office;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OfficeMapper {

    OfficeResponse toResponse(Office office);
}