package com.exponentus.messaging.email;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import com.exponentus.dataengine.jpa.IAppEntity;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.exception.MsgException;
import com.exponentus.localization.Vocabulary;
import com.exponentus.log.JavaConsoleLogger;
import com.exponentus.server.Server;

public class MailAgent {

	public boolean sendMail(List<String> recipients, String subj, String body) {
		return sendMail(recipients, subj, body, null);
	}

	public boolean sendMail(List<String> recipients, String subj, String body, boolean async) {
		return sendMail(recipients, subj, body, null, async);
	}

	public boolean sendDebugMail(List<String> recipients, String subj, String body) throws MsgException {
		Memo memo = new Memo(recipients, subj, body, null);
		return memo.sendWithPassions();
	}

	public boolean sendMail(List<String> recipients, String subj, String body, IAppEntity entity, boolean async) {
		Memo memo = new Memo(recipients, subj, body, entity);
		if (async) {
			return sendMail(recipients, subj, body, entity);
		} else {
			return memo.send();
		}
	}

	public boolean sendMail(List<String> recipients, String subj, String body, IAppEntity entity) {
		Memo memo = new Memo(recipients, subj, body, entity);
		RunnableFuture<Boolean> f = new FutureTask<>(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return memo.send();
			}
		});
		new Thread(f).start();
		try {
			return f.get();
		} catch (InterruptedException | ExecutionException e) {
			Server.logger.errorLogEntry(e);
			return false;
		}
	}

	public static void main(String[] args) {
		Server.logger = new JavaConsoleLogger();
		EnvConst.DATABASE_NAME = "poema";
		Environment.vocabulary = new Vocabulary("test");
		Environment.init();
		MailAgent ma = new MailAgent();
		List<String> r = new ArrayList<String>();
		r.add("11111@gmail.com");
		ma.sendMail(r, "subject", " body");
	}

}
