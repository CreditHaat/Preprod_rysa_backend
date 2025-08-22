package com.lsp.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lsp.web.entity.MasterBank;

public interface MasterBankRepository extends JpaRepository<MasterBank, Long> {
	boolean existsByBankName(String bankName);
}

