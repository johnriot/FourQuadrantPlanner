package com.example.fourquadrantcontentprovider;

public class DataRecord {

	private static int id;

	// Unique ID
	private final int _id;

	// Display Name
	private final String _data;

	public DataRecord(String _data) {
		this._data = _data;
		this._id = id++;
	}

	public String getData() {
		return _data;
	}

	public int getID() {
		return _id;
	}

}
