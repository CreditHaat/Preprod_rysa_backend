package com.lsp.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lsp.web.entity.Apply;
import com.lsp.web.entity.UserInfo;

import java.util.Optional;

public interface ApplyRepository extends JpaRepository<Apply, Integer> {
    Optional<Apply> findByUser(UserInfo user);
    
    Optional<Apply> findByUserAndProductName(UserInfo user, String productName);

}


