package it.dcs.iscrivo.mailclientnew.dao.model;

import java.util.List;

public class PaginatedData<T> {
	public PaginatedData(List<T> collection, long totalRecords) {
		this.collection = collection;
		this.totalRecords = totalRecords;
	}

	private List<T>	collection;

	private long	totalRecords;

	public void setCollection(List<T> collection) {
		this.collection = collection;
	}

	public List<T> getCollection() {
		return collection;
	}

	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}

	public long getTotalRecords() {
		return totalRecords;
	}
}
