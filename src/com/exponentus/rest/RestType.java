package com.exponentus.rest;

public enum RestType {
	UNKNOWN(0), JERSEY(568);

	private int code;

	RestType(int code) {
		this.code = code;

	}

	public int getCode() {
		return code;
	}

	public static RestType getType(int code) {
		for (RestType type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		return UNKNOWN;
	}
}
