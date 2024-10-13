package com.example.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.example.backend.model.Product;
import com.example.backend.repository.ProductRepository;
import com.example.backend.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository; // Simuleerime productRepository'd

    @InjectMocks
    private ProductServiceImpl productService; // Testime ProductService klassi

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initsialiseerime Mockito annotatsioonid
    }

    @Test
    void testFindById_whenIdIs2_thenChangeTitle() {
        // Given
        Product product2 = new Product();
        product2.setId(2L);
        product2.setTitle("TEST TITLE");
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));

        // When
        Product result = productService.findById(2L);

        // Then
        assertNotNull(result);
        assertEquals("TEST TITLE", result.getTitle());
    }

}
