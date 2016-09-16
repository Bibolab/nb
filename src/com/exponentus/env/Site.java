package com.exponentus.env;

import java.util.List;

import com.exponentus.rest.RestType;
import com.exponentus.rule.constans.RunMode;

public class Site {
	public String siteName;
	public String name;

	private RunMode restIsOn = RunMode.OFF;
	private String restUrlMapping = "";
	private String allowCORS = "";
	private RestType restType;
	private List<String> restServices;

	public RunMode getRestIsOn() {
		return restIsOn;
	}

	public void setRestIsOn(RunMode restIsOn) {
		this.restIsOn = restIsOn;
	}

	public String getRestUrlMapping() {
		return normalizeRestURL(restUrlMapping);
	}

	public void setRestUrlMapping(String restUrlMapping) {
		this.restUrlMapping = restUrlMapping;
	}

	public String getAllowCORS() {
		return allowCORS;
	}

	public void setAllowCORS(String allowCORS) {
		this.allowCORS = allowCORS;
	}

	public RestType getRestType() {
		return restType;
	}

	public void setRestType(RestType restType) {
		this.restType = restType;
	}

	public List<String> getRestServices() {
		return restServices;
	}

	public void setRestServices(List<String> restServices) {
		this.restServices = restServices;
	}

	@Override
	public String toString() {
		return "name=" + name + ", siteName=" + siteName;
	}

	private static String normalizeRestURL(String v) {
		String v2 = v.substring(v.length() - 1, v.length());
		if (v2.equals("/") || v2.equals("*")) {
			if (v2.equals("/")) {
				return v.substring(0, v.length() - 1);
			} else {
				String v1 = v.substring(v.length() - 2, v.length());
				if (v1.equals("/*")) {
					return v.substring(0, v.length() - 2);
				} else {
					return v.substring(0, v.length() - 1);
				}
			}

		} else {
			return v;
		}
	}
}
