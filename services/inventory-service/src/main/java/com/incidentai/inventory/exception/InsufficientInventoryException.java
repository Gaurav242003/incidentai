package com.incidentai.inventory.exception;

public class InsufficientInventoryException extends RuntimeException {

    public InsufficientInventoryException(Long productId, int requested, int available) {
        super(
            "Insufficient inventory for productId: " + productId
                + ". Requested: " + requested
                + ", Available: " + available
        );
    }
}