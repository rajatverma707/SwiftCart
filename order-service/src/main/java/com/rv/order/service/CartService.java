package com.rv.order.service;

import com.rv.order.dto.CartDto;
import com.rv.order.dto.CartItemRequestDto;
import com.rv.order.dto.CartCheckoutRequestDto;
import com.rv.order.dto.PurchaseOrderResponseDto;

public interface CartService {

    CartDto getCart(String customerEmail);

    CartDto addItem(CartItemRequestDto requestDto);

    CartDto updateItem(CartItemRequestDto requestDto);

    CartDto removeItem(String customerEmail, Long productId);

    void clearCart(String customerEmail);

    PurchaseOrderResponseDto checkoutCart(CartCheckoutRequestDto checkoutRequest) throws Exception;
}
