package com.pdvapi.category;

import com.pdvapi.common.CategoryNameAlreadyExistsException;
import com.pdvapi.common.CategoryNotFoundException;
import com.pdvapi.config.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        UUID tenantId = TenantContext.get();

        Optional<Category> existing = categoryRepository.findByTenantIdAndNameIgnoreCase(tenantId, request.name());
        if (existing.isPresent()) {
            Category category = existing.get();
            if (category.isActive()) {
                throw new CategoryNameAlreadyExistsException(request.name());
            }
            category.activate();
            return CategoryResponse.from(category);
        }

        int code = categoryRepository.findMaxCodeByTenantId(tenantId) + 1;
        Category category = categoryRepository.save(Category.create(tenantId, code, request.name()));
        return CategoryResponse.from(category);
    }

    @Transactional(readOnly = true)
    public List<CategorySummary> listActive() {
        UUID tenantId = TenantContext.get();
        return categoryRepository.findAllByTenantIdAndActiveTrueOrderByNameAsc(tenantId)
                .stream()
                .map(CategorySummary::from)
                .toList();
    }

    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest request) {
        UUID tenantId = TenantContext.get();
        Category category = categoryRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (!category.getName().equalsIgnoreCase(request.name())
                && categoryRepository.existsByTenantIdAndNameIgnoreCaseAndActiveTrue(tenantId, request.name())) {
            throw new CategoryNameAlreadyExistsException(request.name());
        }

        category.rename(request.name());
        return CategoryResponse.from(category);
    }

    @Transactional
    public CategoryResponse deactivate(UUID id) {
        UUID tenantId = TenantContext.get();
        Category category = categoryRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        category.deactivate();
        return CategoryResponse.from(category);
    }

    @Transactional
    public CategoryResponse activate(UUID id) {
        UUID tenantId = TenantContext.get();
        Category category = categoryRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (!category.isActive()
                && categoryRepository.existsByTenantIdAndNameIgnoreCaseAndActiveTrue(tenantId, category.getName())) {
            throw new CategoryNameAlreadyExistsException(category.getName());
        }

        category.activate();
        return CategoryResponse.from(category);
    }
}
