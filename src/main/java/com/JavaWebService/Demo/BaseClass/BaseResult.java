package com.JavaWebService.Demo.BaseClass;

import java.io.Serializable;

public class BaseResult implements Serializable {

	private static final long serialVersionUID = -2149778906536101931L;
	
	private boolean success;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	
}
