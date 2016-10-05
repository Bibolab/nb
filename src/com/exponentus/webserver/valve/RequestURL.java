package com.exponentus.webserver.valve;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.exponentus.env.EnvConst;

public class RequestURL {

	private String appType = "";
	private String url;
	private String pageID = "";
	private String ip;
	private String agent;
	private boolean isLogout;

	public RequestURL(String url) {
		this.url = url;
		String urlVal = url != null ? url.trim() : "";
		Pattern pattern = Pattern.compile("^/(\\p{Alpha}+)(/[\\p{Lower}0-9]{16})?.*$");
		Matcher matcher = pattern.matcher(urlVal);
		if (matcher.matches()) {
			appType = matcher.group(1) == null ? "" : matcher.group(1);
		}

		if (!isPage()) {
			return;
		}

		for (String pageIdRegex : new String[] { "^.*/page/([\\w\\-~\\.]+)", "^.*/((Provider)|(P)|(p))\\?(.+&)?id=([\\w\\-~\\.]+).*" }) {
			Pattern pagePattern = Pattern.compile(pageIdRegex);
			Matcher pageMatcher = pagePattern.matcher(urlVal);
			if (pageMatcher.matches()) {
				pageID = pageMatcher.group(6);
				break;
			}
		}

	}

	public RequestURL(HttpServletRequest http) {
		String requestURI = http.getRequestURI();
		String params = http.getQueryString();

		if (params != null) {
			url = requestURI + "?" + http.getQueryString();
		} else {
			url = "";
		}

		String urlVal = url != null ? url.trim() : "";
		Pattern pattern = Pattern.compile("^/(\\p{Alpha}+)(/[\\p{Lower}0-9]{16})?.*$");
		Matcher matcher = pattern.matcher(urlVal);
		if (matcher.matches()) {
			appType = matcher.group(1) == null ? "" : matcher.group(1);
		}

		if (!isPage()) {
			return;
		}

		for (String pageIdRegex : new String[] { "^.*/page/([\\w\\-~\\.]+)", "^.*/((Provider)|(p))\\?(.+&)?id=([\\w\\-~\\.]+).*" }) {
			Pattern pagePattern = Pattern.compile(pageIdRegex);
			Matcher pageMatcher = pagePattern.matcher(urlVal);
			if (pageMatcher.matches()) {
				pageID = pageMatcher.group(6);
				break;
			}
		}
	}

	public String getAppType() {
		return appType;
	}

	public boolean isAuthRequest() {
		String ulc = url.toLowerCase();
		if (ulc.contains("login") || ulc.contains("/session")) {
			return true;
		} else if (ulc.contains("logout")) {
			isLogout = true;
			return true;
		}
		return false;
	}

	public boolean isLogout() {
		return isLogout;
	}

	// TODO it need to check Site class instance
	public boolean isRest() {
		return url.matches(".*/((rest)).*");
	}

	public boolean isPage() {
		return url.trim().length() == 0 || url.matches(".*/((Provider)|(P)|(p))\\?.*") || url.matches("/" + appType + "/*");
	}

	public String getPageID() {
		return pageID;
	}

	public String getUrl() {
		return url;
	}

	public boolean isProtected() {
		return !(url.startsWith("/" + EnvConst.SHARED_RESOURCES_APP_NAME) || isSimpleObject());
	}

	private boolean isSimpleObject() {
		return url.matches(".+\\.(" + "(css)|" + "(js)|" + "(htm)|" + "(html)|" + "(png)|" + "(jpg)|" + "(gif)|" + "(bmp))$");
	}

	public void setAppType(String templateType) {
		appType = templateType;

	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	@Override
	public String toString() {
		return url;
	}
}
