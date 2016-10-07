package com.exponentus.rest.pojo.view;

import com.exponentus.rest.pojo.constants.RequestType;

public class SearchViewRequest extends ViewRequest {
	protected RequestType type = RequestType.FT_SEARCH;
	protected String keyword = "";

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

}
