package com.openodm.impl.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class OperationResponse {
	private OperationResult result;
	private String error;

	public OperationResult getResult() {
		return result;
	}

	public void setResult(OperationResult result) {
		this.result = result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OperationResponse [result=").append(result).append("]");
		return builder.toString();
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
