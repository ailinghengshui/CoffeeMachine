package com.jingye.coffeemac.service.protocol;

public final class ResponseCode {

	public static final short RES_SUCCESS = 200;

	public static final short RES_EVERSION = 201; // 客户端版本不对

	public static final short RES_ENOTINVITE = 300; // 用户没有被邀请

	public static final short RES_EBAN = 301; // 用户在黑名单中

	public static final short RES_EUIDPASS = 302; // 密码错误

	public static final short RES_ALREADY_LOGINED  = 303; // 该账号已经登录
	
	public static final short RES_FORBIDDEN = 403; // 用户被封禁 

	public static final short RES_ENONEXIST = 404; // 目标(对象或用户)不存在

	public static final short RES_EACCESS = 405; // 没有权限操作

	public static final short RES_ETIMEOUT = 408; // 超时

}
