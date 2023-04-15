package com.paymentology.weather.service;

import com.paymentology.weather.mapper.GeoLocationMapper;
import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.repository.GeoLocationRepository;
import com.paymentology.weather.repository.entity.GeoLocationEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeoLocationEntityService {


    private static final String CLASS_NAME = GeoLocationEntity.class.getSimpleName();
    private static final String SAVED_WITH_ID = CLASS_NAME + " saved with id: ";
    private static final String FOUND_FOR_HOST = CLASS_NAME + " found for host: ";


    private final GeoLocationRepository repository;
    private final GeoLocationMapper mapper;


    @Cacheable(value = "geoLocationCache", key = "#host")
    public Optional<GeoLocationDto> findByHost(String host) {
        var dto = repository.findDtoByHost(host);
        dto.ifPresent(geoLocationDto -> log.info(FOUND_FOR_HOST + geoLocationDto.host()));
        return dto;
    }

    @CacheEvict(value = "geoLocationCache", key = "#requestDto.host")
    public GeoLocationDto saveOrUpdate(GeoLocationDto requestDto) {
        var requestEntity = mapper.dtoToEntity(requestDto);
        var savedEntity = repository.save(requestEntity);
        log.info(SAVED_WITH_ID + savedEntity.getId());
        return mapper.entityToDto(savedEntity);
    }

    @Async
    public void saveOrUpdateAsync(GeoLocationDto geoLocationDto) {
        this.saveOrUpdate(geoLocationDto);
    }
}
