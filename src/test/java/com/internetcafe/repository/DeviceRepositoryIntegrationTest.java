package com.internetcafe.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.internetcafe.entity.Device;
import com.internetcafe.enums.DeviceCondition;
import com.internetcafe.support.AbstractIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeviceRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private DeviceRepository deviceRepository;

    @Test
    void findsSeededDeviceAfterFlywayMigrations() {
        Device device = deviceRepository.findById("device-001")
                .orElseThrow();

        assertThat(device.getName()).isEqualTo("PlayStation 5");
        assertThat(device.getType()).isEqualTo("PS5");
        assertThat(device.getCondition()).isEqualTo(DeviceCondition.WORKING);
    }

    @Test
    void findsWorkingDevicesForSeededOffice() {
        List<Device> devices =
                deviceRepository.findAvailableDevicesByOffice("office-001");

        assertThat(devices)
                .extracting(Device::getId)
                .contains("device-001");
    }
}