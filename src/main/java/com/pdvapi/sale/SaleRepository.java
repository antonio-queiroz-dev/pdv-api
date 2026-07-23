package com.pdvapi.sale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface SaleRepository extends JpaRepository<Sale, UUID> {

    Optional<Sale> findByIdAndTenantId(UUID id, UUID tenantId);

    @Query("select coalesce(max(s.code), 0) from Sale s where s.tenantId = :tenantId")
    int findMaxCodeByTenantId(@Param("tenantId") UUID tenantId);

    Page<Sale> findByTenantIdOrderByCreatedAtDesc(UUID tenantId, Pageable pageable);

    @Query("select s from Sale s where s.tenantId = :tenantId "
            + "and s.createdAt >= :start and s.createdAt < :end order by s.createdAt desc")
    Page<Sale> findByTenantIdAndPeriod(@Param("tenantId") UUID tenantId,
                                       @Param("start") Instant start,
                                       @Param("end") Instant end,
                                       Pageable pageable);
}
