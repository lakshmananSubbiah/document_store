package com.leucine.documentstore.fileAPI;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public interface FileAPI {

	public Attachment createFile(HttpServletRequest request);
	
	public Attachment getFile(int fileId);
	
	public List<Attachment> getFiles(int from, int limit,SortingField sortingField,boolean isAsc);
	
	public File getFileContent(Attachment attachment,HttpServletRequest request);
	
	public Attachment updateFile(int fileId,HttpServletRequest request);
	
	public Attachment updateFileName(int fileId,String fileName);
	
	public void deleteFile(int fileId,HttpServletRequest request);
}
