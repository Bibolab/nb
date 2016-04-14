package kz.nextbase.script;

import com.exponentus.appenv.AppEnv;
import com.exponentus.scripting._Session;
import com.exponentus.server.Server;

//import kz.nextbase.script.struct._Employer;

public class _AppEntourage {
	private AppEnv env;
	private _Session ses;

	public _AppEntourage(_Session ses, AppEnv env) {
		this.ses = ses;
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
