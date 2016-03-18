package com.openodm.impl.controller.response;

public class OperationResponse {
	private OperationResult result = new OperationResult();

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

}
