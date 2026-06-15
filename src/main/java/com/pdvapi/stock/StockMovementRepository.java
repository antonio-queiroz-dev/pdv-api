package com.pdvapi.stock;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    Optional<StockMovement> findByIdAndTenantId(UUID id, UUID tenantId);

    @Query("select coalesce(sum(m.quantity), 0) from StockMovement m "
            + "where m.tenantId = :tenantId and m.productId = :productId")
    int currentStock(@Param("tenantId") UUID tenantId, @Param("productId") UUID productId);

    Page<StockMovement> findByTenantIdAndProductIdOrderByCreatedAtDesc(UUID tenantId, UUID productId, Pageable pageable);

    Optional<StockMovement> findFirstByTenantIdAndProductIdOrderByCreatedAtDesc(UUID tenantId, UUID productId);

    @Query(value = """
            select new com.pdvapi.stock.StockBalanceResponse(p.id, p.name, p.code, sum(m.quantity), max(m.createdAt))
            from StockMovement m join Product p on p.id = m.productId
            where m.tenantId = :tenantId and p.active = true
            group by p.id, p.name, p.code
            """,
            countQuery = """
                    select count(distinct p.id)
                    from StockMovement m join Product p on p.id = m.productId
                    where m.tenantId = :tenantId and p.active = true
                    """)
    Page<StockBalanceResponse> findBalances(@Param("tenantId") UUID tenantId, Pageable pageable);
}