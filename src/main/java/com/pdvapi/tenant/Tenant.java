package com.pdvapi.tenant;

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
@Table(name = "tenant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tenant {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 60)
    private String segment;

    @Column(nullable = false, length = 20)
    private String plan;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private Tenant(UUID id, String name, String plan, Instant now) {
        this.id = id;
        this.name = name;
        this.plan = plan;
        this.active = true;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static Tenant create(String name) {
        return new Tenant(UUID.randomUUID(), name, "FREE", Instant.now());
    }
}
