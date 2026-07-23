package com.pdvapi.sale;

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

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SaleResponse create(@Valid @RequestBody CreateSaleRequest request) {
        return saleService.create(request);
    }

    @GetMapping
    public Page<SaleSummaryResponse> list(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Pageable pageable) {
        return saleService.list(startDate, endDate, pageable);
    }

    @GetMapping("/{id}")
    public SaleResponse findById(@PathVariable UUID id) {
        return saleService.findById(id);
    }

    @PostMapping("/{id}/cancel")
    public SaleCancelResponse cancel(@PathVariable UUID id) {
        return saleService.cancel(id);
    }
}
