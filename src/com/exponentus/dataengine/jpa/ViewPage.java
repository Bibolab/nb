package com.exponentus.dataengine.jpa;

import java.util.ArrayList;
import java.util.List;

public class ViewPage<T> {
	private List<T> result;
	private long count;
	private int maxPage;
	private int pageNum;
	private String keyWord;

	public ViewPage(List<T> result, long count2, int maxPage, int pageNum) {
		this.result = result;
		this.count = count2;
		this.maxPage = maxPage;
		this.pageNum = pageNum;
	}

	public ViewPage(List<T> result, long count2, int maxPage, int pageNum, String k) {
		this(result, count2, maxPage, pageNum);
		keyWord = k;
	}

	public ViewPage(T entity) {
		this.result = new ArrayList<>();
		result.add(entity);
		this.count = 1;
		this.maxPage = 1;
		this.pageNum = 1;
	}

	public long getCount() {
		return count;
	}

	public List<T> getResult() {
		return result;
	}

	public int getMaxPage() {
		return maxPage;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public String getKeyWord() {
		return keyWord;
	}

}
