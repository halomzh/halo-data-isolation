package com.halo.data.isolation.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author shoufeng
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class HaloDataIsolationException extends RuntimeException implements Serializable {

	private int code = 500;
	private String message;


	public HaloDataIsolationException(String message) {
		super(message);
		this.message = message;
	}

	public HaloDataIsolationException(String message, Throwable e) {
		super(message, e);
		this.message = message;
	}

	public HaloDataIsolationException(String message, int code) {
		super(message);
		this.message = message;
		this.code = code;
	}

	public HaloDataIsolationException(String message, int code, Throwable e) {
		super(message, e);
		this.message = message;
		this.code = code;
	}

}
