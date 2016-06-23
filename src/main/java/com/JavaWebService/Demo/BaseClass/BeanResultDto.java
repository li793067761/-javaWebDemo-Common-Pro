/**
 * BeanResultDto.java    2014年2月19日上午10:22:05
 * Copyright 2012 Garea Microsystem Inc. All Rights Reserved.
 */
package com.JavaWebService.Demo.BaseClass;

import java.io.Serializable;

/**
 * @author yuancen.li
 * @since 2014年2月19日  上午10:22:05
 */
public class BeanResultDto<T> extends BaseResult implements Serializable{

	private static final long serialVersionUID = 6202073842525134956L;
	
	private T result;

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}
}
