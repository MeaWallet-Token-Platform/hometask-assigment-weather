package com.paymentology.weather.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "client_api_keys")
@Data
public class ClientApiKeyEntity {

    @Id
    @Column(name = "id")
    Long id;

    @Column(name = "api_key")
    String apiKey;

    @Column(name = "revoked")
    boolean revoked;

    @Column(name = "created_at")
    Instant createdAt;
}
