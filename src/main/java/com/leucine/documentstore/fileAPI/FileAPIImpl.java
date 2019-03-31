package com.leucine.documentstore.fileAPI;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;


import com.leucine.documentstore.commons.CommonUtil;
import com.leucine.documentstore.database.PostgresDBConnector;

public class FileAPIImpl implements FileAPI {

	private static final String SAVE_DIR = "uploadFiles";
	
	private static final Logger logger = Logger.getLogger(FileAPIImpl.class.getName());
	@Override
	public Attachment createFile(HttpServletRequest request) {
		Attachment attachment = null;
		String savePath = getSavePath(request);
		 File fileSaveDir = new File(savePath);
	     if (!fileSaveDir.exists()) {
	          fileSaveDir.mkdir();
	     }
	     try {
			for (Part part : request.getParts()) {
			        String fileName = extractFileName(part);
			        attachment = new Attachment(fileName);
			        Long attachmentId = attachment.addFile();
			        String extn = CommonUtil.extractExtn(fileName);
			        fileName = new File(attachmentId+"."+extn).getName();
			        part.write(savePath + File.separator + fileName);
			    }
			return attachment;
		} catch (IOException | ServletException e) {
			logger.log(Level.SEVERE, "exception log", e);
			throw new RuntimeException(e);
		}
	}

	private static String extractFileName(Part part) {
	        String contentDisp = part.getHeader("content-disposition");
	        String[] items = contentDisp.split(";");
	        for (String s : items) {
	            if (s.trim().startsWith("filename")) {
	                return s.substring(s.indexOf("=") + 2, s.length()-1);
	            }
	        }
	        return "";
	    }

	@Override
	public Attachment getFile(int fileId) {
		try {
			ResultSet rs = PostgresDBConnector.getInstance().getRow(Attachment.FILE_TABLE, Attachment.FILE_ID_COLUMN, fileId);
			if(rs.next()) {
				Attachment attachment = new Attachment(rs);
				return attachment;
			}
			throw new RuntimeException("No Attachment Present");
		} catch (Exception e) {
			logger.log(Level.SEVERE, "exception log", e);
			throw new RuntimeException("Error while retreiving attachment ",e);
		}
		
	}

	@Override
	public List<Attachment> getFiles(int from, int limit,SortingField sortingField,boolean isAsc) {
		ResultSet rs;
		List<Attachment> attachments = new ArrayList<Attachment>();
		try {
			rs = PostgresDBConnector.getInstance().getRows(Attachment.FILE_TABLE, limit, from, sortingField.getColumnName(),isAsc);
			while(rs.next()) {
				attachments.add(new Attachment(rs));
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "exception log", e);
			throw new RuntimeException("Error while retreiving attachments",e);
		}
		return attachments;
	}

	@Override
	public File getFileContent(Attachment attachment,HttpServletRequest request) {
		String savePath = getSavePath(request);
		File file= new File((savePath + File.separator + attachment.getId()+"."+CommonUtil.extractExtn(attachment.getName())).trim());
		return file;
	}



	@Override
	public Attachment updateFile(int fileId, HttpServletRequest request) {
		try {
			String savePath = getSavePath(request);
			for (Part part : request.getParts()) {
			        String fileName = extractFileName(part);
			        String extn = CommonUtil.extractExtn(fileName);
			        fileName = new File(fileId+"."+extn).getName();
			        part.write(savePath + File.separator + fileName);
			    }
			PostgresDBConnector.getInstance().updateRowColumn(Attachment.FILE_TABLE, Attachment.FILE_LAST_UPDATED_TIME_COLUMN, System.currentTimeMillis(), Attachment.FILE_ID_COLUMN, fileId);
			return getFile(fileId);
		} catch (IOException | ServletException |SQLException e) {
			logger.log(Level.SEVERE, "exception log", e);
			throw new RuntimeException(e);
		}
	}
	
	private String getSavePath(HttpServletRequest request) {
		String appPath = request.getServletContext().getRealPath("");
		 String savePath = appPath + File.separator + SAVE_DIR;
		 return savePath;
	}

	/**lakshmanan - 31/03/2019
	 * Have to do the both updates in same query but leaving that for a future update.
	 */
	@Override
	public Attachment updateFileName(int fileId, String fileName)  {
		try {
			PostgresDBConnector.getInstance().updateRowColumn(Attachment.FILE_TABLE, Attachment.FILE_NAME_COLUMN, fileName, Attachment.FILE_ID_COLUMN, fileId);
			PostgresDBConnector.getInstance().updateRowColumn(Attachment.FILE_TABLE, Attachment.FILE_LAST_UPDATED_TIME_COLUMN, System.currentTimeMillis(), Attachment.FILE_ID_COLUMN, fileId);
			Attachment attachment = getFile(fileId);
			return attachment;
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "exception log", e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void deleteFile(int fileId,HttpServletRequest request) {
		try {
			String savePath = getSavePath(request);
			Attachment attachment = getFile(fileId);
			File file= new File((savePath + File.separator + fileId+"."+CommonUtil.extractExtn(attachment.getName())).trim());
			file.delete();
			PostgresDBConnector.getInstance().deleteRow(Attachment.FILE_TABLE, Attachment.FILE_ID_COLUMN, fileId);
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "exception log", e);
			throw new RuntimeException(e);
		}
	}

}
