package com.lsp.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lsp.web.entity.ReferenceDetails;

public interface ReferenceDetailsRepository extends JpaRepository<ReferenceDetails, Long> {
    List<ReferenceDetails> findByClientLoanId(String clientLoanId);
}

