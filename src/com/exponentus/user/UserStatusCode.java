package com.exponentus.user;

public enum UserStatusCode {
	UNKNOWN(0), NOT_VERIFIED(455), REGISTERED(456), USER_WAS_DELETED(457), WAITING_FOR_VERIFYCODE(458), VERIFYCODE_NOT_SENT(
	        460), WAITING_FIRST_ENTERING(463), RESET_PASSWORD_NOT_SENT(464), TEMPORARY(465);

	private int code;

	UserStatusCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static UserStatusCode getType(int code) {
		for (UserStatusCode type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		return UNKNOWN;
	}
}
