package com.pdvapi.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<Product> findByTenantIdAndBarcodeAndActiveTrue(UUID tenantId, String barcode);

    boolean existsByTenantIdAndBarcodeAndActiveTrue(UUID tenantId, String barcode);

    Page<Product> findByTenantIdAndActiveTrue(UUID tenantId, Pageable pageable);

    Page<Product> findByTenantIdAndActiveTrueAndNameContainingIgnoreCase(UUID tenantId, String name, Pageable pageable);

    @Query("select coalesce(max(p.code), 0) from Product p where p.tenantId = :tenantId")
    int findMaxCodeByTenantId(@Param("tenantId") UUID tenantId);
}
