package com.rv.wishlist.service;

import com.rv.wishlist.entity.WishlistItem;
import com.rv.wishlist.repository.WishlistItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistItemRepository repository;

    public List<WishlistItem> getWishlist(String customerEmail) {
        return repository.findByCustomerEmail(customerEmail);
    }

    public WishlistItem addItem(String customerEmail, Long productId) {
        if (repository.existsByCustomerEmailAndProductId(customerEmail, productId)) {
            // idempotent add
            return repository.findByCustomerEmail(customerEmail).stream()
                    .filter(i -> i.getProductId().equals(productId))
                    .findFirst()
                    .orElseThrow();
        }
        WishlistItem item = WishlistItem.builder()
                .customerEmail(customerEmail)
                .productId(productId)
                .createdAt(Instant.now())
                .build();
        return repository.save(item);
    }

    public void removeItem(String customerEmail, Long productId) {
        repository.deleteByCustomerEmailAndProductId(customerEmail, productId);
    }

    public void clearWishlist(String customerEmail) {
        repository.findByCustomerEmail(customerEmail)
                .forEach(i -> repository.deleteById(i.getId()));
    }
}
