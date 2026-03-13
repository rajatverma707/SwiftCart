package com.rv.order.service;

import com.rv.order.dto.*;
import com.rv.order.entity.CartEntity;
import com.rv.order.entity.CartItemEntity;
import com.rv.order.entity.ProductEntity;
import com.rv.order.repo.CartItemRepo;
import com.rv.order.repo.CartRepo;
import com.rv.order.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private OrderService orderService;

    @Override
    public CartDto getCart(String customerEmail) {
        CartEntity cart = cartRepo.findByCustomerEmail(customerEmail);
        if (cart == null) {
            return buildEmptyCart(customerEmail);
        }
        List<CartItemEntity> items = cartItemRepo.findByCartCartId(cart.getCartId());
        return mapToDto(cart, items);
    }

    @Override
    public CartDto addItem(CartItemRequestDto requestDto) {
        CartEntity cart = getOrCreateCart(requestDto.getCustomerEmail());
        CartItemEntity item = cartItemRepo.findByCartCartIdAndProductId(cart.getCartId(), requestDto.getProductId());

        if (item == null) {
            ProductEntity product = productRepo.findById(requestDto.getProductId()).orElse(null);
            if (product == null) {
                throw new IllegalArgumentException("Product not found: " + requestDto.getProductId());
            }
            item = new CartItemEntity();
            item.setCart(cart);
            item.setProductId(requestDto.getProductId());
            item.setProductName(product.getName());
            item.setUnitPrice(product.getUnitPrice());
            item.setQuantity(requestDto.getQuantity() != null ? requestDto.getQuantity() : 1);
        } else {
            int qty = item.getQuantity() != null ? item.getQuantity() : 0;
            int delta = requestDto.getQuantity() != null ? requestDto.getQuantity() : 1;
            item.setQuantity(qty + delta);
        }

        cartItemRepo.save(item);
        recalculateCartTotals(cart);
        cartRepo.save(cart);

        List<CartItemEntity> items = cartItemRepo.findByCartCartId(cart.getCartId());
        return mapToDto(cart, items);
    }

    @Override
    public CartDto updateItem(CartItemRequestDto requestDto) {
        CartEntity cart = cartRepo.findByCustomerEmail(requestDto.getCustomerEmail());
        if (cart == null) {
            return buildEmptyCart(requestDto.getCustomerEmail());
        }
        CartItemEntity item = cartItemRepo.findByCartCartIdAndProductId(cart.getCartId(), requestDto.getProductId());
        if (item == null) {
            return mapToDto(cart, cartItemRepo.findByCartCartId(cart.getCartId()));
        }
        if (requestDto.getQuantity() == null || requestDto.getQuantity() <= 0) {
            cartItemRepo.delete(item);
        } else {
            item.setQuantity(requestDto.getQuantity());
            cartItemRepo.save(item);
        }
        recalculateCartTotals(cart);
        cartRepo.save(cart);
        return mapToDto(cart, cartItemRepo.findByCartCartId(cart.getCartId()));
    }

    @Override
    public CartDto removeItem(String customerEmail, Long productId) {
        CartEntity cart = cartRepo.findByCustomerEmail(customerEmail);
        if (cart == null) {
            return buildEmptyCart(customerEmail);
        }
        CartItemEntity item = cartItemRepo.findByCartCartIdAndProductId(cart.getCartId(), productId);
        if (item != null) {
            cartItemRepo.delete(item);
        }
        recalculateCartTotals(cart);
        cartRepo.save(cart);
        return mapToDto(cart, cartItemRepo.findByCartCartId(cart.getCartId()));
    }

    @Override
    public void clearCart(String customerEmail) {
        CartEntity cart = cartRepo.findByCustomerEmail(customerEmail);
        if (cart == null) {
            return;
        }
        cartItemRepo.deleteByCartCartId(cart.getCartId());
        cart.setTotalQuantity(0);
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepo.save(cart);
    }

    @Override
    public PurchaseOrderResponseDto checkoutCart(CartCheckoutRequestDto checkoutRequest) throws Exception {
        String customerEmail = checkoutRequest.getUserDto() != null ? checkoutRequest.getUserDto().getEmail() : null;
        if (customerEmail == null || customerEmail.isEmpty()) {
            throw new IllegalArgumentException("Customer email is required for checkout");
        }

        CartEntity cart = cartRepo.findByCustomerEmail(customerEmail);
        if (cart == null) {
            throw new IllegalStateException("No cart found for customer: " + customerEmail);
        }

        List<CartItemEntity> cartItems = cartItemRepo.findByCartCartId(cart.getCartId());
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty for customer: " + customerEmail);
        }

        PurchaseOrderRequestDto orderRequest = new PurchaseOrderRequestDto();
        orderRequest.setUserDto(checkoutRequest.getUserDto());
        orderRequest.setAddressDto(checkoutRequest.getAddressDto());

        OrderDto orderDto = new OrderDto();
        orderDto.setCustomerEmail(customerEmail);
        orderDto.setTotalPrice(cart.getTotalPrice() != null ? cart.getTotalPrice().doubleValue() : 0.0);
        orderDto.setTotalQuantity(cart.getTotalQuantity());
        orderRequest.setOrderDto(orderDto);

        List<OrderItemDto> orderItems = new ArrayList<>();
        for (CartItemEntity cartItem : cartItems) {
            OrderItemDto itemDto = new OrderItemDto();
            itemDto.setProductId(cartItem.getProductId());
            itemDto.setProductName(cartItem.getProductName());
            itemDto.setQuantity(cartItem.getQuantity());
            if (cartItem.getUnitPrice() != null) {
                itemDto.setUnitPrice(cartItem.getUnitPrice().doubleValue());
            }
            orderItems.add(itemDto);
        }
        orderRequest.setOrderItemDtoList(orderItems);

        PurchaseOrderResponseDto response = orderService.createOrder(orderRequest);

        cartItemRepo.deleteByCartCartId(cart.getCartId());
        cart.setTotalQuantity(0);
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepo.save(cart);

        return response;
    }

    private CartEntity getOrCreateCart(String customerEmail) {
        CartEntity cart = cartRepo.findByCustomerEmail(customerEmail);
        if (cart == null) {
            cart = new CartEntity();
            cart.setCustomerEmail(customerEmail);
            cart.setTotalQuantity(0);
            cart.setTotalPrice(BigDecimal.ZERO);
            cart = cartRepo.save(cart);
        }
        return cart;
    }

    private void recalculateCartTotals(CartEntity cart) {
        List<CartItemEntity> items = cartItemRepo.findByCartCartId(cart.getCartId());
        int totalQty = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItemEntity item : items) {
            int qty = item.getQuantity() != null ? item.getQuantity() : 0;
            totalQty += qty;
            if (item.getUnitPrice() != null) {
                totalPrice = totalPrice.add(item.getUnitPrice().multiply(BigDecimal.valueOf(qty)));
            }
        }
        cart.setTotalQuantity(totalQty);
        cart.setTotalPrice(totalPrice);
    }

    private CartDto mapToDto(CartEntity cart, List<CartItemEntity> items) {
        CartDto dto = new CartDto();
        dto.setCartId(cart.getCartId());
        dto.setCustomerEmail(cart.getCustomerEmail());
        dto.setTotalQuantity(cart.getTotalQuantity());
        dto.setTotalPrice(cart.getTotalPrice());

        List<CartItemDto> itemDtos = new ArrayList<>();
        for (CartItemEntity item : items) {
            CartItemDto itemDto = new CartItemDto();
            itemDto.setProductId(item.getProductId());
            itemDto.setProductName(item.getProductName());
            itemDto.setUnitPrice(item.getUnitPrice());
            itemDto.setQuantity(item.getQuantity());
            itemDtos.add(itemDto);
        }
        dto.setItems(itemDtos);
        return dto;
    }

    private CartDto buildEmptyCart(String customerEmail) {
        CartDto dto = new CartDto();
        dto.setCartId(null);
        dto.setCustomerEmail(customerEmail);
        dto.setTotalQuantity(0);
        dto.setTotalPrice(BigDecimal.ZERO);
        dto.setItems(new ArrayList<>());
        return dto;
    }
}
