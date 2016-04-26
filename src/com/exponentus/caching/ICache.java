package com.exponentus.caching;

import java.io.IOException;

import com.exponentus.exception.RuleException;
import com.exponentus.runtimeobj.Page;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scriptprocessor.page.PageOutcome;

import net.sf.saxon.s9api.SaxonApiException;

public interface ICache {

	PageOutcome getCachedPage(PageOutcome outcome, Page page, _WebFormData formData)
	        throws ClassNotFoundException, RuleException, IOException, SaxonApiException;

	void flush();
}
