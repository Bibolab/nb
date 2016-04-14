package com.exponentus.webserver.servlet;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.exponentus.appenv.AppEnv;
import com.exponentus.dataengine.jpadatabase.Database;
import com.exponentus.dataengine.jpadatabase.ftengine.FTEntity;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.server.Server;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kz.flabs.dataengine.IDatabase;
import kz.flabs.dataengine.IFTIndexEngine;

public class PortalInit extends HttpServlet {

	private static final long serialVersionUID = -8913620140247217298L;
	private boolean isValid;

	@Override
	public void init(ServletConfig config) throws ServletException {
		ServletContext context = config.getServletContext();
		String contextName = context.getServletContextName();
		Server.logger.infoLogEntry("# Start application \"" + contextName + "\"");
		try {
			IDatabase db = new Database(contextName);
			AppEnv env = new AppEnv(contextName, db);
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
