package com.pdvapi.stock;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping("/in")
    @ResponseStatus(HttpStatus.CREATED)
    public StockMovementsResponse registerIn(@Valid @RequestBody StockEntryRequest request) {
        return stockService.registerIn(request);
    }

    @PostMapping("/out")
    @ResponseStatus(HttpStatus.CREATED)
    public StockMovementsResponse registerOut(@Valid @RequestBody StockEntryRequest request) {
        return stockService.registerOut(request);
    }

    @PostMapping("/movements/{id}/cancel")
    public CancelResponse cancel(@PathVariable UUID id) {
        return stockService.cancel(id);
    }

    @GetMapping("/products/{productId}")
    public StockBalanceResponse getBalance(@PathVariable UUID productId) {
        return stockService.getBalance(productId);
    }

    @GetMapping("/products")
    public Page<StockBalanceResponse> listBalances(Pageable pageable) {
        return stockService.listBalances(pageable);
    }

    @GetMapping("/movements")
    public Page<MovementHistoryResponse> history(@RequestParam UUID productId, Pageable pageable) {
        return stockService.history(productId, pageable);
    }
}