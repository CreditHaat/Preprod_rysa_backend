package com.lsp.web.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lsp.web.entity.Callback;

public interface CallbackRepository extends JpaRepository<Callback, Long> {
	
}
