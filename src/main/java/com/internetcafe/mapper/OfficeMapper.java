package com.internetcafe.mapper;

import com.internetcafe.dto.request.office.OfficeCreateRequest;
import com.internetcafe.dto.request.office.OfficeUpdateRequest;
import com.internetcafe.dto.response.OfficeResponse;
import com.internetcafe.entity.Office;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OfficeMapper {

    OfficeResponse toResponse(Office office);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "administrators", ignore = true)
    @Mapping(target = "devices", ignore = true)
    Office toEntity(OfficeCreateRequest request);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "administrators", ignore = true)
    @Mapping(target = "devices", ignore = true)
    void updateEntity(OfficeUpdateRequest request, @MappingTarget Office office);
}