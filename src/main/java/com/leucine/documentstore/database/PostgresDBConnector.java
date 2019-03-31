package com.leucine.documentstore.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class PostgresDBConnector {

	private static PostgresDBConnector pg;
	
	private Connection connection = null;
	
	private static final String DB_NAME = "newdb";
	
	private static final String USER_NAME = "lakshmanan-zt71";
	
	private static final String PASSWORD = "demo1234";
	
	private PostgresDBConnector() {
		try {
			Class.forName("org.postgresql.Driver");
			connection =  DriverManager.getConnection(
					"jdbc:postgresql://127.0.0.1:5432/"+DB_NAME, USER_NAME,
					PASSWORD);
		}
		catch(ClassNotFoundException e ) {
			throw new RuntimeException(e.getMessage());
		}
		catch(SQLException sqlExcep) {
			throw new RuntimeException(sqlExcep.getMessage());
		}
		
	}
	
	public static PostgresDBConnector getInstance() {
		if(pg == null) {
			pg = new PostgresDBConnector();
		}
		return pg;
	}
	
	public ResultSet insert(String tableName,Map<String,Object> values)throws SQLException {
		StringBuilder sql = new StringBuilder( "INSERT INTO "+tableName+" (");
		for(String key: values.keySet()) {
			sql.append(key+",");
		}
		sql.replace(sql.length()-1, sql.length(), ")");
		sql.append(" VALUES (");
		for (Map.Entry<String, Object> data : values.entrySet() ) {
			Object obj = data.getValue();
			if(obj instanceof String) {
				sql.append(" '"+obj+" ',");
			}
			else {
				sql.append(" "+obj+",");
			}
		}
		sql.replace(sql.length()-1, sql.length(), ");");
		 PreparedStatement ps = null;
		 ResultSet rs = null;
		 ps = connection.prepareStatement(sql.toString(),Statement.RETURN_GENERATED_KEYS);
		 ps.executeUpdate();
		 rs = ps.getGeneratedKeys();
		 return rs;
	}
	
	/**
	 * sample method to teest db;
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {
		Map<String,Object> obj = new HashMap<String,Object>();
		obj.put("NAME","Sample.txt");
		obj.put("CREATEDTIME", System.currentTimeMillis());
		obj.put("LAST_UPDATED_TIME", System.currentTimeMillis());
		PostgresDBConnector.getInstance().insert("FILE",obj);
	}
	
	public ResultSet getRow(String tableName, String columnName, Object value) throws SQLException {
		StringBuilder sql = new StringBuilder();
		if(value instanceof String)
			 sql = new StringBuilder( "SELECT * FROM "+tableName+" WHERE "+columnName+" = '"+value+"'");
		else
			 sql = new StringBuilder( "SELECT * FROM "+tableName+" WHERE "+columnName+" = "+value);
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql.toString());
		return resultSet;
	}
	
	public ResultSet getRows (String tableName, int limit, int from, String orderBy,boolean isAsc) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM "+tableName);
		if(orderBy!=null) {
			sql.append(" ORDER BY "+orderBy);
			if(isAsc){
				sql.append(" ASC");
			}
			else {
				sql.append(" DESC");
			}
		}
		if(limit>0) {
			sql.append(" LIMIT "+limit);
		}
		sql.append(" OFFSET "+from);
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql.toString());
		return resultSet;
	}
	
	public void updateRowColumn (String tableName,String updateColumnName,Object updateColumnValue,String conditionColumnName,Object conditionColumnValue) throws SQLException {
		StringBuilder sql = new StringBuilder();
		if(updateColumnValue instanceof String)
			 sql = new StringBuilder( "UPDATE "+tableName+" SET "+updateColumnName+" = '"+updateColumnValue+"'");
		else
			 sql = new StringBuilder( "UPDATE "+tableName+" SET "+updateColumnName+" = "+updateColumnValue);
		if(conditionColumnValue instanceof String)
			 sql.append(" WHERE "+conditionColumnName+" = '"+conditionColumnValue+ "'");
		else
			sql.append(" WHERE "+conditionColumnName+" = "+conditionColumnValue);
		Statement statement = connection.createStatement();
		statement.executeUpdate(sql.toString());
	}
	
	public void deleteRow ( String tableName, String columnName, Object value)throws SQLException {
		StringBuilder sql = new StringBuilder();
		if(value instanceof String)
			 sql = new StringBuilder( "DELETE  FROM "+tableName+" WHERE "+columnName+" = '"+value+"'");
		else
			 sql = new StringBuilder( "DELETE FROM "+tableName+" WHERE "+columnName+" = "+value);
		Statement statement = connection.createStatement();
		statement.executeUpdate(sql.toString());
	}
	
}