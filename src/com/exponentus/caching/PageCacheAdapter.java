package com.exponentus.caching;

import java.io.IOException;
import java.util.HashMap;

import com.exponentus.exception.RuleException;
import com.exponentus.runtimeobj.Page;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scriptprocessor.page.PageOutcome;

import net.sf.saxon.s9api.SaxonApiException;

public abstract class PageCacheAdapter implements ICache {
	private HashMap<String, Object> cache = new HashMap<String, Object>();

	@Override
	public PageOutcome getCachedPage(PageOutcome outcome, Page page, _WebFormData formData)
	        throws ClassNotFoundException, RuleException, IOException, SaxonApiException {
		String cacheKey = page.getCacheID();
		Object obj = cache.get(cacheKey);
		String cacheParam[] = formData.getFormData().get("cache");
		if (cacheParam == null) {
			PageOutcome buffer = page.getPageContent(outcome, formData, "GET");
			cache.put(cacheKey, buffer.getValue());
			return buffer;
		} else if (cacheParam[0].equalsIgnoreCase("reload")) {
			PageOutcome buffer = page.getPageContent(outcome, formData, "GET");
			cache.put(cacheKey, buffer.getValue());
			return buffer;
		} else {
			return (PageOutcome) obj;
		}
	}

	@Override
	public void flush() {
		cache.clear();

	}

	public String getCacheInfo() {
		String ci = "";
		for (String c : cache.keySet()) {
			ci = ci + "," + c;
		}
		if (ci.equals("")) {
			ci = "cache is empty";
		}
		return ci;
	}
}
