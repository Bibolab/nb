package com.exponentus.scripting;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import com.exponentus.util.Util;

public class _WebFormData {
	private Map<String, String[]> data;
	private String referrer;

	public _WebFormData(Map<String, String[]> formData, String r) {
		this.data = formData;
		setReferer(r);
	}

	public String getValueSilently(String fn) {
		try {
			String value[] = data.get(fn);
			if (value[0].contains("null")) {
				return "";
			} else {
				Object r = value[0].trim();
				return (String) r;
			}
		} catch (Exception e) {
			return "";
		}
	}

	public String getValueSilently(String fn, String defaultValue) {
		try {
			String value[] = data.get(fn);
			return value[0].trim();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public String getEncodedValueSilently(String fn) {
		try {
			return new String(getValueSilently(fn).getBytes("ISO-8859-1"), "UTF-8");
		} catch (Exception e) {
			return "";
		}
	}

	public int getNumberValueSilently(String fn, int defaultValue) {
		if (!containsField(fn)) {
			return defaultValue;
		}

		try {
			String value[] = data.get(fn);
			return Integer.parseInt(value[0].trim());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public float getFloatValueSilently(String fn, int defaultValue) {
		try {
			String value[] = data.get(fn);
			return Float.parseFloat(value[0].trim().replace(" ", "").replace(",", "."));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public Long[] getListOfLongValues(String fn, long defaultValue) {
		try {
			String value[] = data.get(fn);
			Long[] nValue = new Long[value.length];
			for (int i = 0; i < value.length; i++) {
				try {
					nValue[i] = Long.parseLong(value[i].trim());
				} catch (Exception e) {
					nValue[i] = defaultValue;
				}
			}
			return nValue;
		} catch (Exception e) {
			return new Long['0'];
		}
	}

	public Integer[] getListOfNumberValues(String fn, int defaultValue) {
		try {
			String value[] = data.get(fn);
			Integer[] nValue = new Integer[value.length];
			for (int i = 0; i < value.length; i++) {
				try {
					nValue[i] = Integer.parseInt(value[i].trim());
				} catch (Exception e) {
					nValue[i] = defaultValue;
				}
			}
			return nValue;
		} catch (Exception e) {
			return new Integer['0'];
		}
	}

	public double getNumberDoubleValueSilently(String fn, double defaultValue) {
		try {
			String value[] = data.get(fn);
			return Double.parseDouble(value[0].trim());
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public String[] getListOfValuesSilently(String fn) {
		String value[] = data.get(fn);
		if (value != null) {
			return value;
		} else {
			String val[] = {};
			return val;
		}

	}

	public String[] getListOfStringValues(String fn, String defaultVal) {
		String value[] = data.get(fn);
		if (value != null) {
			return value;
		} else {
			return new String[] { defaultVal };
		}

	}

	public String[] getListOfValues(String fn) throws _Exception {
		String value[] = data.get(fn);
		if (value != null) {
			return value;
		} else {
			throw new _Exception(_ExceptionType.FORMDATA_INCORRECT, "value of field=" + fn + " has not resolved");
		}

	}

	// TODO need to replace _Exception to WebFormValueExceptionType
	public String getValue(String fn) throws _Exception {
		try {
			String value[] = data.get(fn);
			return value[0].trim();
		} catch (Exception e) {
			throw new _Exception(_ExceptionType.FORMDATA_INCORRECT, "value of field=" + fn + " has not resolved");
		}
	}

	public Map<String, String[]> getFormData() {
		return data;
	}

	public boolean containsField(String key) {
		return data.containsKey(key);
	}

	public int getSizeOfField(String fn) {
		try {
			String value[] = data.get(fn);
			return value.length;
		} catch (Exception e) {
			return 0;
		}
	}

	public Date getDateSilently(String fn) {
		try {
			String value[] = data.get(fn);
			return Util.convertStringToSimpleDate(value[0].trim());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String toString() {
		String result = "-----------properties of the web form data-----------\n";

		result += "REFERRER = " + referrer;
		Iterator<String> en = data.keySet().iterator();

		while (en.hasNext()) {
			String webFormFieldName = en.next();
			String[] val = data.get(webFormFieldName);
			String v = "";
			for (int i = 0; i < val.length; i++) {
				v += val[i] + "[" + Integer.toString(i) + "],";
			}
			result += " " + webFormFieldName + " = " + v + "\n";
		}

		result += "-----------------------------------------------------";

		return result;

	}

	public String getReferrer() {
		return referrer;
	}

	public void setReferer(String referrer) {
		this.referrer = referrer;
	}

	public boolean getBoolSilently(String fn) {
		try {
			String value[] = data.get(fn);
			if (value[0].equalsIgnoreCase("true") || value[0].equals("1")) {
				return true;
			}
		} catch (Exception e) {

		}
		return false;
	}

}
