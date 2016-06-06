package com.exponentus.messaging.email;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import com.exponentus.dataengine.jpa.IAppEntity;
import com.exponentus.server.Server;

public class MailAgent {

	public boolean sendMail(List<String> recipients, String subj, String body) {
		return sendMail(recipients, subj, body, null);
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

}
