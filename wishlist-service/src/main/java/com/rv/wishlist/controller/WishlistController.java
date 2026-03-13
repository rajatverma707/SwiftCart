package com.rv.wishlist.controller;

import com.rv.wishlist.entity.WishlistItem;
import com.rv.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public List<WishlistItem> getWishlist(@RequestParam String email) {
        return wishlistService.getWishlist(email);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public WishlistItem addItem(@RequestParam String email, @RequestParam Long productId) {
        return wishlistService.addItem(email, productId);
    }

    @DeleteMapping("/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@RequestParam String email, @RequestParam Long productId) {
        wishlistService.removeItem(email, productId);
    }

    @DeleteMapping("/clear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearWishlist(@RequestParam String email) {
        wishlistService.clearWishlist(email);
    }
}
