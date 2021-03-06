package com.exponentus.webserver.servlet;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.exponentus.appenv.AppEnv;
import com.exponentus.dataengine.IDatabase;
import com.exponentus.dataengine.IFTIndexEngine;
import com.exponentus.dataengine.jpadatabase.Database;
import com.exponentus.dataengine.jpadatabase.ftengine.FTEntity;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.server.Server;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PortalInit extends HttpServlet {

	private static final long serialVersionUID = -8913620140247217298L;
	private boolean isValid;

	@Override
	public void init(ServletConfig config) throws ServletException {
		ServletContext context = config.getServletContext();
		String contextName = context.getServletContextName();
		Server.logger.infoLogEntry("# Start application \"" + contextName + "\"");
		try {
			IDatabase db = null;
			AppEnv env = null;
			if ("welcome".equals(contextName)) {
				db = new Database(EnvConst.WELCOME_APPLICATION);
				env = new AppEnv(EnvConst.WELCOME_APPLICATION, db);
			} else {
				db = new Database(contextName);
				env = new AppEnv(contextName, db);
			}
			try {
				Class<?> c = Class.forName(env.appName.toLowerCase() + ".init.AppConst");

				Field f = c.getDeclaredField("FT_INDEX_SCOPE");
				String result = (String) f.get(null);
				if (!result.isEmpty()) {
					ObjectMapper mapper = new ObjectMapper();
					ArrayList<FTEntity> fEntList = mapper.readValue(result, new TypeReference<ArrayList<FTEntity>>() {
					});
					IFTIndexEngine ftEngine = db.getFTSearchEngine();
					for (FTEntity fEnt : fEntList) {
						ftEngine.registerTable(fEnt);
					}
				}

				String dp = (String) c.getDeclaredField("DEFAULT_PAGE").get(null);
				if (!dp.isEmpty()) {
					env.setDefaultPage(dp);
				}

				String mv = (String) c.getDeclaredField("MODULE_VERSION").get(null);
				if (!mv.isEmpty()) {
					// env.setModuleVer(mv);
				}
			} catch (ClassNotFoundException e) {

			}

			isValid = true;

			if (isValid) {
				Environment.addApplication(env);
			}

			if (isValid) {
				context.setAttribute(EnvConst.APP_ATTR, env);
			}

		} catch (Exception e) {
			Server.logger.errorLogEntry(e);
		}

	}

}
