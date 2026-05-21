package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.*;
import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductResponse> getAllProducts() {
        return productRepository.findByActiveTrue()
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(UUID id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Product not found!"));
    }

    public ProductResponse createProduct(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku()))
            throw new RuntimeException("SKU already exists!");

        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found!"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .category(category)
                .sku(request.getSku())
                .active(true)
                .build();

        return toResponse(productRepository.save(product));
    }

    public ProductResponse updateStock(UUID id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found!"));
        product.setStockQuantity(product.getStockQuantity() + quantity);
        return toResponse(productRepository.save(product));
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stockQuantity(p.getStockQuantity())
                .categoryName(p.getCategory() != null ?
                        p.getCategory().getName() : null)
                .sku(p.getSku())
                .active(p.getActive())
                .build();
    }
}