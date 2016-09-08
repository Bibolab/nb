package administrator.model.constants;

/**
 * 
 * @author Kayra created 08-09-2016
 */
public enum MessagingType {
	UNKNOWN(0), EMAIL(56), SLACK(57), XMPP(58), ALL(59), EMAIL_IM(60);

	private int code;

	MessagingType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static MessagingType getType(int code) {
		for (MessagingType type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		return UNKNOWN;
	}

}
