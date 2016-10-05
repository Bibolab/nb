package com.exponentus.webserver.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;

import com.exponentus.appenv.AppEnv;
import com.exponentus.env.EnvConst;
import com.exponentus.env.Environment;
import com.exponentus.exception.ApplicationException;
import com.exponentus.scripting._FormAttachments;
import com.exponentus.scripting._Session;
import com.exponentus.server.Server;

public class UploadFile extends HttpServlet {
	private static final long serialVersionUID = -6070611526857723049L;
	private ServletContext context;

	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			context = config.getServletContext();
		} catch (Exception e) {
			Server.logger.errorLogEntry(e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession jses = req.getSession(false);
		_Session ses = (_Session) jses.getAttribute(EnvConst.SESSION_ATTR);
		FileItem fsidItem = null;
		String fn = "", fieldName = "", sign = "";

		String time = req.getParameter(EnvConst.TIME_FIELD_NAME);
		File userTmpDir = new File(Environment.tmpDir + File.separator + ses.getUser().getUserID());
		if (!userTmpDir.exists()) {
			userTmpDir.mkdir();
		}
		FileItem file = null;
		File uploadedFile = null;
		File repository = new File(Environment.trash);

		DiskFileItemFactory factory = newDiskFileItemFactory(context, repository);
		factory.setRepository(repository);

		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setProgressListener(getProgressListener(time, jses));

		StringBuilder sb = new StringBuilder();
		sb.append("{\"files\":[");
		List<String> fns = new ArrayList<>();

		try {
			List<FileItem> items = upload.parseRequest(req);
			for (FileItem item : items) {
				if (item.isFormField()) {
					// System.out.println(">>>>>>>form value = " +
					// item.getString() + " " + item.getFieldName());
					String formFieldNameField = item.getFieldName();
					if (formFieldNameField.endsWith(EnvConst.FSID_FIELD_NAME)) {
						fsidItem = item;
					} else if (formFieldNameField.equalsIgnoreCase("fieldname")) {
						fieldName = item.getString();
					} else if (formFieldNameField.equalsIgnoreCase("sign")) {
						sign = item.getString();
					}

				} else {
					file = item;

				}
			}

			fn = file.getName();
			if (fn != null) {
				fn = FilenameUtils.getName(fn);
			}
			File dirToUploadedFile = new File(userTmpDir.getAbsolutePath() + File.separator + fsidItem.getString());
			if (!dirToUploadedFile.exists()) {
				dirToUploadedFile.mkdir();
			}
			uploadedFile = new File(dirToUploadedFile + File.separator + fn);
			jses.setAttribute("filename", fn);
			file.write(uploadedFile);
			fns.add("\"" + fn + "\"");

		} catch (FileUploadException e) {
			req.getSession().removeAttribute(time);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		sb.append(fns.stream().collect(Collectors.joining(",")));
		sb.append("]}");

		if (fsidItem != null) {
			System.out.println(">>" + ses.hashCode());
			_FormAttachments attachs = ses.getFormAttachments(fsidItem.getString());
			if (sign != null && !sign.isEmpty()) {
				attachs.addFileWithSign(uploadedFile, fn, fieldName, sign);
			} else {
				attachs.addFile(uploadedFile, fn, fieldName);
			}
			resp.setContentType(ContentType.APPLICATION_JSON.toString());
			PrintWriter out = resp.getWriter();
			out.println(sb.toString());
			out.close();
		} else {
			String msg = "a field \"" + EnvConst.FSID_FIELD_NAME + "\" has not been pointed in the form";
			Server.logger.errorLogEntry(msg);
			ApplicationException ae = new ApplicationException(((AppEnv) context.getAttribute(EnvConst.APP_ATTR)).appName, msg, ses.getLang());
			resp.setStatus(HttpStatus.SC_BAD_REQUEST);
			resp.setContentType("text/html");
			resp.getWriter().println(ae.getHTMLMessage());
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String time = req.getParameter("time");
		String appendPiece = "}}";
		Object o = req.getSession().getAttribute(time);
		String progress = (o == null) ? "100.0" : o + "";

		if (progress.startsWith("100")) {
			HttpSession jses = req.getSession();
			jses.removeAttribute(time);
			appendPiece = ", \"filename\":\"" + jses.getAttribute("filename") + "\"}}";
		}

		StringBuilder sb = new StringBuilder();
		// String jsonString = new Gson().toJson(map)
		// sb.append("{progress:
		// {").append(time).append(":").append(progress).append("}}");
		sb.append("{\"progress\": {\"").append(time).append("\":\"").append(progress).append("\"").append(appendPiece);
		// sb.append(",filename:\"exel.xls\"}}");
		// System.out.println(sb.toString());
		resp.setContentType(ContentType.APPLICATION_JSON.toString());
		PrintWriter out = resp.getWriter();
		out.println(sb.toString());
		out.close();
	}

	private static DiskFileItemFactory newDiskFileItemFactory(ServletContext context, File repository) {
		FileCleaningTracker fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(context);
		DiskFileItemFactory factory = new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, repository);
		factory.setFileCleaningTracker(fileCleaningTracker);
		return factory;
	}

	private static ProgressListener getProgressListener(final String id, final HttpSession sess) {
		ProgressListener progressListener = new ProgressListener() {
			@Override
			public void update(long pBytesRead, long pContentLength, int pItems) {
				sess.setAttribute(id, ((double) pBytesRead / (double) pContentLength) * 100);
			}
		};
		return progressListener;
	}
}
