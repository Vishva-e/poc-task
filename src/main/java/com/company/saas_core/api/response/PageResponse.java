package com.company.saas_core.api.response;

import java.util.List;

public class PageResponse<T> {

	private final List<T> items;
	private final long totalElements;
	private final int page;
	private final int size;
	private final int totalPages;

	public PageResponse(List<T> items, long totalElements, int page, int size, int totalPages) {
		super();
		this.items = items;
		this.totalElements = totalElements;
		this.page = page;
		this.size = size;
		this.totalPages = totalPages;
	}

	public List<T> getItems() {
		return items;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public int getPage() {
		return page;
	}

	public int getSize() {
		return size;
	}

	public int getTotalPages() {
		return totalPages;
	}

}
