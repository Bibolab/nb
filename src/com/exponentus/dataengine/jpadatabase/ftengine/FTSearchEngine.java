package com.exponentus.dataengine.jpadatabase.ftengine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.Table;

import com.exponentus.dataengine.IDBConnectionPool;
import com.exponentus.dataengine.IDatabase;
import com.exponentus.dataengine.IFTIndexEngine;
import com.exponentus.dataengine.jpa.IAppEntity;
import com.exponentus.dataengine.jpa.IDAO;
import com.exponentus.dataengine.jpa.ViewPage;
import com.exponentus.localization.LanguageCode;
import com.exponentus.scripting._Session;
import com.exponentus.server.Server;

public class FTSearchEngine<T> implements IFTIndexEngine<T> {
	private IDBConnectionPool dbPool;
	private List<FTEntity> indexTables = new ArrayList<>();
	private Class[] intArgsClass = new Class[] { _Session.class };

	public FTSearchEngine(IDatabase db) {
		this.dbPool = db.getConnectionPool();
	}

	@Override
	public ViewPage<T> search(Class<T> clazz, String keyword, _Session ses, int pageNum, int pageSize) {
		String tableName = clazz.getSimpleName();
		if (clazz.isAnnotationPresent(Table.class)) {
			Table tableAn = clazz.getAnnotation(Table.class);
			String value = tableAn.name();
			if (value != null) {
				tableName = value;
			}
		}

		String sql = "SELECT id FROM " + tableName + " WHERE " + FTSearchEngineDeployer.FT_INDEX_COLUMN + " @@ to_tsquery('" + keyword
		        + "') ORDER BY reg_date DESC LIMIT 10";

		Connection conn = dbPool.getConnection();
		try {
			PreparedStatement pst = conn.prepareStatement(sql.toString());
			ResultSet rs = pst.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Deprecated
	@Override
	public ViewPage<?> search(String keyWord, _Session ses, int pageNum, int pageSize) {
		if (keyWord == null || keyWord.trim().isEmpty() || indexTables.isEmpty()) {
			return null;
		}

		Connection conn = dbPool.getConnection();
		String lang = getLangString(ses.getLang());
		List result = new ArrayList<>();

		try {
			conn.setAutoCommit(false);

			StringBuilder sql = new StringBuilder();

			String tsVectorTemplate = "to_tsvector('" + lang + "', %s::character varying)";
			String sqlPart = "select '%s' as table_name, id from %s where (%s) @@ to_tsquery('" + lang + "', '" + keyWord + "') union all ";

			for (FTEntity table : indexTables) {
				String tsVectors = table.getFieldNames().stream().map(colName -> String.format(tsVectorTemplate, colName))
				        .collect(Collectors.joining("||"));
				sql.append(String.format(sqlPart, table.getTableName(), table.getTableName(), tsVectors));
			}

			sql.append("SELECT 'EMPTY', '00000000-0000-0000-0000-000000000000'::uuid;");

			PreparedStatement pst = conn.prepareStatement(sql.toString());
			ResultSet rs = pst.executeQuery();

			List<UUID> ids = new ArrayList<>();
			String currentTableName = "";
			while (rs.next()) {
				String tableName = rs.getString("table_name");
				if (!currentTableName.equals(tableName)) {
					final String finalCurrentTableName = currentTableName;
					Optional<FTEntity> table = indexTables.stream().filter(r -> r.getTableName().equals(finalCurrentTableName)).findFirst();

					if (ids.size() > 0 && table.isPresent()) {
						try {
							Constructor<?> constructor = table.get().getDaoImpl().getConstructor(intArgsClass);
							IDAO<? extends IAppEntity, UUID> dao = (IDAO<IAppEntity, UUID>) constructor.newInstance(ses);
							ViewPage<?> vPage = dao.findAllByIds(ids, pageNum, pageSize);
							result.addAll(vPage.getResult());
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
						        | NoSuchMethodException | SecurityException e) {
							Server.logger.errorLogEntry(e);
						}

					}

					currentTableName = tableName;
					ids.clear();
				}

				ids.add(UUID.fromString(rs.getString("id")));
			}

		} catch (Exception pe) {
			Server.logger.errorLogEntry(pe);
		} finally {
			dbPool.returnConnection(conn);
		}

		return new ViewPage<>(result, result.size(), pageNum, pageSize, keyWord);

	}

	@Override
	public void registerTable(FTEntity table) {
		indexTables.add(table);
	}

	private String getLangString(LanguageCode lang) {
		switch (lang) {
		case RUS:
			return "russian";
		case ENG:
			return "english";
		default:
			return "english";
		}

	}

}
