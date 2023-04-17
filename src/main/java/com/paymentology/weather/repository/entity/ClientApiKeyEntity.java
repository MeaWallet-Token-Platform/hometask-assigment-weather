package com.paymentology.weather.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;

@Entity
@Table(name = "client_api_keys")
@Data
@Accessors(chain = true)
public class ClientApiKeyEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "revoked")
    private Boolean revoked;

    @Column(name = "created_at")
    private Instant createdAt;
}
