package kz.flabs.scriptprocessor;

import com.exponentus.log.ILogger;
import com.exponentus.server.Server;

import groovy.lang.GroovyObject;

public class ScriptProcessor implements IScriptProcessor {
	public static ILogger logger = Server.logger;

	@Override
	public String[] processString(String script) {
		Server.logger.errorLogEntry("method 4563 has not reloaded");
		return null;
	}

	@Override
	public String process(String script) {
		Server.logger.errorLogEntry("method 4564 has not reloaded");
		return "";
	}

	@Override
	public String[] processString(Class<GroovyObject> compiledClass) {
		Server.logger.errorLogEntry("method 4565 has not reloaded");
		return null;
	}

}
