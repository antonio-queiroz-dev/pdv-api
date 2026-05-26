package com.pdvapi.unit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "unit")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Unit {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private int code;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private Unit(UUID id, UUID tenantId, int code, String name, Instant now) {
        this.id = id;
        this.tenantId = tenantId;
        this.code = code;
        this.name = name;
        this.active = true;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static Unit create(UUID tenantId, int code, String name) {
        return new Unit(UUID.randomUUID(), tenantId, code, name, Instant.now());
    }

    public void rename(String name) {
        this.name = name;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }
}
