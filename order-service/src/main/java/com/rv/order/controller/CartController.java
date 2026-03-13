package com.rv.order.controller;

import com.rv.order.dto.CartDto;
import com.rv.order.dto.CartItemRequestDto;
import com.rv.order.dto.CartCheckoutRequestDto;
import com.rv.order.dto.PurchaseOrderResponseDto;
import com.rv.order.response.ApiResponse;
import com.rv.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartDto>> getCart(@RequestParam String email) {
        CartDto cart = cartService.getCart(email);
        ApiResponse<CartDto> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMsg("Cart fetched successfully");
        response.setData(cart);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartDto>> addToCart(@RequestBody CartItemRequestDto requestDto) {
        CartDto cart = cartService.addItem(requestDto);
        ApiResponse<CartDto> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMsg("Item added to cart");
        response.setData(cart);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<CartDto>> updateCartItem(@RequestBody CartItemRequestDto requestDto) {
        CartDto cart = cartService.updateItem(requestDto);
        ApiResponse<CartDto> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMsg("Cart updated");
        response.setData(cart);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<CartDto>> removeItem(@RequestParam String email, @RequestParam Long productId) {
        CartDto cart = cartService.removeItem(email, productId);
        ApiResponse<CartDto> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMsg("Item removed from cart");
        response.setData(cart);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(@RequestParam String email) {
        cartService.clearCart(email);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMsg("Cart cleared");
        response.setData(null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<PurchaseOrderResponseDto>> checkout(@RequestBody CartCheckoutRequestDto checkoutRequest) throws Exception {
        PurchaseOrderResponseDto orderResponse = cartService.checkoutCart(checkoutRequest);
        ApiResponse<PurchaseOrderResponseDto> response = new ApiResponse<>();
        response.setStatus(200);
        response.setMsg("Cart checked out successfully");
        response.setData(orderResponse);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
