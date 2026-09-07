package com.internetcafe.mapper;

import com.internetcafe.dto.request.device.DeviceCreateRequest;
import com.internetcafe.dto.request.device.DeviceUpdateRequest;
import com.internetcafe.dto.response.DeviceResponse;
import com.internetcafe.entity.Device;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DeviceMapper {

    @Mapping(target = "officeId", source = "office.id")
    @Mapping(target = "officeAddress", source = "office.address")
    @Mapping(target = "gameNames", source = "games", qualifiedByName = "mapGameNames")
    DeviceResponse toResponse(Device device);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "office", ignore = true)
    @Mapping(target = "games", ignore = true)
    @Mapping(target = "rents", ignore = true)
    Device toEntity(DeviceCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "office", ignore = true)
    @Mapping(target = "games", ignore = true)
    @Mapping(target = "rents", ignore = true)
    void updateEntity(DeviceUpdateRequest request, @MappingTarget Device device);

    @Named("mapGameNames")
    default Set<String> mapGameNames(Set<com.internetcafe.entity.Game> games) {
        if (games == null) {
            return Set.of();
        }
        return games.stream()
                .map(com.internetcafe.entity.Game::getName)
                .collect(Collectors.toSet());
    }
}