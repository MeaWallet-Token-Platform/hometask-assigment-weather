package com.paymentology.weather.service;

import com.paymentology.weather.constant.TemperatureUnit;
import com.paymentology.weather.mapper.WeatherMapper;
import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.model.WeatherDto;
import com.paymentology.weather.repository.WeatherRepository;
import com.paymentology.weather.repository.entity.WeatherEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherEntityService {


    private static final String CLASS_NAME = WeatherEntity.class.getSimpleName();
    private static final String WEATHER_ENTITY_FOUND_WITH_ID = CLASS_NAME + " found with id: ";
    private static final String WEATHER_ENTITY_SAVED_WITH_ID = CLASS_NAME + " saved with id: ";


    private final WeatherRepository repository;
    private final WeatherMapper mapper;


    @Cacheable(value = "weatherCache", key = "#geoLocationDto.host + #unit")
    public Optional<WeatherDto> findByLocationAndUnit(GeoLocationDto geoLocationDto, TemperatureUnit unit) {
        var id = geoLocationDto.host() + unit;
        var weatherDtoOptional = repository.findDtoById(id);
        weatherDtoOptional.ifPresent(weatherDto -> log.info(WEATHER_ENTITY_FOUND_WITH_ID + weatherDto.host()));
        return weatherDtoOptional;
    }

    @Async
    @CacheEvict(value = "weatherCache", key = "#requestDto.host")
    public void saveUpdateAsync(WeatherDto requestDto) {
        var requestEntity = mapper.dtoToEntity(requestDto);
        var savedEntity = repository.save(requestEntity);
        log.info(WEATHER_ENTITY_SAVED_WITH_ID + savedEntity.getId());
    }

}
