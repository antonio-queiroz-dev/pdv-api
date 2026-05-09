package com.pdvapi.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("select coalesce(max(u.code), 0) from User u where u.tenantId = :tenantId")
    int findMaxCodeByTenantId(@Param("tenantId") UUID tenantId);
}
