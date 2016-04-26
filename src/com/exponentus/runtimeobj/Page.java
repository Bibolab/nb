package com.exponentus.runtimeobj;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpStatus;

import com.exponentus.appenv.AppEnv;
import com.exponentus.exception.RuleException;
import com.exponentus.localization.LanguageCode;
import com.exponentus.rule.Caption;
import com.exponentus.rule.page.ElementRule;
import com.exponentus.rule.page.PageRule;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scriptprocessor.page.DoProcessor;
import com.exponentus.scriptprocessor.page.PageOutcome;

public class Page {
	public boolean fileGenerated;
	public boolean toJSON;
	public String generatedFilePath;
	public String generatedFileOriginalName;
	public int status = HttpStatus.SC_OK;
	protected AppEnv env;
	protected PageRule rule;

	protected _WebFormData fields;

	protected _Session ses;

	public Page(AppEnv env, _Session ses, PageRule pageRule) {
		this.ses = ses;
		this.env = env;
		this.rule = pageRule;
	}

	public String getCacheID() {
		return "PAGE_" + env.appName + "_" + rule.id + "_" + ses.getLang();

	}

	public PageOutcome getPageContent(PageOutcome outcome, _WebFormData webFormData, String method) throws ClassNotFoundException, RuleException {
		fields = webFormData;

		if (rule.elements.size() > 0) {
			for (ElementRule elementRule : rule.elements) {

				switch (elementRule.type) {
				case SCRIPT:
					DoProcessor sProcessor = new DoProcessor(outcome, ses, fields);
					switch (elementRule.doClassName.getType()) {
					case GROOVY_FILE:
						outcome = sProcessor.processScenario(elementRule.doClassName.getClassName(), method);
						break;
					case JAVA_CLASS:
						outcome = sProcessor.processScenario(elementRule.doClassName.getClassName(), method);
						break;
					case UNKNOWN:
						break;
					default:
						break;

					}
					outcome.setScriptResult(true);
					break;
				case INCLUDED_PAGE:
					PageRule rule = env.ruleProvider.getRule(elementRule.value);
					if (method.equalsIgnoreCase("GET")) {
						IncludedPage page = new IncludedPage(env, ses, rule);
						PageOutcome includedOutcome = new PageOutcome();
						switch (rule.caching) {
						case NO_CACHING:
							outcome.addPageOutcome(page.getPageContent(includedOutcome, fields, method));
							break;
						case CACHING_IN_USER_SESSION_SCOPE:
							outcome.addPageOutcome(page.getPageContent(includedOutcome, fields, method));
							break;
						case CACHING_IN_APPLICATION_SCOPE:
							outcome.addPageOutcome(page.getPageContent(includedOutcome, fields, method));
							break;
						case CACHING_IN_SERVER_SCOPE:
							outcome.addPageOutcome(page.getPageContent(includedOutcome, fields, method));
							break;
						default:
							outcome.addPageOutcome(page.getPageContent(includedOutcome, fields, method));
						}
					} else {
						IncludedPage page = new IncludedPage(env, ses, rule);
						PageOutcome includedOutcome = new PageOutcome();
						outcome.addPageOutcome(page.getPageContent(includedOutcome, fields, method));
						break;
					}
					break;
				default:
					break;
				}

				if (elementRule.hasElementName) {
					outcome.setName(elementRule.name);
				}
			}

		}
		outcome.setPageId(rule.id);
		outcome.setCaptions(

		getCaptions(rule.captions, ses.getLang()));
		return outcome;
	}

	private HashMap<String, String> getCaptions(ArrayList<Caption> captions, LanguageCode lang) {
		HashMap<String, String> translated = new HashMap<String, String>();
		for (Caption cap : captions) {
			translated.put(cap.captionID, env.vocabulary.getWord(cap.captionID, lang));
		}
		return translated;
	}

}
