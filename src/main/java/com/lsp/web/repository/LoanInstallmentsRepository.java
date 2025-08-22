package com.lsp.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lsp.web.entity.DisbursedLoan;
import com.lsp.web.entity.LoanInstallments;

public interface LoanInstallmentsRepository extends JpaRepository<LoanInstallments, Long> {

	// Fetch the next upcoming installment based on startDate or endDate
    Optional<LoanInstallments> findFirstByDisbursedLoanAndStatusOrderByStartDateAsc(
            DisbursedLoan disbursedLoan, String status);

    // Or if you want by endDate (common in EMI cases)
    Optional<LoanInstallments> findFirstByDisbursedLoanAndStatusOrderByEndDateAsc(
            DisbursedLoan disbursedLoan, String status);

//	Optional<LoanInstallments> findByDisbursedLoanAndInstallmentId(DisbursedLoan loan, int int1);
    Optional<LoanInstallments> findByDisbursedLoanAndInstallmentId(DisbursedLoan loan, String InstallmentId);
    
 // LoanInstallmentsRepository
    void deleteByDisbursedLoan(DisbursedLoan loan);

	List<LoanInstallments> findByDisbursedLoan(DisbursedLoan disbursedLoan);
    
//    Optional<LoanInstallments> findFirstByDisbursedLoanAndStatusOrderByEndDateAscIdAsc(
//            DisbursedLoan disbursedLoan, String status);
    
//    findByDisbursedLoanAndInstallmentId
	
}
