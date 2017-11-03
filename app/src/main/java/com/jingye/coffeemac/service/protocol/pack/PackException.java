package com.jingye.coffeemac.service.protocol.pack;

public class PackException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PackException() {
		super("PackError");
	}

	public PackException(String message) {
		super(message);
	}
}
