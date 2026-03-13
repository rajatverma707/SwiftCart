package com.rv.order.service;

import com.rv.order.dto.OrderItemDto;
import com.rv.order.dto.inventory.InventoryCheckItem;
import com.rv.order.dto.inventory.InventoryCheckRequest;
import com.rv.order.dto.inventory.InventoryCheckResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InventoryClient {

    private final RestTemplate restTemplate;

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    public InventoryClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public InventoryCheckResponse checkAvailability(List<OrderItemDto> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be null or empty");
        }
        InventoryCheckRequest request = new InventoryCheckRequest();
        request.setItems(items.stream().map(this::toCheckItem).collect(Collectors.toList()));
        return restTemplate.postForObject(
                inventoryServiceUrl + "/api/v1/inventory/check",
                request,
                InventoryCheckResponse.class
        );
    }

    private InventoryCheckItem toCheckItem(OrderItemDto itemDto) {
        if (itemDto.getProductId() == null) {
            throw new IllegalArgumentException("productId is required for inventory check");
        }
        InventoryCheckItem item = new InventoryCheckItem();
        item.setProductId(itemDto.getProductId());
        item.setQuantity(itemDto.getQuantity());
        return item;
    }
}
