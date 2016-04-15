package administrator.dao;

import java.util.UUID;

import com.exponentus.dataengine.jpa.DAO;
import com.exponentus.env.Environment;
import com.exponentus.scripting._Session;
import com.exponentus.user.SuperUser;

import administrator.model.Language;

public class LanguageDAO extends DAO<Language, UUID> {

	public LanguageDAO() {
		super(Language.class, new _Session(Environment.adminApplication, new SuperUser()));
	}

	public LanguageDAO(_Session session) {
		super(Language.class, session);
	}

}
