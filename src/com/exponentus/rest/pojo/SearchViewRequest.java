package com.exponentus.rest.pojo;

public class SearchViewRequest extends ViewRequest {
	protected String keyword = "";

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

}
