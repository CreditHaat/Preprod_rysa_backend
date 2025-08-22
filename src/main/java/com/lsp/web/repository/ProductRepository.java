package com.lsp.web.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lsp.web.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findByProductName(String name);

}
