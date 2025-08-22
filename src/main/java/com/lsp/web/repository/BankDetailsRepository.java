package com.lsp.web.repository;

import com.lsp.web.entity.BankDetails;
import com.lsp.web.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankDetailsRepository extends JpaRepository<BankDetails, Long> {
    Optional<BankDetails> findByUser(UserInfo user);
}
