package administrator.page.form;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.exponentus.env.Environment;
import com.exponentus.scripting._Session;
import com.exponentus.scripting._WebFormData;
import com.exponentus.scripting.actions._Action;
import com.exponentus.scripting.actions._ActionBar;
import com.exponentus.scripting.actions._ActionType;
import com.exponentus.scripting.event._DoForm;
import com.exponentus.util.Util;

public class ServerForm extends _DoForm {

	@Override
	public void doGET(_Session session, _WebFormData formData) {
		addValue("hostname", Environment.hostName);
		addValue("port", Environment.httpPort);
		addValue("tmpdir", Environment.tmpDir);
		addValue("orgname", Environment.orgName);
		addValue("database", Environment.adminApplication.getDataBase().getInfo());
		addValue("devmode", Environment.isDevMode());
		addValue("officeframe", Environment.getOfficeFrameDir());
		addValue("kernel", Environment.getKernelDir());
		addValue("starttime", Util.convertDataTimeToString(Environment.startTime));
		addValue("devmode", Environment.isDevMode());
		_ActionBar actionBar = new _ActionBar(session);
		actionBar.addAction(new _Action("Save", "Save and close the form", _ActionType.SAVE_AND_CLOSE));
		actionBar.addAction(new _Action("Close", "Just close the form", _ActionType.CLOSE));
		addContent(actionBar);
	}

	@Override
	public void doPUT(_Session session, _WebFormData formData) {
		devPrint(formData);
		String org = formData.getValueSilently("orgname");
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse("cfg.xml");
			doc.setXmlStandalone(true);

			Node orgName = doc.getElementsByTagName("orgname").item(0);
			orgName.setTextContent(org);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("cfg.xml"));
			transformer.transform(source, result);

		} catch (Exception e) {
			setBadRequest();
			logError(e);
		}
	}

	@Override
	public void doPOST(_Session session, _WebFormData formData) {

	}

	@Override
	public void doDELETE(_Session session, _WebFormData formData) {

	}

}
