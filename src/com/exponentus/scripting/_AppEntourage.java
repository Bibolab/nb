package com.exponentus.scripting;

import com.exponentus.appenv.AppEnv;
import com.exponentus.server.Server;

public class _AppEntourage {
	private AppEnv env;

	public _AppEntourage(_Session ses, AppEnv env) {

		this.env = env;
	}

	public String getServerVersion() {
		return Server.serverVersion;
	}

	public String getBuildTime() {
		return Server.compilationTime;
	}

	public String getAppName() {
		return env.appName;
	}

}
