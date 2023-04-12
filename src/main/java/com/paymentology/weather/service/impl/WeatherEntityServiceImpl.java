package com.paymentology.weather.service.impl;

import com.paymentology.weather.constant.TemperatureUnit;
import com.paymentology.weather.mapper.WeatherMapper;
import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.model.WeatherDto;
import com.paymentology.weather.repository.WeatherRepository;
import com.paymentology.weather.repository.entity.WeatherEntity;
import com.paymentology.weather.service.WeatherEntityService;
import com.paymentology.weather.util.WeatherUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherEntityServiceImpl implements WeatherEntityService {

    private static final String CLASS_NAME = WeatherEntity.class.getSimpleName();
    private static final String WEATHER_ENTITY_FOUND_WITH_ID = CLASS_NAME + " found with id: ";
    private static final String WEATHER_ENTITY_SAVED_WITH_ID = CLASS_NAME + " saved with id: ";
    private final WeatherRepository repository;
    private final WeatherMapper mapper;


    @Override
    public Optional<WeatherDto> findByLocationAndUnit(GeoLocationDto geoLocationDto, TemperatureUnit unit) {
        var id = geoLocationDto.host() + unit;
        var entityOptional = repository.findById(id);

        if (entityOptional.isPresent()) {
            var entity = entityOptional.get();
            log.info(WEATHER_ENTITY_FOUND_WITH_ID + entity.getId());
            var responseDto = mapper.entityToDto(entity);
            return Optional.of(responseDto);
        }

        return Optional.empty();
    }

    @Override
    public WeatherDto save(WeatherDto requestDto) {
        var requestEntity = mapper.dtoToEntity(requestDto);
        var savedEntity = repository.save(requestEntity);
        log.info(WEATHER_ENTITY_SAVED_WITH_ID + savedEntity.getId());
        return mapper.entityToDto(savedEntity);
    }
}