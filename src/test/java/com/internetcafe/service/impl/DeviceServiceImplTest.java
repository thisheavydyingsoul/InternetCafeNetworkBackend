package com.internetcafe.service.impl;

import com.internetcafe.dto.response.DeviceResponse;
import com.internetcafe.entity.Device;
import com.internetcafe.exception.ResourceNotFoundException;
import com.internetcafe.mapper.DeviceMapper;
import com.internetcafe.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceImplTest {

    @Mock DeviceRepository deviceRepository;
    @Mock DeviceMapper deviceMapper;

    @InjectMocks DeviceServiceImpl deviceService;

    @Test
    void getDeviceById_notFound() {
        when(deviceRepository.findById("x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deviceService.getDeviceById("x"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getDevicesByOffice_mapsAll() {
        Device d1 = new Device();
        d1.setId("device-001");
        DeviceResponse r1 = DeviceResponse.builder().id("device-001").build();

        when(deviceRepository.findByOfficeId("office-001")).thenReturn(List.of(d1));
        when(deviceMapper.toResponse(d1)).thenReturn(r1);

        List<DeviceResponse> result = deviceService.getDevicesByOffice("office-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("device-001");
    }
}
