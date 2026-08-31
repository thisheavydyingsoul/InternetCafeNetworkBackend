package com.internetcafe.mapper;

import com.internetcafe.dto.response.DeviceResponse;
import com.internetcafe.entity.Device;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DeviceMapper {

    @Mapping(target = "officeId", source = "office.id")
    @Mapping(target = "officeAddress", source = "office.address")
    @Mapping(target = "gameNames", source = "games", qualifiedByName = "mapGameNames")
    DeviceResponse toResponse(Device device);

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