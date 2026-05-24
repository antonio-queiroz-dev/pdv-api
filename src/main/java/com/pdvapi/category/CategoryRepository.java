package com.pdvapi.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<Category> findByTenantIdAndNameIgnoreCase(UUID tenantId, String name);

    List<Category> findAllByTenantIdAndActiveTrueOrderByNameAsc(UUID tenantId);

    List<Category> findAllByTenantIdAndIdIn(UUID tenantId, Collection<UUID> ids);

    boolean existsByTenantIdAndNameIgnoreCaseAndActiveTrue(UUID tenantId, String name);

    @Query("select coalesce(max(c.code), 0) from Category c where c.tenantId = :tenantId")
    int findMaxCodeByTenantId(@Param("tenantId") UUID tenantId);
}
