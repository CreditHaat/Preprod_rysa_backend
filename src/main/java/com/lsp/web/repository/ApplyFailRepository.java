package com.lsp.web.repository;

import com.lsp.web.entity.Apply;
import com.lsp.web.entity.ApplyFail;
import com.lsp.web.entity.UserInfo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplyFailRepository extends JpaRepository<ApplyFail, Long> {
	
	Optional<ApplyFail> findByUserAndProductName(UserInfo user, String productName);
	
}
