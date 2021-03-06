package com.exponentus.legacy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import org.apache.catalina.realm.RealmBase;

import com.exponentus.appenv.AppEnv;
import com.exponentus.exception.WebFormValueException;
import com.exponentus.exception.WebFormValueExceptionType;
import com.exponentus.util.Util;

public class User {
	public int docID;
	public boolean authorized;
	public boolean authorizedByHash;

	public final static String ANONYMOUS_USER = "anonymous";

	private String userID;

	private String password;
	private String passwordHash = "";
	private String email = "";
	private boolean isSupervisor;
	private int hash;
	private String publicKey = "";
	private String userName;

	public User() {

		userID = ANONYMOUS_USER;
	}

	public User(AppEnv env) {

		userID = ANONYMOUS_USER;
	}

	public User(String u) {

		setUserID(u);
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publickey) {
		this.publicKey = publickey;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		try {
			this.userID = userID;
		} catch (Exception e) {

		}
	}

	public HashSet<String> getAllUserGroups() {
		HashSet<String> userGroups = new HashSet<String>();

		return userGroups;
	}

	public void fill(ResultSet rs) throws SQLException {
		try {
			docID = rs.getInt("DOCID");
			userID = rs.getString("USERID");
			setEmail(rs.getString("EMAIL"));
			password = rs.getString("PWD");
			passwordHash = rs.getString("PWDHASH");
			publicKey = rs.getString("PUBLICKEY");
			int isa = rs.getInt("ISADMIN");
			if (isa == 1) {
				isSupervisor = true;
			}
			setHash(rs.getInt("LOGINHASH"));

		} catch (Exception e) {

		}
	}

	public String getPassword() {
		return password;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPassword(String password) throws WebFormValueException {
		if (!("".equalsIgnoreCase(password))) {
			if (Util.pwdIsCorrect(password)) {
				this.password = password;
			} else {
				throw new WebFormValueException(WebFormValueExceptionType.FORMDATA_INCORRECT, "password");
			}
		}
	}

	public void setPasswordHash(String password) throws WebFormValueException {
		if (!("".equalsIgnoreCase(password))) {
			if (Util.pwdIsCorrect(password)) {
				this.passwordHash = RealmBase.Digest(password, "MD5", "UTF-8");
			} else {
				throw new WebFormValueException(WebFormValueExceptionType.FORMDATA_INCORRECT, "password");
			}
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) throws WebFormValueException {
		if (email != null) {
			if (email.equalsIgnoreCase("")) {
				this.email = "";
			} else if (Util.addrIsCorrect(email)) {
				this.email = email;
			} else {
				throw new WebFormValueException(WebFormValueExceptionType.FORMDATA_INCORRECT, "email");
			}
		}
	}

	public boolean isSupervisor() {
		return isSupervisor;
	}

	public int getIsAdmin() {
		if (isSupervisor) {
			return 1;
		} else {
			return 0;
		}
	}

	public void setAdmin(boolean isAdmin) {
		this.isSupervisor = isAdmin;
	}

	public void setAdmin(String isAdmin) {
		if (isAdmin.equalsIgnoreCase("1")) {
			this.isSupervisor = true;
		} else {
			this.isSupervisor = false;
		}
	}

	public void setAdmin(String[] isAdmin) {
		try {
			String value = isAdmin[0];
			setAdmin(value);
		} catch (Exception e) {
			this.isSupervisor = false;
		}
	}

	public void setHash(int hash) {
		this.hash = hash;
	}

	public int getHash() {
		return hash;
	}

	@Override
	public String toString() {
		return "userID=" + userID + ", email=" + email;
	}

	public String toXML() {
		return "<userid>" + userID + "</userid>";
	}

	public String usersByKeytoXML() {
		return "<userid>" + userID + "</userid>" + "<key>" + docID + "</key>" + "<email>" + email + "</email>";
	}

	public void setUserName(String name) {
		userName = name;
	}

	public String getUserName() {
		return userName;
	}

	public String getLogin() {
		return userID;
	}

	public AppEnv getAppEnv() {
		// TODO Auto-generated method stub
		return null;
	}
}
