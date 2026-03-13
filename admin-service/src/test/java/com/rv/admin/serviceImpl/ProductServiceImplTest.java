package com.rv.admin.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.rv.admin.dto.ProductDto;
import com.rv.admin.entity.ProductEntity;
import com.rv.admin.repository.ProductCategoryRepository;
import com.rv.admin.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @InjectMocks
    private ProductServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductById_whenNotFound_shouldThrow() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getProductById(1));
        assertTrue(ex.getMessage().contains("Product not found"));
    }

    @Test
    void getProductById_shouldReturnDto() {
        ProductEntity entity = new ProductEntity();
        entity.setProductId(1);
        entity.setName("Test");
        when(productRepository.findById(1)).thenReturn(Optional.of(entity));
        ProductDto dto = service.getProductById(1);
        assertEquals("Test", dto.getName());
    }

    // Add more tests for createProduct, updateProduct, deleteProduct, etc. as needed
}
