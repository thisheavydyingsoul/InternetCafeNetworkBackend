package com.internetcafe.service.impl;

import com.internetcafe.dto.request.office.OfficeCreateRequest;
import com.internetcafe.dto.request.office.OfficeUpdateRequest;
import com.internetcafe.dto.response.OfficeResponse;
import com.internetcafe.entity.Office;
import com.internetcafe.mapper.OfficeMapper;
import com.internetcafe.repository.OfficeRepository;
import com.internetcafe.service.OfficeService;
import lombok.RequiredArgsConstructor;
import com.internetcafe.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfficeServiceImpl implements OfficeService {

    private final OfficeRepository officeRepository;
    private final OfficeMapper officeMapper;

    @Override
    public List<OfficeResponse> getAllOffices() {
        return officeRepository.findAll().stream()
                .map(officeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OfficeResponse getOfficeById(String id) {
        Office office = officeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Office not found with id: " + id));
        return officeMapper.toResponse(office);
    }

    @Override
    @Transactional
    public OfficeResponse create(OfficeCreateRequest request) {
        Office office = officeMapper.toEntity(request);
        return officeMapper.toResponse(officeRepository.save(office));
    }

    @Override
    @Transactional
    public OfficeResponse update(String id, OfficeUpdateRequest request) {
        Office office = getOfficeEntity(id);
        officeMapper.updateEntity(request, office);
        return officeMapper.toResponse(officeRepository.save(office));
    }

    private Office getOfficeEntity(String id) {
        return officeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Office", id));
    }
}