package com.cmax.bodysheild.exception;

/**
 * 命令不合法异常
 * Created by allen on 15-11-3.
 */
public class IllegalCommandException extends RuntimeException{
	public IllegalCommandException(){
		super();
	}

	public IllegalCommandException(String msg){
		super(msg);
	}

	public IllegalCommandException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}
}
