package com.incidentai.inventory.service;

import com.incidentai.inventory.dto.InventoryReleaseRequest;
import com.incidentai.inventory.dto.InventoryReservationRequest;
import com.incidentai.inventory.dto.InventoryResponse;
import com.incidentai.inventory.entity.Inventory;
import com.incidentai.inventory.exception.InsufficientInventoryException;
import com.incidentai.inventory.exception.InventoryNotFoundException;
import com.incidentai.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventory(Long productId) {

        Inventory inventory = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(productId));

        return toResponse(inventory);
    }

    @Transactional
    public InventoryResponse reserve(InventoryReservationRequest request) {

        Inventory inventory = inventoryRepository
                .findByProductId(request.productId())
                .orElseThrow(
                        () -> new InventoryNotFoundException(request.productId())
                );

        if (inventory.getAvailableQuantity() < request.quantity()) {
            throw new InsufficientInventoryException(
                    request.productId(),
                    request.quantity(),
                    inventory.getAvailableQuantity()
            );
        }

        inventory.reserve(request.quantity());

        Inventory savedInventory = inventoryRepository.save(inventory);

        return toResponse(savedInventory);
    }

    @Transactional
    public InventoryResponse release(InventoryReleaseRequest request) {

        Inventory inventory = inventoryRepository
                .findByProductId(request.productId())
                .orElseThrow(
                        () -> new InventoryNotFoundException(request.productId())
                );

        if (inventory.getReservedQuantity() < request.quantity()) {
            throw new IllegalArgumentException(
                    "Cannot release more inventory than currently reserved"
            );
        }

        inventory.release(request.quantity());

        Inventory savedInventory = inventoryRepository.save(inventory);

        return toResponse(savedInventory);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getProductId(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity()
        );
    }
}