package com.lsp.web.dto;

import com.lsp.web.entity.DisbursedLoan;

public class LoanMapper {

    public static DisbursedLoanDTO2 toDTO(DisbursedLoan loan) {
        DisbursedLoanDTO2 dto = new DisbursedLoanDTO2();
        dto.setId(String.valueOf(loan.getId()));
        dto.setLoanNumber(loan.getLoanNumber());
        dto.setPrincipalAmount(loan.getPrincipalAmount());
        dto.setOutstandingAmount(loan.getOutstandingAmount());
        dto.setInterestRate(loan.getInterestRate());
        dto.setTenureMonths(loan.getTenureMonths());
        dto.setType(loan.getType());
        dto.setStatus(loan.getStatus());
        dto.setTransactionId(loan.getTransactionId());
        dto.setBppId(loan.getBppId());
        dto.setBppUri(loan.getBppUri());
        dto.setVersion(loan.getVersion());
        return dto;
    }
}
