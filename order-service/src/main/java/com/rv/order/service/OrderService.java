package com.rv.order.service;

import com.rv.order.dto.OrderDto;
import com.rv.order.dto.PurchaseOrderRequestDto;
import com.rv.order.dto.PurchaseOrderResponseDto;
import com.rv.order.dto.UpdateOrderRequestDto;

import java.util.List;

public interface OrderService {

    public PurchaseOrderResponseDto createOrder(PurchaseOrderRequestDto orderRequestDto) throws Exception;

    public PurchaseOrderResponseDto updateOrder(UpdateOrderRequestDto updateOrderRequestDto)  throws  Exception;

    public PurchaseOrderResponseDto cancelOrder(String orderTrackingNumber)  throws  Exception;

    public List<OrderDto> getCustomerOrders(String customerEmail);

}



