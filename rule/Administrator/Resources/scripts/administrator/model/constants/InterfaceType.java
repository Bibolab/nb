package administrator.model.constants;

/**
 * 
 * @author Kayra created 08-09-2016
 */
public enum InterfaceType {
	UNKNOWN(0), SPA(71), HTML(72);

	private int code;

	InterfaceType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static InterfaceType getType(int code) {
		for (InterfaceType type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		return UNKNOWN;
	}

}
