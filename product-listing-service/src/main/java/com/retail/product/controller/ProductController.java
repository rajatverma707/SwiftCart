package com.retail.product.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.retail.product.dto.ProductDto;
import com.retail.product.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private  ProductService service;

  @GetMapping
  public List<ProductDto> products(
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "10") int size,
      @RequestParam(name = "sort", defaultValue = "name") String sort) {
    return service.list(page, size, sort);
  }
}
