
package com.incidentai.inventory.service;

import com.incidentai.inventory.dto.InventoryReservationRequest;
import com.incidentai.inventory.dto.InventoryReleaseRequest;
import com.incidentai.inventory.dto.InventoryResponse;
import com.incidentai.inventory.entity.Inventory;
import com.incidentai.inventory.exception.InsufficientInventoryException;
import com.incidentai.inventory.exception.InventoryNotFoundException;
import com.incidentai.inventory.repository.InventoryRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void shouldReserveInventorySuccessfully() {

        Inventory inventory = new Inventory(1L, 50);

        when(inventoryRepository.findByProductId(1L))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InventoryReservationRequest request =
                new InventoryReservationRequest(1L, 5);

        InventoryResponse response = inventoryService.reserve(request);

        assertEquals(45, response.availableQuantity());
        assertEquals(5, response.reservedQuantity());
    }

    @Test
    void shouldThrowExceptionWhenStockIsInsufficient() {

        Inventory inventory = new Inventory(1L, 5);

        when(inventoryRepository.findByProductId(1L))
                .thenReturn(Optional.of(inventory));

        InventoryReservationRequest request =
                new InventoryReservationRequest(1L, 10);

        assertThrows(
                InsufficientInventoryException.class,
                () -> inventoryService.reserve(request)
        );
    }

    @Test
    void shouldReturnInventoryForExistingProduct() {

        Inventory inventory = new Inventory(1L, 50);

        when(inventoryRepository.findByProductId(1L))
                .thenReturn(Optional.of(inventory));

        InventoryResponse response = inventoryService.getInventory(1L);

        assertEquals(1L, response.productId());
        assertEquals(50, response.availableQuantity());
        assertEquals(0, response.reservedQuantity());
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {

        when(inventoryRepository.findByProductId(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                InventoryNotFoundException.class,
                () -> inventoryService.getInventory(999L)
        );
    }

    @Test
    void shouldReleaseInventorySuccessfully() {

        Inventory inventory = new Inventory(1L, 50);

        // First reserve 5 units.
        inventory.reserve(5);

        when(inventoryRepository.findByProductId(1L))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InventoryReleaseRequest request =
                new InventoryReleaseRequest(1L, 5);

        InventoryResponse response = inventoryService.release(request);

        assertEquals(50, response.availableQuantity());
        assertEquals(0, response.reservedQuantity());
    }


    @Test
    void shouldThrowExceptionWhenReleasingMoreThanReserved() {

        Inventory inventory = new Inventory(1L, 50);

        inventory.reserve(3);

        when(inventoryRepository.findByProductId(1L))
                .thenReturn(Optional.of(inventory));

        InventoryReleaseRequest request =
                new InventoryReleaseRequest(1L, 5);

        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.release(request)
        );
    }
}
