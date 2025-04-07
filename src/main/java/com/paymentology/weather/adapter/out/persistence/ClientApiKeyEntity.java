package com.paymentology.weather.adapter.out.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("client_api_keys")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientApiKeyEntity {

    @Id
    private Long id;

    @Column("api_key")
    private String apiKey;

    @Column("revoked")
    private boolean revoked;

    @Column("created_at")
    private Instant createdAt;
}
