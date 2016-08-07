package com.openodm.impl.controller.response;

public class Breadcrumb {
	private String url;
	private String label;

	public static Breadcrumb create(String url, String label) {
		return new Breadcrumb(url, label);
	}

	private Breadcrumb(String url, String label) {
		this.url = url;
		this.label = label;
	}

	public String getUrl() {
		return url;
	}

	public String getLabel() {
		return label;
	}
}
