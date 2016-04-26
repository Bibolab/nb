package com.exponentus.runtimeobj;

import com.exponentus.appenv.AppEnv;
import com.exponentus.rule.page.PageRule;
import com.exponentus.scripting._Session;

public class IncludedPage extends Page {

	public IncludedPage(AppEnv env, _Session ses, PageRule rule) {
		super(env, ses, rule);
	}

	@Override
	public String getCacheID() {
		return "INCLUDED_PAGE_" + env.appName + "_" + rule.id + "_" + ses.getLang();

	}

}
