package com.jingye.coffeemac.util.multicard;

public class MultiCardFilePath {
	/**
	 * 返回OK
	 */
	public static final int RET_OK = 0;
	
	/**
	 * 返回内存不多预警
	 */
	public static final int RET_LIMIT_SPACE_WARNNING = 1;
	
	/**
	 * 文件完整路径
	 */
	private String filePath;
	
	/**
	 * 返回码
	 */
	private int code;
	
	
	/**
	 * 获取文件路径
	 * @return
	 */
	public String getFilePath() {
		return filePath;
	}
	
	/**
	 * 设置文件路径
	 * @param mFilePath
	 */
	public void setFilePath(String mFilePath) {
		this.filePath = mFilePath;
	}
	
	/**
	 * 获取返回码
	 * @return
	 */
	public int getCode() {
		return code;
	}
	
	/**
	 * 设置返回码
	 * @param code
	 */
	public void setCode(int code) {
		this.code = code;
	}
}
