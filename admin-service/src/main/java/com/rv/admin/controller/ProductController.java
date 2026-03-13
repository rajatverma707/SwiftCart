package com.rv.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rv.admin.dto.ApiResponse;
import com.rv.admin.dto.ProductDto;
import com.rv.admin.service.ProductService;

@RestController
@RequestMapping("/admin/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(@RequestParam("categoryId") Integer categoryId,
                                                                 @RequestParam("product") String productDtoJson,
                                                                 @RequestParam("productImage") MultipartFile productImage) throws Exception {


        ObjectMapper objectMapper = new ObjectMapper();
        ProductDto productDto = objectMapper.readValue(productDtoJson, ProductDto.class);

        ProductDto createdProduct = productService.createProduct(categoryId, productDto, productImage);

        ApiResponse<ProductDto> response = new ApiResponse<>();

        if (createdProduct != null) {
            response.setStatusCode(201);
            response.setMessage("Product created successfully");
            response.setData(createdProduct);
            return ResponseEntity.status(201).body(response);
        } else {
            response.setStatusCode(400);
            response.setMessage("Failed to create product");
            response.setData(null);
            return ResponseEntity.status(400).body(response);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(@PathVariable Integer id,
                                                                @RequestParam("product") String productDtoJson,
                                                                @RequestParam(value = "productImage", required = false) MultipartFile productImage) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ProductDto productDto = objectMapper.readValue(productDtoJson, ProductDto.class);
        ProductDto updatedProduct = productService.updateProduct(id, productDto, productImage);
        ApiResponse<ProductDto> response = new ApiResponse<>();
        if (updatedProduct != null) {
            response.setStatusCode(200);
            response.setMessage("Product updated successfully");
            response.setData(updatedProduct);
            return ResponseEntity.ok(response);
        } else {
            response.setStatusCode(400);
            response.setMessage("Failed to update product");
            response.setData(null);
            return ResponseEntity.status(400).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> deleteProduct(@PathVariable Integer id) {
        ProductDto deletedProduct = productService.deleteProduct(id);
        ApiResponse<ProductDto> response = new ApiResponse<>();
        if (deletedProduct != null) {
            response.setStatusCode(200);
            response.setMessage("Product deleted successfully");
            response.setData(deletedProduct);
            return ResponseEntity.ok(response);
        } else {
            response.setStatusCode(404);
            response.setMessage("Product not found");
            response.setData(null);
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Integer id) {
        ProductDto product = productService.getProductById(id);
        ApiResponse<ProductDto> response = new ApiResponse<>();
        if (product != null) {
            response.setStatusCode(200);
            response.setMessage("Product fetched successfully");
            response.setData(product);
            return ResponseEntity.ok(response);
        } else {
            response.setStatusCode(404);
            response.setMessage("Product not found");
            response.setData(null);
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping("/fetch-all")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getAllProductsByCategoryId(@RequestParam Integer categoryId) {
        List<ProductDto> products = productService.getAllProductsByCategoryId(categoryId);
        ApiResponse<List<ProductDto>> response = new ApiResponse<>();
        response.setStatusCode(200);
        response.setMessage("Products fetched successfully");
        response.setData(products);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getProductsByName(@RequestParam String productName) {
        List<ProductDto> products = productService.getProductsByName(productName);
        ApiResponse<List<ProductDto>> response = new ApiResponse<>();
        response.setStatusCode(200);
        response.setMessage("Products fetched successfully");
        response.setData(products);
        return ResponseEntity.ok(response);
    }
    }

