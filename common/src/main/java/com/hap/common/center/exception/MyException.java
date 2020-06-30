package com.hap.common.center.exception;

public class MyException extends RuntimeException {

	public MyException(){
		super();
	}

	private static final long serialVersionUID = 1L;

	private int code;

	private String message;

	private Throwable throwable;

	public MyException(int code) {
		this.code = code;
	}

	public MyException(String message) {
		this.code = 500;
		this.message=message;
	}

	public MyException(String message, Throwable e) {
		this.code = 500;
		this.message = message;
		this.throwable = e;
	}
//
public MyException(int code, String message) {
	this.code = code;
	this.message = message;
}

	public MyException(int code, String message, Throwable e) {
		this.code = code;
		this.message = message;
		this.throwable = e;
	}

	public MyException(ExceptionEnum customEnum) {
		this.code = customEnum.getCode();
		this.message=customEnum.getMessage();
	}

	public MyException(ExceptionEnum customEnum, Throwable throwable) {
		this.code = customEnum.getCode();
		this.message = customEnum.getMessage();
		this.throwable = throwable;
	}

	
	
	public int getCode() {
		return code;
	}
	@Override
    public String getMessage() {
		return message;
	}


}
