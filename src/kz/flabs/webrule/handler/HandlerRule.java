package kz.flabs.webrule.handler;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.w3c.dom.Node;

import com.exponentus.appenv.AppEnv;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import kz.flabs.dataengine.Const;
import kz.flabs.exception.RuleException;
import kz.flabs.sourcesupplier.DocumentCollectionMacro;
import kz.flabs.util.XMLUtil;
import kz.flabs.webrule.Rule;
import kz.flabs.webrule.constants.RuleType;
import kz.flabs.webrule.constants.RunMode;
import kz.flabs.webrule.constants.ValueSourceType;
import kz.flabs.webrule.scheduler.RunUnderUser;
import kz.flabs.webrule.scheduler.ScheduleSettings;

public class HandlerRule extends Rule implements Const {
	public RunUnderUser runUnderUser;
	public TriggerType trigger;
	public ToHandle toHandle;
	public DocumentCollectionMacro toHandleMacro;
	public ScheduleSettings scheduleSettings;
	public boolean waitResponse;
	public boolean showFile;
	public String description;
	public Class<GroovyObject> handlerClass;
	public boolean scriptIsValid;
	public ValueSourceType qsSourceType = ValueSourceType.STATIC;
	public String handlerClassName;

	private String script = "";

	public HandlerRule(AppEnv env, File file) throws IOException, RuleException {
		super(env, file);
		try {
			trigger = TriggerType.valueOf(XMLUtil.getTextContent(doc, "/rule/trigger", true, "UNKNOWN", false));
			if (trigger == TriggerType.SCHEDULER) {

				Node schedNode = XMLUtil.getNode(doc, "/rule/scheduler", true);
				if (schedNode != null) {
					scheduleSettings = new ScheduleSettings(file.getAbsolutePath(), schedNode);
				} else {
					AppEnv.logger.errorLogEntry("Schedule in " + id + " rule has not set");
					trigger = TriggerType.UNKNOWN;
					isValid = false;
					return;
				}
			}

			toHandle = new ToHandle(XMLUtil.getNode(doc, "/rule/tohandle", true));

			runUnderUser = new RunUnderUser(XMLUtil.getNode(doc, "/rule/rununderuser", true));
			script = XMLUtil.getTextContent(doc, "/rule/script");

			Node qsNode = XMLUtil.getNode(doc, "/rule/events/trigger", true);
			// handlerClassName = getClassName(qsNode);
			if (isOn != RunMode.OFF) {
				if (handlerClassName != null) {
					scriptIsValid = true;
				} else {
					ClassLoader parent = getClass().getClassLoader();
					GroovyClassLoader loader = new GroovyClassLoader(parent);
					try {

					} catch (MultipleCompilationErrorsException e) {
						AppEnv.logger.errorLogEntry("Handler Script compilation error at compiling=" + id + ":" + e.getMessage());
						isValid = false;
					}
				}
			}
			try {
				String mt = XMLUtil.getTextContent(doc, "/rule/waitresponse");
				if (mt.equalsIgnoreCase("true")) {
					waitResponse = true;
				} else {
					waitResponse = false;
				}
			} catch (Exception e) {
				waitResponse = false;
			}

			description = XMLUtil.getTextContent(doc, "rule/description");

			try {
				String mt = XMLUtil.getTextContent(doc, "/rule/showfile");
				if (mt.equalsIgnoreCase("true")) {
					showFile = true;
				} else {
					showFile = false;
				}
			} catch (Exception e) {
				showFile = false;
			}

			type = RuleType.HANDLER;
			isValid = true;
		} catch (Exception e) {
			AppEnv.logger.errorLogEntry(e);
		}
	}

	public void setScript(String script) {
		this.script = script;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "id=" + id + ", ison=" + isOn + ", schedule=" + scheduleSettings;
	}

	@Override
	public int hashCode() {
		return script.hashCode();
	}

	public int getCodeHash(String scriptText) {
		return scriptText.hashCode();
	}

	private static String getSignature(TriggerType trigger) {
		switch (trigger) {
		case PROVIDER:
			return "String doHandler(_Session ses, Map<String, String[]> formData){";
		case SCHEDULER:
			return "String doHandler(_Session session, _DocumentCollection collection){";
		case MANUALLY:
			return "String doHandler(_Session session, _DocumentCollection collection){";
		default:
			return "Unknown trigger";
		}

	}

}
