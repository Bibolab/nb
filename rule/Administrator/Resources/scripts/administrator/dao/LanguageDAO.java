package administrator.dao;

import java.util.UUID;

import com.exponentus.appenv.AppEnv;
import com.exponentus.dataengine.jpa.DAO;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.scripting._Session;
import com.exponentus.user.SuperUser;

import administrator.model.Language;

public class LanguageDAO extends DAO<Language, UUID> {

	public LanguageDAO() {
		super(Language.class, new _Session(new AppEnv(EnvConst.ADMINISTRATOR_APP_NAME, Environment.dataBase), new SuperUser()));
	}

	public LanguageDAO(_Session session) {
		super(Language.class, session);
	}

}
