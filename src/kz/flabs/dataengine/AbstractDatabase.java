package kz.flabs.dataengine;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

import com.exponentus.appenv.AppEnv;
import com.exponentus.dataengine.IDBConnectionPool;
import com.exponentus.dataengine.IDatabase;
import com.exponentus.dataengine.IFTIndexEngine;
import com.exponentus.dataengine.exception.DatabasePoolException;

import kz.flabs.users.User;

public abstract class AbstractDatabase implements IDatabase {
	protected AppEnv env;
	protected String dbID;

	public boolean clearDocuments() {
		return false;
	}

	@Override
	public IDBConnectionPool getConnectionPool() {
		return null;
	}

	@Override
	public IFTIndexEngine getFTSearchEngine() {

		return null;
	}

	public int shutdown() {

		return 0;
	}

	public int getAllDocumentsCount(int docType, Set<String> complexUserID, String absoluteUserID) {

		return 0;
	}

	public int getDocumentsCountByCondition(String query, Set<String> complexUserID, String absoluteUserID) {

		return 0;
	}

	public boolean hasResponse(int docID, int docType, Set<String> complexUserID, String absoluteUserID) {

		return false;
	}

	public boolean hasResponse(Connection conn, int docID, int docType, Set<String> complexUserID, String absoluteUserID) {

		return false;
	}

	public boolean hasDocumentByComplexID(int docID, int docType) {

		return false;
	}

	public void deleteDocument(int docType, int docID, User user, boolean completely)
	        throws SQLException, DatabasePoolException, InstantiationException, IllegalAccessException, ClassNotFoundException {

	}

	public void deleteDocument(String id, boolean completely, User user)
	        throws SQLException, DatabasePoolException, InstantiationException, IllegalAccessException, ClassNotFoundException {

	}

	public boolean unDeleteDocument(String id, User user) {

		return false;
	}

	public boolean unDeleteDocument(int aid, User user) {

		return false;
	}

	public int getRegNum(String key) {

		return 0;
	}

	public int postRegNum(int num, String key) {

		return 0;
	}

	public StringBuffer getCounters() {

		return null;
	}

	public StringBuffer getPatches() {

		return null;
	}

	public String getFieldByComplexID(int docID, int docType, String fieldName) {

		return null;
	}

	public String getDocumentAttach(int docID, int docType, Set<String> complexUserID, String fieldName, String fileName) {

		return null;
	}

	public int randomBinary() {

		return 0;
	}

	public ArrayList<Integer> getAllDocumentsIDS(int docType, Set<String> complexUserID, String absoluteUserID, String[] fields, int offset,
	        int pageSize) {

		return null;
	}

	public ArrayList<Integer> getAllDocumentsIDS(int docType, Set<String> complexUserID, String absoluteUserID, int start, int end) {

		return null;
	}

	public ArrayList<Integer> getAllDocumentsIDsByCondition(String query, int docType, Set<String> complexUserID, String absoluteUserID) {

		return null;
	}

	public String getMainDocumentFieldValueByID(int docID, Set<String> complexUserID, String absoluteUserID, String fieldName) {

		return null;
	}

	public String getGlossaryCustomFieldValueByID(int docID, String fieldName) {

		return null;
	}

	public StringBuffer getUsersRecycleBin(int offset, int pageSize, String userID) {

		return null;
	}

	public int getDocsCountByCondition(String sql, Set<String> complexUserID, String absoluteUserID) {

		return 0;
	}

	public void setTopic(int topicID, int parentdocID, int parentDocType) {

	}

	public int calcStartEntry(int pageNum, int pageSize) {

		return 0;
	}

	public int getUsersRecycleBinCount(int calcStartEntry, int pageSize, String userID) {

		return 0;
	}

	public int getFavoritesCount(Set<String> complexUserID, String absoluteUserID) {

		return 0;
	}

	public void addCounter(String key, int num) {

	}

}
