package com.lsp.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lsp.web.entity.Repayment;

public interface RepaymentRepository extends JpaRepository<Repayment, Long> {
	
	boolean existsByTransactionIdAndPaymentId(String transactionId, String paymentId);

}
