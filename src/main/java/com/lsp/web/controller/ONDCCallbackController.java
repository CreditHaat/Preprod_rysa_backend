package com.lsp.web.controller;

import java.io.BufferedReader;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lsp.web.ONDCService.ConfirmService;
import com.lsp.web.ONDCService.InitService;
import com.lsp.web.ONDCService.SearchService;
import com.lsp.web.ONDCService.SelectService;
import com.lsp.web.ONDCService.StatusService;
import com.lsp.web.ONDCService.UpdateService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
//@RequestMapping("/ondc")
public class ONDCCallbackController {
	
	@Autowired
	private SearchService searchService;
	@Autowired
	private SelectService selectService;
	@Autowired
	private InitService initService;
	@Autowired
	private StatusService statusService;
	@Autowired
	private ConfirmService confirmService;
	@Autowired
	private UpdateService updateService;
	
	@RequestMapping(value = "/on_search", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> handleCallback(HttpServletRequest request) throws IOException {
		
		System.out.println("Got the callback in on_search");
		
		try {
			StringBuilder requestBody = new StringBuilder();
	        BufferedReader reader = request.getReader();
	        String line;

	        while ((line = reader.readLine()) != null) {
	            requestBody.append(line);
	        }
			return searchService.onSearch(requestBody);
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok("NACK");
		}
		
        

//        System.out.println("The on_search callback received is: " + requestBody.toString());
        //here we will write code to save this callback in db
        
    }
	
	@RequestMapping(value = "/on_select", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> handleOnSelectCallback(HttpServletRequest request) throws IOException {
		try {
			StringBuilder requestBody = new StringBuilder();
	        BufferedReader reader = request.getReader();
	        String line;

	        while ((line = reader.readLine()) != null) {
	            requestBody.append(line);
	        }
			return selectService.onSelect(requestBody);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.ok("NACK");
    }
	
	@RequestMapping(value = "/on_init", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> handleOnInitCallback(HttpServletRequest request) throws IOException {
		try {
			StringBuilder requestBody = new StringBuilder();
	        BufferedReader reader = request.getReader();
	        String line;

	        while ((line = reader.readLine()) != null) {
	            requestBody.append(line);
	        }
	        return initService.onInit(requestBody);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok("NACK");
    }
	
	@RequestMapping(value = "/on_confirm", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> handleOnConfirmCallback(HttpServletRequest request) throws IOException {
		try {
			StringBuilder requestBody = new StringBuilder();
	        BufferedReader reader = request.getReader();
	        String line;

	        while ((line = reader.readLine()) != null) {
	            requestBody.append(line);
	        }
			return confirmService.onConfirm(requestBody);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok("NACK");
    }
	@RequestMapping(value = "/on_update", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> handleOnUpdateCallback(HttpServletRequest request) throws IOException {
		try {
			StringBuilder requestBody = new StringBuilder();
	        BufferedReader reader = request.getReader();
	        String line;

	        while ((line = reader.readLine()) != null) {
	            requestBody.append(line);
	        }
			return updateService.onUpdate(requestBody);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok("NACK");

    }
	
	@RequestMapping(value = "/on_status", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> handleOnStatusCallback(HttpServletRequest request) throws IOException {
		try {
			StringBuilder requestBody = new StringBuilder();
	        BufferedReader reader = request.getReader();
	        String line;

	        while ((line = reader.readLine()) != null) {
	            requestBody.append(line);
	        }
			return statusService.onStatus(requestBody);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.ok("NACK");
    }

}
