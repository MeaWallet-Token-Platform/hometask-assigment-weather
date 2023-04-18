package com.paymentology.weather.repository;

import com.paymentology.weather.model.GeoLocationDto;
import com.paymentology.weather.repository.entity.GeoLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeoLocationRepository extends JpaRepository<GeoLocationEntity, String> {

    @Query("""
            SELECT new com.paymentology.weather.model.GeoLocationDto(l.id, l.latitude, l.longitude)
            FROM GeoLocationEntity l
            WHERE l.id = :id
            """)
    Optional<GeoLocationDto> findDtoByHost(@Param("id") String host);

}
