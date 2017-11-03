package com.jingye.coffeemac.service.protocol.response;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Response注解
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ResponseID {

	short service();

	String[] command();// MAGIC:2.1 does NOT support array of primitives types
}
