package com.lsp.web.Exception;

public class UserInfoNotFoundException extends RuntimeException {
	
	public UserInfoNotFoundException(String message) {
		super(message);
	}
	
	public UserInfoNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}


}
