package com.pdvapi.unit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UnitRepository extends JpaRepository<Unit, UUID> {

    Optional<Unit> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<Unit> findByTenantIdAndNameIgnoreCase(UUID tenantId, String name);

    List<Unit> findAllByTenantIdAndActiveTrueOrderByNameAsc(UUID tenantId);

    List<Unit> findAllByTenantIdAndIdIn(UUID tenantId, Collection<UUID> ids);

    boolean existsByTenantIdAndNameIgnoreCaseAndActiveTrue(UUID tenantId, String name);

    @Query("select coalesce(max(u.code), 0) from Unit u where u.tenantId = :tenantId")
    int findMaxCodeByTenantId(@Param("tenantId") UUID tenantId);
}
