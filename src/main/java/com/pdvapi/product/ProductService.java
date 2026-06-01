package com.pdvapi.product;

import com.pdvapi.category.Category;
import com.pdvapi.category.CategoryRepository;
import com.pdvapi.common.CategoryNotFoundException;
import com.pdvapi.common.ProductBarcodeAlreadyExistsException;
import com.pdvapi.common.ProductNotFoundException;
import com.pdvapi.common.UnitNotFoundException;
import com.pdvapi.config.TenantContext;
import com.pdvapi.unit.Unit;
import com.pdvapi.unit.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UnitRepository unitRepository;

    @Transactional
    public ProductResponse create(ProductRequest request) {
        UUID tenantId = TenantContext.get();

        Category category = resolveCategory(tenantId, request.categoryId());
        Unit unit = resolveUnit(tenantId, request.unitId());

        if (request.barcode() != null
                && productRepository.existsByTenantIdAndBarcodeAndActiveTrue(tenantId, request.barcode())) {
            throw new ProductBarcodeAlreadyExistsException(request.barcode());
        }

        int code = productRepository.findMaxCodeByTenantId(tenantId) + 1;
        Product product = productRepository.save(Product.create(
                tenantId, code, request.name(), request.description(), request.barcode(),
                request.sellingPrice(), request.costPrice(), request.categoryId(), request.unitId()
        ));
        return ProductResponse.from(product, category, unit);
    }

    @Transactional(readOnly = true)
    public Page<ProductSummary> list(Pageable pageable) {
        UUID tenantId = TenantContext.get();
        Page<Product> page = productRepository.findByTenantIdAndActiveTrue(tenantId, pageable);
        return toSummaries(tenantId, page);
    }

    @Transactional(readOnly = true)
    public Page<ProductSummary> search(String q, Pageable pageable) {
        UUID tenantId = TenantContext.get();
        Page<Product> page = productRepository
                .findByTenantIdAndActiveTrueAndNameContainingIgnoreCase(tenantId, q, pageable);
        return toSummaries(tenantId, page);
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(UUID id) {
        UUID tenantId = TenantContext.get();
        Product product = productRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return buildResponse(tenantId, product);
    }

    @Transactional(readOnly = true)
    public ProductResponse getByBarcode(String barcode) {
        UUID tenantId = TenantContext.get();
        Product product = productRepository.findByTenantIdAndBarcodeAndActiveTrue(tenantId, barcode)
                .orElseThrow(() -> new ProductNotFoundException(barcode));
        return buildResponse(tenantId, product);
    }

    @Transactional
    public ProductResponse update(UUID id, ProductRequest request) {
        UUID tenantId = TenantContext.get();
        Product product = productRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ProductNotFoundException(id));

        Category category = resolveCategory(tenantId, request.categoryId());
        Unit unit = resolveUnit(tenantId, request.unitId());

        if (request.barcode() != null
                && !request.barcode().equals(product.getBarcode())
                && productRepository.existsByTenantIdAndBarcodeAndActiveTrue(tenantId, request.barcode())) {
            throw new ProductBarcodeAlreadyExistsException(request.barcode());
        }

        product.update(request.name(), request.description(), request.barcode(),
                request.sellingPrice(), request.costPrice(), request.categoryId(), request.unitId());
        return ProductResponse.from(product, category, unit);
    }

    @Transactional
    public void delete(UUID id) {
        UUID tenantId = TenantContext.get();
        Product product = productRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.delete(product);
    }

    @Transactional
    public ProductResponse deactivate(UUID id) {
        UUID tenantId = TenantContext.get();
        Product product = productRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.deactivate();
        return buildResponse(tenantId, product);
    }

    @Transactional
    public ProductResponse activate(UUID id) {
        UUID tenantId = TenantContext.get();
        Product product = productRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (!product.isActive()
                && product.getBarcode() != null
                && productRepository.existsByTenantIdAndBarcodeAndActiveTrue(tenantId, product.getBarcode())) {
            throw new ProductBarcodeAlreadyExistsException(product.getBarcode());
        }

        product.activate();
        return buildResponse(tenantId, product);
    }

    //
    private Category resolveCategory(UUID tenantId, UUID categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findByIdAndTenantId(categoryId, tenantId)
                .filter(Category::isActive)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    private Unit resolveUnit(UUID tenantId, UUID unitId) {
        if (unitId == null) {
            return null;
        }
        return unitRepository.findByIdAndTenantId(unitId, tenantId)
                .filter(Unit::isActive)
                .orElseThrow(() -> new UnitNotFoundException(unitId));
    }

    private ProductResponse buildResponse(UUID tenantId, Product product) {
        Category category = product.getCategoryId() == null
                ? null
                : categoryRepository.findByIdAndTenantId(product.getCategoryId(), tenantId).orElse(null);
        Unit unit = product.getUnitId() == null
                ? null
                : unitRepository.findByIdAndTenantId(product.getUnitId(), tenantId).orElse(null);
        return ProductResponse.from(product, category, unit);
    }

    private Page<ProductSummary> toSummaries(UUID tenantId, Page<Product> page) {
        Set<UUID> categoryIds = page.getContent().stream()
                .map(Product::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));
        Set<UUID> unitIds = page.getContent().stream()
                .map(Product::getUnitId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(HashSet::new));

        Map<UUID, Category> categoriesById = categoryIds.isEmpty()
                ? Map.of()
                : categoryRepository.findAllByTenantIdAndIdIn(tenantId, categoryIds).stream()
                        .collect(Collectors.toMap(Category::getId, Function.identity()));
        Map<UUID, Unit> unitsById = unitIds.isEmpty()
                ? Map.of()
                : unitRepository.findAllByTenantIdAndIdIn(tenantId, unitIds).stream()
                        .collect(Collectors.toMap(Unit::getId, Function.identity()));

        return page.map(product -> ProductSummary.from(
                product,
                product.getCategoryId() == null ? null : categoriesById.get(product.getCategoryId()),
                product.getUnitId() == null ? null : unitsById.get(product.getUnitId())
        ));
    }
}
