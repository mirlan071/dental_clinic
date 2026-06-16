package com.dentalclinic.service;

import com.dentalclinic.controller.dto.service.DentalServiceCreateRequest;
import com.dentalclinic.controller.dto.service.DentalServiceResponse;
import com.dentalclinic.controller.dto.service.DentalServiceUpdateRequest;
import com.dentalclinic.domain.service.DentalService;
import com.dentalclinic.exception.ResourceNotFoundException;
import com.dentalclinic.mapper.DentalServiceMapper;
import com.dentalclinic.repository.DentalServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DentalServiceService {

    private final DentalServiceRepository dentalServiceRepository;
    private final DentalServiceMapper dentalServiceMapper;

    @Transactional
    @CacheEvict(value = "services", allEntries = true)
    public DentalServiceResponse create(DentalServiceCreateRequest request) {
        DentalService service = dentalServiceMapper.toEntity(request);
        DentalService saved = dentalServiceRepository.save(service);
        log.info("Dental service created: id={}, name={}", saved.getId(), saved.getName());
        return dentalServiceMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "services", key = "#id")
    public DentalServiceResponse getById(Long id) {
        DentalService service = dentalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dental service", id));
        return dentalServiceMapper.toResponse(service);
    }

    @Transactional(readOnly = true)
    public Page<DentalServiceResponse> getAll(Pageable pageable) {
        return dentalServiceRepository.findByActiveTrue(pageable).map(dentalServiceMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<DentalServiceResponse> search(String query, Pageable pageable) {
        return dentalServiceRepository.search(query, pageable).map(dentalServiceMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<DentalServiceResponse> getByCategory(String category, Pageable pageable) {
        return dentalServiceRepository.findByCategoryIgnoreCase(category, pageable).map(dentalServiceMapper::toResponse);
    }

    @Transactional
    @CachePut(value = "services", key = "#id")
    @CacheEvict(value = "services", allEntries = true)
    public DentalServiceResponse update(Long id, DentalServiceUpdateRequest request) {
        DentalService service = dentalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dental service", id));
        dentalServiceMapper.updateEntity(request, service);
        DentalService updated = dentalServiceRepository.save(service);
        log.info("Dental service updated: id={}", updated.getId());
        return dentalServiceMapper.toResponse(updated);
    }

    @Transactional
    @CacheEvict(value = "services", allEntries = true)
    public void delete(Long id) {
        DentalService service = dentalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dental service", id));
        service.setActive(false);
        dentalServiceRepository.save(service);
        log.info("Dental service deactivated: id={}", id);
    }
}
