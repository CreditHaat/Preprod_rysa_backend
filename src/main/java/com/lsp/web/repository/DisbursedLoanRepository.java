package com.lsp.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lsp.web.entity.DisbursedLoan;
import com.lsp.web.entity.UserInfo;

public interface DisbursedLoanRepository extends JpaRepository<DisbursedLoan, Long> {

//	Optional<DisbursedLoan> findByUser(UserInfo user);
	List<DisbursedLoan> findByUser(UserInfo user);
	
	//this will return a single loan associated with that particular unique loan number
	Optional<DisbursedLoan> findByLoanNumber(String loanNumber);

	Optional<DisbursedLoan> findByTransactionId(String transactionId);

}
