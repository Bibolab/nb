package com.exponentus.scripting.outline;

import java.util.ArrayList;

import com.exponentus.rule.constans.RunMode;
import com.exponentus.util.XMLUtil;

public class _OutlineEntry {
	public RunMode isOn = RunMode.ON;
	public String caption;
	public String hint;
	public String url;
	public String customID;
	public int unread;

	private String value = "";
	private ArrayList<_OutlineEntry> entries = new ArrayList<_OutlineEntry>();

	public _OutlineEntry(String caption, String hint, String customID, String url) {
		this.caption = caption;
		this.hint = hint;
		this.url = url;
		this.customID = customID;
	}

	public _OutlineEntry(String caption, String entityName) {
		this.caption = caption;
		this.hint = caption;
		this.url = "p?id=" + entityName;
		this.customID = entityName;
	}

	public void setValue(String v) {
		value = v;
	}

	public void setValue(int v) {
		value = Integer.toString(v);
	}

	public void addEntry(_OutlineEntry entry) {
		entries.add(entry);
	}

	public String toXML() {
		String a = "";

		for (_OutlineEntry e : entries) {
			a += e.toXML();
		}

		return "<entry mode=\"" + isOn + "\"" + XMLUtil.getAsAttribute("url", url) + XMLUtil.getAsAttribute("id", customID)
		        + XMLUtil.getAsAttribute("caption", caption) + XMLUtil.getAsAttribute("hint", hint)
		        + XMLUtil.getAsAttribute("unread", String.valueOf(unread)) + ">" + XMLUtil.getAsTagValue(value) + a + "</entry>";
	}

	public Object toJSON() {
		return this;
	}
}
