package com.version.demov2.Services.Exceptions;

@SuppressWarnings("serial")
public class DatabaseException extends RuntimeException {

	public DatabaseException(String msg) {
		super(msg);
	}
}