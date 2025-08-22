package com.lsp.web.Exception;

public class ProductNotFoundExeption extends RuntimeException {
	
	public ProductNotFoundExeption(String message) {
		super(message);
	}
	
	public ProductNotFoundExeption(String message, Throwable cause) {
		super(message, cause);
	}

}
