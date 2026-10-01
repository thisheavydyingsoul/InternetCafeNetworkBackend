package com.internetcafe.service.impl;

import com.internetcafe.dto.request.office.OfficeCreateRequest;
import com.internetcafe.dto.request.office.OfficeUpdateRequest;
import com.internetcafe.dto.response.OfficeResponse;
import com.internetcafe.entity.Office;
import com.internetcafe.exception.ResourceNotFoundException;
import com.internetcafe.mapper.OfficeMapper;
import com.internetcafe.repository.OfficeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class OfficeServiceImplTest {

    @Mock OfficeRepository officeRepository;
    @Mock OfficeMapper officeMapper;

    @InjectMocks OfficeServiceImpl officeService;

    @Test
    void getOfficeById_notFound() {
        when(officeRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> officeService.getOfficeById("missing"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_persistsAndMaps() {
        OfficeCreateRequest request = OfficeCreateRequest.builder().address("New St").build();
        Office entity = new Office();
        entity.setAddress("New st");
        Office saved = new Office();
        saved.setId("office-new");
        saved.setAddress("New St");
        OfficeResponse response = OfficeResponse.builder().id("office-new").address("New St").build();

        when(officeMapper.toEntity(request)).thenReturn(entity);
        when(officeRepository.save(entity)).thenReturn(saved);
        when(officeMapper.toResponse(saved)).thenReturn(response);

        OfficeResponse result = officeService.create(request);

        assertThat(result.getId()).isEqualTo("office-new");
        assertThat(result.getAddress()).isEqualTo("New St");
    }

    @Test
    void update_appliesPatch() {
        Office existing = new Office();
        existing.setId("office-001");
        existing.setAddress("Old");
        OfficeUpdateRequest request = OfficeUpdateRequest.builder().address("New").build();
        OfficeResponse response = OfficeResponse.builder().id("office-001").address("New").build();

        when(officeRepository.findById("office-001")).thenReturn(Optional.of(existing));
        when(officeRepository.save(existing)).thenReturn(existing);
        when(officeMapper.toResponse(existing)).thenReturn(response);

        OfficeResponse result = officeService.update("office-001", request);

        verify(officeMapper).updateEntity(request, existing);
        assertThat(result.getAddress()).isEqualTo("New");
    }
}
