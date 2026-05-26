package com.pdvapi.unit;

import com.pdvapi.common.UnitNameAlreadyExistsException;
import com.pdvapi.common.UnitNotFoundException;
import com.pdvapi.config.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;

    @Transactional
    public UnitResponse create(UnitRequest request) {
        UUID tenantId = TenantContext.get();

        Optional<Unit> existing = unitRepository.findByTenantIdAndNameIgnoreCase(tenantId, request.name());
        if (existing.isPresent()) {
            Unit unit = existing.get();
            if (unit.isActive()) {
                throw new UnitNameAlreadyExistsException(request.name());
            }
            unit.activate();
            return UnitResponse.from(unit);
        }

        int code = unitRepository.findMaxCodeByTenantId(tenantId) + 1;
        Unit unit = unitRepository.save(Unit.create(tenantId, code, request.name()));
        return UnitResponse.from(unit);
    }

    @Transactional(readOnly = true)
    public List<UnitSummary> listActive() {
        UUID tenantId = TenantContext.get();
        return unitRepository.findAllByTenantIdAndActiveTrueOrderByNameAsc(tenantId)
                .stream()
                .map(UnitSummary::from)
                .toList();
    }

    @Transactional
    public UnitResponse update(UUID id, UnitRequest request) {
        UUID tenantId = TenantContext.get();
        Unit unit = unitRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new UnitNotFoundException(id));

        if (!unit.getName().equalsIgnoreCase(request.name())
                && unitRepository.existsByTenantIdAndNameIgnoreCaseAndActiveTrue(tenantId, request.name())) {
            throw new UnitNameAlreadyExistsException(request.name());
        }

        unit.rename(request.name());
        return UnitResponse.from(unit);
    }

    @Transactional
    public UnitResponse deactivate(UUID id) {
        UUID tenantId = TenantContext.get();
        Unit unit = unitRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new UnitNotFoundException(id));
        unit.deactivate();
        return UnitResponse.from(unit);
    }

    @Transactional
    public UnitResponse activate(UUID id) {
        UUID tenantId = TenantContext.get();
        Unit unit = unitRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new UnitNotFoundException(id));

        if (!unit.isActive()
                && unitRepository.existsByTenantIdAndNameIgnoreCaseAndActiveTrue(tenantId, unit.getName())) {
            throw new UnitNameAlreadyExistsException(unit.getName());
        }

        unit.activate();
        return UnitResponse.from(unit);
    }
}
