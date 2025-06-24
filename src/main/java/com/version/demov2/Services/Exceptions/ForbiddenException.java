package com.version.demov2.Services.Exceptions;

@SuppressWarnings("serial")
public class ForbiddenException extends RuntimeException {

	public ForbiddenException(String msg) {
		super(msg);
	}
}