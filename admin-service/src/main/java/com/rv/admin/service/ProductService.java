package com.rv.admin.service;

import org.springframework.web.multipart.MultipartFile;

import com.rv.admin.dto.ProductDto;
import com.rv.admin.entity.ProductEntity;

import java.util.List;

public interface ProductService {

    public ProductDto createProduct(Integer categoryId, ProductDto productDto, MultipartFile productImage) throws Exception;

    public ProductDto updateProduct(Integer productId, ProductDto productDto, MultipartFile productImage) throws Exception;

    public List<ProductDto> getAllProductsByCategoryId(Integer categoryId);

    public ProductDto getProductById(Integer productId);

    public List<ProductDto> getProductsByName(String productName);

    public ProductDto deleteProduct(Integer productId);

}
