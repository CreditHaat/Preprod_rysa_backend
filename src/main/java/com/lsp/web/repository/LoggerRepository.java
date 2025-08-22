package com.lsp.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lsp.web.entity.Logger;

public interface LoggerRepository extends JpaRepository<Logger, Long> {
}
