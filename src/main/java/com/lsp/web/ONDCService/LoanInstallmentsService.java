package com.lsp.web.ONDCService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lsp.web.Exception.LoanNotFoundException;
import com.lsp.web.dto.LoanInstallmentDTO;
import com.lsp.web.entity.DisbursedLoan;
import com.lsp.web.entity.LoanInstallments;
import com.lsp.web.repository.DisbursedLoanRepository;
import com.lsp.web.repository.LoanInstallmentsRepository;

@Service
public class LoanInstallmentsService {
	
	@Autowired
	private DisbursedLoanRepository disbursedLoanRepository;

    @Autowired
    private LoanInstallmentsRepository installmentsRepository;

    public Optional<LoanInstallments> getUpcomingInstallment(String loanNumber) {
    	
    	Optional<DisbursedLoan> loan = disbursedLoanRepository.findByLoanNumber(loanNumber);
    	if(loan.isEmpty()) {
    		throw new LoanNotFoundException("loan with id : "+loanNumber+" not found");
    	}
    	
        // Assuming "PENDING" status means unpaid
        return installmentsRepository.findFirstByDisbursedLoanAndStatusOrderByEndDateAsc(loan.get(), "NOT-PAID");
    }

//	public Object fetchInstallmentsByLoan(String loanId) {
//		
//		Optional<DisbursedLoan> disbursedLoan = disbursedLoanRepository.findById(Long.parseLong(loanId));
//		if(disbursedLoan.isEmpty()) {
//			return new LoanNotFoundException("Loan with id : "+loanId+" not found");
//		}
//		
//		List<LoanInstallments> disbursedLoanList = installmentsRepository.findByDisbursedLoan(disbursedLoan.get());
//		
//		return disbursedLoanList;
//		
////		return null;
//	}
    
    public Object fetchInstallmentsByLoan(String loanId) {
        Optional<DisbursedLoan> disbursedLoan = disbursedLoanRepository.findById(Long.parseLong(loanId));
        if (disbursedLoan.isEmpty()) {
            return new LoanNotFoundException("Loan with id : " + loanId + " not found");
        }

        List<LoanInstallments> disbursedLoanList = installmentsRepository.findByDisbursedLoan(disbursedLoan.get());

        // Convert to DTO
        List<LoanInstallmentDTO> dtoList = disbursedLoanList.stream().map(installment -> {
            LoanInstallmentDTO dto = new LoanInstallmentDTO();
            dto.setId(installment.getId());  // 👈 converting to string
            dto.setInstallmentId(installment.getInstallmentId());
            dto.setType(installment.getType());
            dto.setStartDate(installment.getStartDate());
            dto.setEndDate(installment.getEndDate());
            dto.setStatus(installment.getStatus());
            dto.setInstallmentAmount(installment.getInstallmentAmount());
            dto.setDisbursedLoan(installment.getDisbursedLoan()); // 👈 include full object
            return dto;
        }).collect(Collectors.toList());

        return dtoList;
    }
    
    public Object fetchMissedEmiByLoan(String loanId) {
        Optional<DisbursedLoan> disbursedLoan = disbursedLoanRepository.findById(Long.parseLong(loanId));
        if (disbursedLoan.isEmpty()) {
            return new LoanNotFoundException("Loan with id : " + loanId + " not found");
        }

        Optional<LoanInstallments> optionalInstallment = installmentsRepository.findFirstByDisbursedLoanAndStatusOrderByEndDateAsc(disbursedLoan.get(), "DELAYED");

        if(optionalInstallment.isEmpty()) {
//        	throw new LoanNotFoundException("loan with id : "+loanId+)
        	return null;
        }
        // Convert to DTO
        
        LoanInstallments installment = optionalInstallment.get();
            LoanInstallmentDTO dto = new LoanInstallmentDTO();
            dto.setId(installment.getId());  // 👈 converting to string
            dto.setInstallmentId(installment.getInstallmentId());
            dto.setType(installment.getType());
            dto.setStartDate(installment.getStartDate());
            dto.setEndDate(installment.getEndDate());
            dto.setStatus(installment.getStatus());
            dto.setInstallmentAmount(installment.getInstallmentAmount());
            dto.setDisbursedLoan(installment.getDisbursedLoan()); // 👈 include full object
            return dto;
    

//        return dtoList;
    }
    
}

