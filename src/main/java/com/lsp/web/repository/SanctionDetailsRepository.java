  package com.lsp.web.repository;

import com.lsp.web.entity.SanctionDetails;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SanctionDetailsRepository extends JpaRepository<SanctionDetails, Long> {
    // You can add custom queries here if needed, for example:
    Optional<SanctionDetails> findByClientLoanId(String clientLoanId);
    
    Optional<SanctionDetails> findTopByClientLoanIdOrderByCreateTimeDesc(String clientLoanId);

}
