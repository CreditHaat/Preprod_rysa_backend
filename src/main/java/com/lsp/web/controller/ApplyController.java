package com.lsp.web.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lsp.web.Exception.UserInfoNotFoundException;
import com.lsp.web.dto.ApplyDTO;
import com.lsp.web.dto.ApplyFailDTO;
import com.lsp.web.entity.Apply;
import com.lsp.web.entity.ApplyFail;
import com.lsp.web.entity.UserInfo;
import com.lsp.web.repository.ApplyFailRepository;
import com.lsp.web.repository.ApplyRepository;
import com.lsp.web.repository.UserInfoRepository;

@RestController
public class ApplyController {

	@Autowired
	private UserInfoRepository userInfoRepository;
	@Autowired
	private ApplyRepository applyRepository;
	@Autowired
	private ApplyFailRepository applyFailRepository;
	
	@PostMapping("/createApplyRecord")
	public ResponseEntity<?> createApplyRecord(@RequestBody ApplyDTO applyDTO) 
	{
		try {

			UserInfo userInfo;
            Optional<UserInfo> optionalUserInfo = userInfoRepository.findByMobileNumber(applyDTO.getMobileNumber());
            if(optionalUserInfo.isEmpty()) {
            	throw new UserInfoNotFoundException("UserInfo not found with mobileNumber : "+applyDTO.getMobileNumber());
            	
            }
            userInfo = optionalUserInfo.get();
			
			Apply apply = new Apply();
			apply.setMobileNumber(applyDTO.getMobileNumber());
			apply.setProductName(applyDTO.getMobileNumber());
			apply.setStage(applyDTO.getStage());
			apply.setUser(userInfo);
			
			applyRepository.save(apply);
			
			return ResponseEntity.ok(apply);
		}catch(UserInfoNotFoundException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	@PostMapping("/createApplyFailRecord")
	public ResponseEntity<?> createApplyFail(@RequestBody ApplyFailDTO applyFailDTO) 
	{
		try {

			UserInfo userInfo;
            Optional<UserInfo> optionalUserInfo = userInfoRepository.findByMobileNumber(applyFailDTO.getMobileNumber());
            if(optionalUserInfo.isEmpty()) {
            	throw new UserInfoNotFoundException("UserInfo not found with mobileNumber : "+applyFailDTO.getMobileNumber());
            	
            }
            userInfo = optionalUserInfo.get();
			
			ApplyFail applyFail = new ApplyFail();
			applyFail.setUserMobileNumber(applyFailDTO.getMobileNumber());
			applyFail.setProductName(applyFailDTO.getMobileNumber());
//			applyFail.setStage(applyDTO.getStage());
			applyFail.setUser(userInfo);
			
			applyFailRepository.save(applyFail);
			
			return ResponseEntity.ok(applyFail);
		}catch(UserInfoNotFoundException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
		catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}


}
