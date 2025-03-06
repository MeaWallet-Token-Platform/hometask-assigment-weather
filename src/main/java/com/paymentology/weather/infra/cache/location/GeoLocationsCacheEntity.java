package com.paymentology.weather.infra.cache.location;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.net.InetAddress;
import java.time.Instant;

@Entity
@Table(name = "geo_locations_cache")
public class GeoLocationsCacheEntity {

    @Id
    @Column(name = "ip_address")
    private InetAddress ipAddress;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    @Column(nullable = false)
    private Instant expiresAt;

    protected GeoLocationsCacheEntity() {
    }

    public GeoLocationsCacheEntity(InetAddress ipAddress, Double latitude, Double longitude, Instant expiresAt) {
        this.ipAddress = ipAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.expiresAt = expiresAt;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
