package com.pdvapi.sale;

import com.pdvapi.common.InsufficientStockException;
import com.pdvapi.common.InvalidAmountTenderedException;
import com.pdvapi.common.InvalidDiscountException;
import com.pdvapi.common.PaymentMismatchException;
import com.pdvapi.common.ProductNotFoundException;
import com.pdvapi.common.SaleAlreadyCancelledException;
import com.pdvapi.common.SaleNotFoundException;
import com.pdvapi.config.TenantContext;
import com.pdvapi.product.Product;
import com.pdvapi.product.ProductRepository;
import com.pdvapi.stock.MovementType;
import com.pdvapi.stock.StockMovement;
import com.pdvapi.stock.StockMovementRepository;
import com.pdvapi.user.User;
import com.pdvapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;

    @Transactional
    public SaleResponse create(CreateSaleRequest request) {
        UUID tenantId = TenantContext.get();
        UUID operatorId = currentOperatorId();

        // 1. valida produtos, checa estoque e monta os itens
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<SaleItem> items = new ArrayList<>();

        //
        for (SaleItemRequest itemReq : request.items()) {
            Product product = productRepository.findByIdAndTenantId(itemReq.productId(), tenantId)
                    .filter(Product::isActive)
                    .orElseThrow(() -> new ProductNotFoundException(itemReq.productId()));

            int currentStock = stockMovementRepository.currentStock(tenantId, product.getId());
            if (currentStock < itemReq.quantity()) {
                throw new InsufficientStockException(product.getName(), currentStock);
            }

            //
            SaleItem item = SaleItem.create(product.getId(), product.getName(),
                    itemReq.quantity(), product.getSellingPrice());
            items.add(item);

            // calcula total
            totalAmount = totalAmount.add(item.getSubtotal());
        }

        // 2. valida desconto
        if (request.discount().compareTo(totalAmount) > 0) {
            throw new InvalidDiscountException(request.discount(), totalAmount);
        }

        BigDecimal finalAmount = totalAmount.subtract(request.discount());

        // 3. valida pagamentos
        BigDecimal paymentTotal = BigDecimal.ZERO;
        List<SalePayment> payments = new ArrayList<>();

        for (SalePaymentRequest payReq : request.payments()) {
            if (payReq.paymentMethod() == PaymentMethod.CASH
                    && payReq.amountTendered() != null
                    && payReq.amountTendered().compareTo(payReq.amount()) < 0) {
                throw new InvalidAmountTenderedException(payReq.amountTendered(), payReq.amount());
            }


            SalePayment payment = SalePayment.create(payReq.paymentMethod(), payReq.amount(), payReq.amountTendered());
            payments.add(payment);
            paymentTotal = paymentTotal.add(payReq.amount());
        }

        if (paymentTotal.compareTo(finalAmount) != 0) {
            throw new PaymentMismatchException(finalAmount, paymentTotal);
        }

        // 4. cria a venda com todos os dados
        int code = nextCode(tenantId);
        Sale sale = Sale.create(tenantId, code, totalAmount, request.discount(), operatorId);

        for (SaleItem item : items) {
            sale.addItem(item);
        }
        for (SalePayment payment : payments) {
            sale.addPayment(payment);
        }

        saleRepository.save(sale);

        // 5. cria movimentações de estoque (SALE, quantidade negativa)
        for (SaleItem item : sale.getItems()) {
            stockMovementRepository.save(StockMovement.create(
                    tenantId, item.getProductId(), MovementType.SALE,
                    -item.getQuantity(), "Sale #" + code, operatorId));
        }

        String operatorName = findOperatorName(operatorId);
        return SaleResponse.from(sale, operatorName);
    }

    @Transactional(readOnly = true)
    public SaleResponse findById(UUID saleId) {
        UUID tenantId = TenantContext.get();
        Sale sale = saleRepository.findByIdAndTenantId(saleId, tenantId)
                .orElseThrow(() -> new SaleNotFoundException(saleId));

        String operatorName = findOperatorName(sale.getOperatorId());
        return SaleResponse.from(sale, operatorName);
    }

    @Transactional(readOnly = true)
    public Page<SaleSummaryResponse> list(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        UUID tenantId = TenantContext.get();

        Page<Sale> page;
        if (startDate != null && endDate != null) {
            Instant start = startDate.atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant end = endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            page = saleRepository.findByTenantIdAndPeriod(tenantId, start, end, pageable);
        } else {
            page = saleRepository.findByTenantIdOrderByCreatedAtDesc(tenantId, pageable);
        }

        // recolhe todos os ids da página
        Set<UUID> operatorIds = page.getContent().stream()
                .map(Sale::getOperatorId)
                .collect(Collectors.toCollection(HashSet::new));

        // extrai o nome dos operadores
        Map<UUID, String> namesById = operatorIds.isEmpty()
                ? Map.of()
                : userRepository.findAllById(operatorIds).stream()
                        .collect(Collectors.toMap(User::getId, User::getName));

        return page.map(sale -> SaleSummaryResponse.from(sale, namesById.get(sale.getOperatorId())));
    }

    @Transactional
    public SaleCancelResponse cancel(UUID saleId) {
        UUID tenantId = TenantContext.get();
        UUID operatorId = currentOperatorId();

        Sale sale = saleRepository.findByIdAndTenantId(saleId, tenantId)
                .orElseThrow(() -> new SaleNotFoundException(saleId));

        if (sale.getStatus() == SaleStatus.CANCELLED) {
            throw new SaleAlreadyCancelledException(saleId);
        }

        sale.cancel();

        // estorna estoque: cria movimentações positivas pra cada item
        for (SaleItem item : sale.getItems()) {
            stockMovementRepository.save(StockMovement.create(
                    tenantId, item.getProductId(), MovementType.SALE,
                    item.getQuantity(), "Cancel sale #" + sale.getCode(), operatorId));
        }

        return SaleCancelResponse.from(sale);
    }

    private int nextCode(UUID tenantId) {
        return saleRepository.findMaxCodeByTenantId(tenantId) + 1;
    }

    private UUID currentOperatorId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private String findOperatorName(UUID operatorId) {
        return userRepository.findById(operatorId)
                .map(User::getName)
                .orElse("Unknown");
    }
}
