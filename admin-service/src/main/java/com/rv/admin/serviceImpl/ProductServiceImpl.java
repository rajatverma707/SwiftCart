package com.rv.admin.serviceImpl;

import com.rv.admin.dto.ProductDto;
import com.rv.admin.entity.ProductEntity;
import com.rv.admin.mapper.ProductMapper;
import com.rv.admin.repository.ProductCategoryRepository;
import com.rv.admin.repository.ProductRepository;
import com.rv.admin.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Value("${app.images.upload-dir}")
    private String imagesUploadDir;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Override
    public ProductDto createProduct(Integer categoryId, ProductDto productDto, MultipartFile productImage) throws Exception {

        String originalFilename = productImage.getOriginalFilename();
        Path filePath = Paths.get(imagesUploadDir + originalFilename);

        // Create the directory if it doesn't exist
        if (!Files.exists(filePath)) {
            try {
                Files.createDirectories(filePath.getParent());

            } catch (IOException e) {
                throw new RuntimeException("Failed to save product image", e);
            }
        }

        // Save the file to the specified location
        Files.copy(productImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        productDto.setImageUrl(filePath.toString());
        ProductEntity entity = ProductMapper.toEntity(productDto);

        productCategoryRepository.findById(categoryId).ifPresent(category -> {
            entity.setCategory(category);
        });

        ProductEntity savedEntity = productRepository.save(entity);
        return ProductMapper.toDto(savedEntity);
    }

    @Override
    public ProductDto updateProduct(Integer productId, ProductDto productDto, MultipartFile productImage) throws Exception {

        ProductEntity entity = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (productImage != null && !productImage.isEmpty()) {
            String originalFilename = productImage.getOriginalFilename();
            Path filePath = Paths.get(imagesUploadDir + originalFilename);
            if (!Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            Files.copy(productImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            productDto.setImageUrl(filePath.toString());
        } else {
            productDto.setImageUrl(entity.getImageUrl());
        }

        ProductEntity updatedEntity = ProductMapper.toEntity(productDto);
       // updatedEntity.setId(productId);
        updatedEntity.setCategory(entity.getCategory());

        ProductEntity savedEntity = productRepository.save(updatedEntity);
        return ProductMapper.toDto(savedEntity);
    }

    @Override
    public List<ProductDto> getAllProductsByCategoryId(Integer categoryId) {
        return productRepository.findById(categoryId)
            .stream()
            .map(ProductMapper::toDto)
            .toList();
    }

    @Override
    public ProductDto getProductById(Integer productId) {
    ProductEntity entity = productRepository.findById(productId)
        .orElseThrow(() -> new RuntimeException("Product not found"));
    return ProductMapper.toDto(entity);
    }

    @Override
    public List<ProductDto> getProductsByName(String productName) {
    return productRepository.findAllByNameContainingIgnoreCase(productName)
        .stream()
        .map(ProductMapper::toDto)
        .toList();
    }

    @Override
    public ProductDto deleteProduct(Integer productId) {
    ProductEntity entity = productRepository.findById(productId)
        .orElseThrow(() -> new RuntimeException("Product not found"));
    productRepository.delete(entity);
    return ProductMapper.toDto(entity);
    }


}
