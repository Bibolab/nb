package com.exponentus.rest.pojo.view;

import com.exponentus.rest.pojo.Request;
import com.exponentus.rest.pojo.constants.RequestType;

public class ViewRequest extends Request {
	protected RequestType type = RequestType.VIEW;
	protected int pageNum = 1;
	protected int pageSize = 20;

	public RequestType getType() {
		return type;
	}

	public void setType(RequestType type) {
		this.type = type;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
