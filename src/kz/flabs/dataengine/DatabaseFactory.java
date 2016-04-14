package kz.flabs.dataengine;

import com.exponentus.appenv.AppEnv;
import com.exponentus.env.Environment;

public class DatabaseFactory implements Const {

	public static IDatabase getDatabase(String appName) {
		AppEnv appEnvironment = Environment.getApplication(appName);
		return appEnvironment.getDataBase();
	}

}
