package kz.flabs.runtimeobj.caching;

import java.io.IOException;

import com.exponentus.scripting._WebFormData;
import com.exponentus.scriptprocessor.page.PageOutcome;

import kz.flabs.exception.RuleException;
import kz.flabs.runtimeobj.page.Page;
import net.sf.saxon.s9api.SaxonApiException;

public interface ICache {

	PageOutcome getCachedPage(PageOutcome outcome, Page page, _WebFormData formData)
	        throws ClassNotFoundException, RuleException, IOException, SaxonApiException;

	void flush();
}
