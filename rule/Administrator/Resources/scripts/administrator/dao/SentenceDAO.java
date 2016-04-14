package administrator.dao;

import java.util.UUID;

import com.exponentus.appenv.AppEnv;
import com.exponentus.dataengine.jpa.DAO;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.scripting._Session;
import com.exponentus.user.SuperUser;

import administrator.model.Sentence;

public class SentenceDAO extends DAO<Sentence, UUID> {

	public SentenceDAO() {
		super(Sentence.class, new _Session(new AppEnv(EnvConst.ADMINISTRATOR_APP_NAME, Environment.dataBase), new SuperUser()));
	}

	public SentenceDAO(_Session session) {
		super(Sentence.class, session);
	}

}
