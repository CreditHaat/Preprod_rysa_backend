package com.lsp.web.util;

public class StringUtil{
	public static boolean nullOrEmpty(String str){
		return str==null||str.trim().length()==0;
	}

	public static boolean notEmpty(String str){
		return !(str==null||str.trim().length()==0);
	}
}

