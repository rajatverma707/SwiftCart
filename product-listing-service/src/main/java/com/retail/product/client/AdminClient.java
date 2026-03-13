package com.retail.product.client;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.retail.product.dto.ProductDto;

// Use service discovery when available (name), but fall back to the explicit URL property for local runs
@FeignClient(name = "admin-service", url = "${admin.service.url:http://localhost:8081}")
public interface AdminClient {
  // Admin service exposes /api/products (not /internal/products)
  @GetMapping("/api/products")
  List<ProductDto> getProducts(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("sort") String sort);
}
