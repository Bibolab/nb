package com.exponentus.rest.pojo;

import java.io.Serializable;

import com.exponentus.env.Environment;
import com.exponentus.exception.AuthFailedExceptionType;
import com.exponentus.localization.LanguageCode;

public class Credentials implements Serializable {
	private static final long serialVersionUID = 1L;
	private String login;
	private String pwd;
	private String error;
	private String token;

	public String getLogin() {
		return login;
	}

	public void setLogin(String username) {
		this.login = username;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public void setError(AuthFailedExceptionType error, LanguageCode lang) {
		this.error = Environment.vocabulary.getWord(error.name(), lang);

	}

	public String getError() {
		return error;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
