package com.incidentai.inventory.controller;

import com.incidentai.inventory.dto.InventoryReleaseRequest;
import com.incidentai.inventory.dto.InventoryReservationRequest;
import com.incidentai.inventory.dto.InventoryResponse;
import com.incidentai.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventory(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                inventoryService.getInventory(productId)
        );
    }

    @PostMapping("/reserve")
    public ResponseEntity<InventoryResponse> reserve(
            @Valid @RequestBody InventoryReservationRequest request
    ) {
        return ResponseEntity.ok(
                inventoryService.reserve(request)
        );
    }

    @PostMapping("/release")
    public ResponseEntity<InventoryResponse> release(
            @Valid @RequestBody InventoryReleaseRequest request
    ) {
        return ResponseEntity.ok(
                inventoryService.release(request)
        );
    }
}