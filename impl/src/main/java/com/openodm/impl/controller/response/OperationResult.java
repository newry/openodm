/**
 * Copyright 2016 StubHub, Inc.  All rights reserved.
 */
package com.openodm.impl.controller.response;

/**
 * @author runiu
 * 
 */
public class OperationResult {
	private boolean success = true;
	private String error;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OperationResult [success=").append(success).append(", error=").append(error).append("]");
		return builder.toString();
	}

}
