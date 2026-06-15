package com.pdvapi.stock;

import java.util.List;

public record StockMovementsResponse(
        List<StockMovementResponse> movements
) {
}