package com.jingye.coffeemac.domain;

public class OrderFetchStatus {
	
	public static final int ORDER_STATUS_REQUESTING = 1;
	public static final int ORDER_STATUS_TIMEOUT = 2;

	private String fetchCode;
	private long timestamp;
	private int status;
	private boolean retry;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getFetchCode() {
		return fetchCode;
	}

	public void setFetchCode(String fetchCode) {
		this.fetchCode = fetchCode;
	}

	public boolean isRetry() {
		return retry;
	}

	public void setRetry(boolean retry) {
		this.retry = retry;
	}
}
