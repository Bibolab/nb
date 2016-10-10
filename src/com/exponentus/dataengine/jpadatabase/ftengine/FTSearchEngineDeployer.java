package com.exponentus.dataengine.jpadatabase.ftengine;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import javax.persistence.Column;
import javax.persistence.Table;

import com.exponentus.dataengine.DatabaseUtil;
import com.exponentus.dataengine.IDBConnectionPool;
import com.exponentus.dataengine.IFTEngineDeployer;
import com.exponentus.dataengine.jpa.IAppEntity;

public class FTSearchEngineDeployer<T extends IAppEntity> implements IFTEngineDeployer<T> {
	public static final String FT_INDEX_COLUMN = "ft_index_col";

	private IDBConnectionPool dbPool;
	private static final String FT_INDEX_UPDATE_TRIGGER = "ft_index_updater";
	private static final String FT_INDEX = "ft_idx";

	public FTSearchEngineDeployer(IDBConnectionPool dbPool) {
		this.dbPool = dbPool;
	}

	public void init() {

	}

	@Override
	public boolean createIndex(Class<T> clazz) {
		String tableName = clazz.getSimpleName();
		if (clazz.isAnnotationPresent(Table.class)) {
			Table tableAn = clazz.getAnnotation(Table.class);
			String value = tableAn.name();
			if (value != null) {
				tableName = value;
			}
		}

		StringJoiner ddlPiece1 = new StringJoiner("||");
		StringJoiner ddlPiece2 = new StringJoiner(",");

		final List<Field> allFields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
		for (final Field f : allFields) {
			if (f.isAnnotationPresent(FTSearchable.class)) {
				String fieldName = f.getName();
				boolean isNullable = false;
				if (f.isAnnotationPresent(Column.class)) {
					String value = f.getAnnotation(Column.class).name();
					if (!value.isEmpty()) {
						fieldName = value;
					}
					isNullable = f.getAnnotation(Column.class).nullable();
				}

				if (isNullable) {
					ddlPiece1.add("coalesce(" + fieldName + ",'')");

				} else {
					ddlPiece1.add("'" + fieldName + "'");
				}

				ddlPiece2.add("'" + fieldName + "'");
			}
		}

		return createIndex(tableName, ddlPiece1.toString(), ddlPiece2.toString(), "english");
	}

	@Override
	public boolean dropIndex(Class<T> clazz) {
		String tableName = clazz.getSimpleName();
		if (clazz.isAnnotationPresent(Table.class)) {
			Table tableAn = clazz.getAnnotation(Table.class);
			String value = tableAn.name();
			if (value != null) {
				tableName = value;
			}
		}
		return dropIndex(tableName);
	}

	private boolean createIndex(String tableName, String ddl1, String ddl2, String lang) {
		StringJoiner sql = new StringJoiner(";");
		sql.add("ALTER TABLE " + tableName + " ADD COLUMN " + FT_INDEX_COLUMN + " tsvector");
		sql.add("UPDATE " + tableName + " SET " + FT_INDEX_COLUMN + " = to_tsvector('" + lang + "', " + ddl1 + ")");
		sql.add("CREATE INDEX " + FT_INDEX + " ON " + tableName + " USING GIN (" + FT_INDEX_COLUMN + ")");
		sql.add("CREATE TRIGGER " + FT_INDEX_UPDATE_TRIGGER + " BEFORE INSERT OR UPDATE ON " + tableName
		        + " FOR EACH ROW EXECUTE PROCEDURE tsvector_update_trigger(" + FT_INDEX_COLUMN + ", 'pg_catalog." + lang + "', " + ddl2 + ")");
		Connection conn = dbPool.getConnection();
		try (Statement s = conn.createStatement();) {
			conn.setAutoCommit(false);
			s.executeUpdate(sql.toString());
			conn.commit();
			return true;
		} catch (SQLException e) {
			DatabaseUtil.debugErrorPrint(e);
		} finally {
			dbPool.returnConnection(conn);
		}
		return true;
	}

	public boolean dropIndex(String tableName) {
		StringJoiner sql = new StringJoiner(";");
		sql.add("ALTER TABLE " + tableName + " DROP COLUMN IF EXISTS " + FT_INDEX_COLUMN);
		sql.add("DROP TRIGGER IF EXISTS " + FT_INDEX_UPDATE_TRIGGER + " ON " + tableName + " CASCADE");
		Connection conn = dbPool.getConnection();
		try (Statement s = conn.createStatement();) {
			conn.setAutoCommit(false);
			s.executeUpdate(sql.toString());
			conn.commit();
			return true;
		} catch (SQLException e) {
			DatabaseUtil.debugErrorPrint(e);
		} finally {
			dbPool.returnConnection(conn);
		}
		return true;
	}

}
