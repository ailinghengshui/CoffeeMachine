package com.jingye.coffeemac.common.database;

public class TException extends Exception{

	private static final long serialVersionUID = 1L;
	private int code;
	private String message;
	
	public TException(int code,String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public TException(String messsage,Throwable cause)
	{
		super(messsage,cause);
	}

	public TException(String messsage)
	{
		super(messsage);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public TException(Throwable cause)
	{
		super(cause);
	}
}
