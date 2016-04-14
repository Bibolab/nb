package kz.flabs.runtimeobj.page;

import com.exponentus.appenv.AppEnv;
import com.exponentus.rule.page.PageRule;
import com.exponentus.scripting._Session;

import kz.flabs.dataengine.Const;

public class IncludedPage extends Page implements Const {

	public IncludedPage(AppEnv env, _Session ses, PageRule rule) {
		super(env, ses, rule);
	}

	@Override
	public String getCacheID() {
		return "INCLUDED_PAGE_" + env.appName + "_" + rule.id + "_" + ses.getLang();

	}

}
