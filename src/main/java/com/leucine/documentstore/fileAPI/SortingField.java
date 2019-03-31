package com.leucine.documentstore.fileAPI;

public enum SortingField {

	CREATED_TIME("CREATEDTIME", "createdTime"),
	LAST_UPDATED_TIME("LAST_UPDATED_TIME", "lastUpdatedTime");
	
	private String columnName;
	private String apiName;
	
	private SortingField(String string,String actualName) {
		this.columnName = string;
		this.apiName = actualName;
	}
	
	public String getColumnName() {
		return this.columnName;
	}
	
	public String getApiName() {
		return this.apiName;
	}
	
	public static SortingField getSortingFieldfromApiName(String apiName) {
		SortingField sortingFields[] = SortingField.values();
		for(SortingField sortingField: sortingFields) {
			if(sortingField.getApiName().equals(apiName)) {
				return sortingField;
			}
		}
		throw new RuntimeException("There is no sortingfieldof the name");
	}
}
