package com.exponentus.rule.page;

import java.util.ArrayList;

import com.exponentus.appenv.AppEnv;
import com.exponentus.rule.Caption;

public interface IElement {
	String getID();

	AppEnv getAppEnv();

	ArrayList<Caption> getCaptions();

}
