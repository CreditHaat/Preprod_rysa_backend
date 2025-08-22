package com.lsp.web.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lsp.web.entity.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
	
	public Optional<UserInfo> findByMobileNumber(String mobileNumber);

}
