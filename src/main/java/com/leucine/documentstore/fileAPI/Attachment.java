package com.leucine.documentstore.fileAPI;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import com.leucine.documentstore.database.PostgresDBConnector;

public class Attachment {

	private Integer id;
	
	private Long createdTime;
	
	private Long lastUpdatedTime;
	
	private String name;
	
	public static final String FILE_TABLE="FILE";
	public static final String FILE_NAME_COLUMN="NAME";
	public static final String FILE_ID_COLUMN="FILE_ID";
	public static final String FILE_CREATEDTIME_COLUMN="CREATEDTIME";
	public static final String FILE_LAST_UPDATED_TIME_COLUMN="LAST_UPDATED_TIME";

	public Integer getId() {
		return id;
	}

	public Long getCreatedTime() {
		return createdTime;
	}
	
	public Date getCreatedTimeDate() {
		return new Date(this.createdTime);
	}

	public Long getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public Date getLastUpdatedTimeDate() {
		return new Date(this.lastUpdatedTime);
	}
	public String getName() {
		return name;
	}
	
	
	public Attachment(String fileName) {
		this.name = fileName;
	}
	
	public Long addFile() {
		ResultSet rs;
		try {
			rs = PostgresDBConnector.getInstance().insert("FILE", getFileQueryObject());
			if(rs.next()) {
				populateAttachmentObject(rs);
				return rs.getLong(1);
			}
			else {
				throw new RuntimeException("data not exist");
			}
		} catch (SQLException e) {
			throw new RuntimeException("sql exception "+e.getMessage());
		}
	}

	public Attachment(ResultSet rs) throws SQLException {
		populateAttachmentObject(rs);
		this.name = (String) rs.getString(2);
	}
	private void populateAttachmentObject(ResultSet rs) throws SQLException {
		this.id =(int) rs.getLong(1);
		this.createdTime = (Long)rs.getLong(3);
		this.lastUpdatedTime =(Long) rs.getLong(4);
	}

	private Map<String, Object> getFileQueryObject() {
		Map<String,Object> map = new LinkedHashMap<String,Object>();
		map.put("NAME", this.name);
		map.put("CREATEDTIME", System.currentTimeMillis());
		map.put("LAST_UPDATED_TIME", System.currentTimeMillis());
		return map;
	}
	
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		json.put("id", this.getId());
		json.put("createdTime", this.getCreatedTimeDate());
		json.put("lastUpdatedTime", this.getLastUpdatedTimeDate());
		json.put("fileName", this.getName());
		return json;
	}
	
	
}
