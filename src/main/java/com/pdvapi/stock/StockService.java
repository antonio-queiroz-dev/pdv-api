package com.pdvapi.stock;

import com.pdvapi.common.InsufficientStockException;
import com.pdvapi.common.InvalidMovementTypeException;
import com.pdvapi.common.MovementNotCancellableException;
import com.pdvapi.common.MovementNotFoundException;
import com.pdvapi.common.ProductNotFoundException;
import com.pdvapi.config.TenantContext;
import com.pdvapi.product.Product;
import com.pdvapi.product.ProductRepository;
import com.pdvapi.user.User;
import com.pdvapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private static final Set<MovementType> IN_TYPES = EnumSet.of(MovementType.PURCHASE, MovementType.ADJUSTMENT);
    private static final Set<MovementType> OUT_TYPES = EnumSet.of(MovementType.LOSS, MovementType.ADJUSTMENT);

    private final StockMovementRepository movementRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public StockMovementsResponse registerIn(StockEntryRequest request) {
        // verifica se o tipo de movimentação é válido.
        if (!IN_TYPES.contains(request.type())) {
            throw new InvalidMovementTypeException(request.type());
        }

        UUID tenantId = TenantContext.get();
        UUID operatorId = currentOperatorId();

        //
        List<StockMovementResponse> movements = new ArrayList<>();
        for (StockItemRequest item : request.items()) {
            // verifica se o produto existe e está ativo.
            Product product = resolveActiveProduct(tenantId, item.productId());
            // cria uma movimentação de estoque
            StockMovement movement = movementRepository.save(StockMovement.create(
                    tenantId, product.getId(), request.type(), item.quantity(), request.note(), operatorId));
            movements.add(StockMovementResponse.from(movement, product.getName()));
        }
        return new StockMovementsResponse(movements);
    }

    @Transactional
    public StockMovementsResponse registerOut(StockEntryRequest request) {
        // verifica se o tipo de movimentação é válido.
        if (!OUT_TYPES.contains(request.type())) {
            throw new InvalidMovementTypeException(request.type());
        }
        UUID tenantId = TenantContext.get();
        UUID operatorId = currentOperatorId();

        //
        List<StockMovementResponse> movements = new ArrayList<>();
        for (StockItemRequest item : request.items()) {
            // verifica se o produto existe e está ativo.
            Product product = resolveActiveProduct(tenantId, item.productId());


            int currentStock = movementRepository.currentStock(tenantId, product.getId());
            // verifica se há estoque suficiente.
            if (currentStock < item.quantity()) {
                throw new InsufficientStockException(product.getName(), currentStock);
            }

            StockMovement movement = movementRepository.save(StockMovement.create(
                    tenantId, product.getId(), request.type(), -item.quantity(), request.note(), operatorId));
            movements.add(StockMovementResponse.from(movement, product.getName()));
        }
        return new StockMovementsResponse(movements);
    }

    @Transactional
    public CancelResponse cancel(UUID movementId) {
        UUID tenantId = TenantContext.get();
        // armazena a movimentação original e se não existir lança uma exception
        StockMovement original = movementRepository.findByIdAndTenantId(movementId, tenantId)
                .orElseThrow(() -> new MovementNotFoundException(movementId));

        // verifica se a movimentação já foi cancelada
        if (original.isCancelled()) {
            throw new MovementNotCancellableException("Movement already cancelled: " + movementId);
        }
        // verifica se o tipo de movimentação é cancelado
        if (original.getType() == MovementType.CANCELLATION) {
            throw new MovementNotCancellableException("A cancellation movement cannot be cancelled: " + movementId);
        }

        // reverte a quantidade da movimentação se for positivo fica negativo e vice-versa.
        int reversedQuantity = -original.getQuantity();
        int currentStock = movementRepository.currentStock(tenantId, original.getProductId());
        // verifica se há estoque suficiente para reverter a quantidade
        if (currentStock + reversedQuantity < 0) {
            // lança exception com o nome do produto e a quantidade atual.
            Product product = resolveProduct(tenantId, original.getProductId());
            throw new InsufficientStockException(product.getName(), currentStock);
        }

        original.markCancelled();
        // cria uma movimentação de reversão.
        StockMovement reversal = movementRepository.save(StockMovement.reversal(
                tenantId, original.getProductId(), original.getId(), reversedQuantity, currentOperatorId()));

        return CancelResponse.of(original, reversal);
    }

    //
    @Transactional(readOnly = true)
    public StockBalanceResponse getBalance(UUID productId) {
        UUID tenantId = TenantContext.get();
        Product product = resolveProduct(tenantId, productId);

        int currentStock = movementRepository.currentStock(tenantId, productId);
        Instant lastMovementAt = movementRepository
                .findFirstByTenantIdAndProductIdOrderByCreatedAtDesc(tenantId, productId)
                .map(StockMovement::getCreatedAt)
                .orElse(null);

        return new StockBalanceResponse(product.getId(), product.getName(), product.getCode(),
                currentStock, lastMovementAt);
    }

    @Transactional(readOnly = true)
    public Page<StockBalanceResponse> listBalances(Pageable pageable) {
        UUID tenantId = TenantContext.get();
        return movementRepository.findBalances(tenantId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MovementHistoryResponse> history(UUID productId, Pageable pageable) {
        UUID tenantId = TenantContext.get();
        resolveProduct(tenantId, productId);

        // busca as movimentações do produto.
        Page<StockMovement> page = movementRepository
                .findByTenantIdAndProductIdOrderByCreatedAtDesc(tenantId, productId, pageable);

        // busca os IDs dos operadores
        Set<UUID> operatorIds = page.getContent().stream()
                .map(StockMovement::getOperatorId)
                .collect(Collectors.toCollection(HashSet::new));

        // busca os nomes dos operadores.
        Map<UUID, String> namesById = operatorIds.isEmpty()
                ? Map.of()
                : userRepository.findAllById(operatorIds).stream()
                        .collect(Collectors.toMap(User::getId, User::getName));

        return page.map(movement ->
                MovementHistoryResponse.from(movement, namesById.get(movement.getOperatorId())));
    }

    //
    private Product resolveActiveProduct(UUID tenantId, UUID productId) {
        return productRepository.findByIdAndTenantId(productId, tenantId)
                .filter(Product::isActive)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private Product resolveProduct(UUID tenantId, UUID productId) {
        return productRepository.findByIdAndTenantId(productId, tenantId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private UUID currentOperatorId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}