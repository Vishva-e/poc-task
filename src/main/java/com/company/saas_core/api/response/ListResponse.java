package com.company.saas_core.api.response;

import java.util.List;

public class ListResponse<T> {

	private final List<T> items;
	private final int count;

	public ListResponse(List<T> items, int count) {
		super();
		this.items = items;
		this.count = count;
	}

	public List<T> getItems() {
		return items;
	}

	public int getCount() {
		return count;
	}

}
