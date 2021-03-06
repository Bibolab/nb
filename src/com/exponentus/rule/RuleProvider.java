package com.exponentus.rule;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

import com.exponentus.appenv.AppEnv;
import com.exponentus.exception.RuleException;
import com.exponentus.rule.page.PageRule;

public class RuleProvider {

	private HashMap<String, PageRule> pageRuleMap = new HashMap<>();
	private AppEnv env;

	public RuleProvider(AppEnv env) {
		try {
			this.env = env;
		} catch (Exception ne) {
			AppEnv.logger.errorLogEntry(ne);
		}
	}

	public Collection<PageRule> getPageRules(boolean reload) throws RuleException {
		if (reload) {
			pageRuleMap.clear();

		}
		return pageRuleMap.values();

	}

	public boolean resetRules(boolean showConsoleOutput) {
		if (showConsoleOutput) {
			System.out.println("reload \"" + env.appName + "\" application rules ...");
		}
		pageRuleMap.clear();
		if (showConsoleOutput) {
			System.out.println("application rules have been reset");
		}
		return true;
	}

	public PageRule getRule(String id) throws RuleException {
		File docFile = null;
		if (id != null) {
			String ruleID = id.toLowerCase();
			PageRule rule = null;

			if (pageRuleMap.containsKey(ruleID)) {
				rule = pageRuleMap.get(ruleID);
			} else {
				docFile = new File(env.getRulePath() + File.separator + "Page" + File.separator + ruleID + ".xml");
				rule = new PageRule(env, docFile);
				pageRuleMap.put(ruleID.toLowerCase(), rule);
			}

			rule.plusHit();
			return rule;
		} else {
			return null;
		}

	}
}
