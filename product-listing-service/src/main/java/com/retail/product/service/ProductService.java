package com.retail.product.service;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.retail.product.client.AdminClient;
import com.retail.product.dto.ProductDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
  private final AdminClient adminClient;

  @Cacheable("products")
  @CircuitBreaker(name = "adminService", fallbackMethod = "fallback")
  public List<ProductDto> list(int page, int size, String sort) {
    log.info("Calling admin service to fetch products: page={}, size={}, sort={}", page, size, sort);
    List<ProductDto> products = adminClient.getProducts(page, size, sort);
    log.info("Received {} products from admin service", products == null ? 0 : products.size());
    return products;
  }

  public List<ProductDto> fallback(int page, int size, String sort, Throwable t) {
    log.warn("Fallback triggered for list(page={},size={},sort={}) due to: {}", page, size, sort, t == null ? "unknown" : t.toString());
    return java.util.Collections.emptyList();
    }
  }
