package com.pdvapi.common;

import java.util.UUID;

public class SaleAlreadyCancelledException extends RuntimeException {
    public SaleAlreadyCancelledException(UUID id) {
        super("Sale already cancelled: " + id);
    }
}
