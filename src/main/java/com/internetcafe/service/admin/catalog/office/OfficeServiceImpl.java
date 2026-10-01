package com.internetcafe.service.admin.catalog.office;

import com.internetcafe.dto.request.office.OfficeCreateRequest;
import com.internetcafe.dto.request.office.OfficeUpdateRequest;
import com.internetcafe.dto.response.OfficeResponse;
import com.internetcafe.entity.Office;
import com.internetcafe.mapper.OfficeMapper;
import com.internetcafe.repository.OfficeRepository;
import lombok.RequiredArgsConstructor;
import com.internetcafe.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfficeServiceImpl implements OfficeService {

    private final OfficeRepository officeRepository;
    private final OfficeMapper officeMapper;

    @Override
    public List<OfficeResponse> getAllOffices() {
        List<OfficeResponse> list = officeRepository.findAll().stream()
                .map(officeMapper::toResponse)
                .collect(Collectors.toList());
        log.debug("Listing all offices count={}", list.size());
        return list;
    }

    @Override
    public OfficeResponse getOfficeById(String id) {
        Office office = officeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Office not found with id: " + id));
        log.debug("Getting office id={}", id);
        return officeMapper.toResponse(office);
    }

    @Override
    @Transactional
    public OfficeResponse create(OfficeCreateRequest request) {
        Office office = officeMapper.toEntity(request);
        log.debug("Creating office address={}", request.getAddress());
        return officeMapper.toResponse(officeRepository.save(office));
    }

    @Override
    @Transactional
    public OfficeResponse update(String id, OfficeUpdateRequest request) {
        Office office = getOfficeEntity(id);
        officeMapper.updateEntity(request, office);
        log.debug("Updating office id={}", id);
        return officeMapper.toResponse(officeRepository.save(office));
    }

    private Office getOfficeEntity(String id) {
        return officeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Office", id));
    }
}