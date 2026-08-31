package com.internetcafe.service.impl;

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
}