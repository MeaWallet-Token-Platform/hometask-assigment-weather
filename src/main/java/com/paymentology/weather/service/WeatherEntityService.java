package com.paymentology.weather.service;

import com.paymentology.weather.constant.TemperatureUnit;
import com.paymentology.weather.mapper.WeatherMapper;
import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.model.WeatherDto;
import com.paymentology.weather.repository.WeatherRepository;
import com.paymentology.weather.repository.entity.WeatherEntity;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;

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
        var entityOptional = repository.findById(id);

        if (entityOptional.isPresent()) {
            var entity = entityOptional.get();
            log.info(WEATHER_ENTITY_FOUND_WITH_ID + entity.getId());
            var responseDto = mapper.entityToDto(entity);
            return Optional.of(responseDto);
        }

        return Optional.empty();
    }

    @CacheEvict(value = "weatherCache"/*, key = "#requestDto.host"*/)
    public WeatherDto saveOrUpdate(WeatherDto requestDto) {
        var requestEntity = mapper.dtoToEntity(requestDto);
        var savedEntity = repository.save(requestEntity);
        log.info(WEATHER_ENTITY_SAVED_WITH_ID + savedEntity.getId());
        return mapper.entityToDto(savedEntity);
    }

    @Async
    public void saveOrUpdateAsync(WeatherDto weatherDto) {
        this.saveOrUpdate(weatherDto);
    }
}
