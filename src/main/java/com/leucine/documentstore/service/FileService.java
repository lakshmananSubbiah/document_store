package com.leucine.documentstore.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.leucine.documentstore.commons.CommonUtil;
import com.leucine.documentstore.fileAPI.Attachment;
import com.leucine.documentstore.fileAPI.FileAPI;
import com.leucine.documentstore.fileAPI.FileAPIImpl;
import com.leucine.documentstore.fileAPI.SortingField;

public class FileService {
	
	public static void getFiles(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			int from = request.getParameter("from")!=null?Integer.parseInt(request.getParameter("from")):0;
			int limit = request.getParameter("limit")!=null?Integer.parseInt(request.getParameter("limit")):10;
			SortingField sortingField = request.getParameter("sortBy")!=null?SortingField.getSortingFieldfromApiName(request.getParameter("sortBy")):SortingField.CREATED_TIME;
			boolean isAsc = request.getParameter("isAsc")!=null?Boolean.parseBoolean(request.getParameter("isAsc")):true;
			FileAPI fileAPi = new FileAPIImpl();
			List<Attachment> attachments = fileAPi.getFiles(from, limit, sortingField,isAsc);
			response.getWriter().append(CommonUtil.convertAttachmentListToJson(attachments).toString());
			response.setContentType("application/json");
			response.setStatus(200);
		}
		catch(RuntimeException e) {
			response.setContentType("application/json");
			response.setStatus(500);
			response.getWriter().append(CommonUtil.getInternalServerError().toString());
		}
	}
	
	public static void createFile(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			FileAPI fileAPi = new FileAPIImpl();
			Attachment attachment = fileAPi.createFile(request);
			response.getWriter().append(attachment.toJSONObject().toString());
			response.setContentType("application/json");
			response.setStatus(201);
		}
		catch(RuntimeException e) {
			response.setContentType("application/json");
			response.setStatus(500);
			response.getWriter().append(CommonUtil.getInternalServerError().toString());
		}
	}
	
	
	
	public static void updateFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			int fileId = CommonUtil.getFileIdfromRequestURI(request.getRequestURI());
			FileAPI fileAPi = new FileAPIImpl();
			Attachment attachment = fileAPi.updateFile(fileId, request);
			response.getWriter().append(attachment.toJSONObject().toString());
			response.setContentType("application/json");
			response.setStatus(200);
		}
		catch(RuntimeException e) {
			if(e.getMessage().equals("The id doesn't exist")) {
				response.setContentType("application/json");
				response.setStatus(422);
				response.getWriter().append(CommonUtil.getUnprocessableEntityError("fileId").toString());
			}
			else {
				response.setContentType("application/json");
				response.setStatus(500);
				response.getWriter().append(CommonUtil.getInternalServerError().toString());
			}
		}
	}
	
	public static void getFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			int fileId = CommonUtil.getFileIdfromRequestURI(request.getRequestURI());
			FileAPI fileAPi = new FileAPIImpl();
			Attachment attachment = fileAPi.getFile(fileId);
			response.getWriter().append(attachment.toJSONObject().toString());
			response.setContentType("application/json");
			response.setStatus(200);
		}
		catch(RuntimeException e) {
			if(e.getMessage().equals("The id doesn't exist")) {
				response.setContentType("application/json");
				response.setStatus(422);
				response.getWriter().append(CommonUtil.getUnprocessableEntityError("fileId").toString());
			}
			else {
				response.setContentType("application/json");
				response.setStatus(500);
				response.getWriter().append(CommonUtil.getInternalServerError().toString());
			}
		}
	}
	
	public static void getFileContent(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			int fileId = CommonUtil.getFileIdfromRequestURI(request.getRequestURI());
			FileAPI fileAPi = new FileAPIImpl();
			Attachment attachment = fileAPi.getFile(fileId);
			File file = fileAPi.getFileContent(attachment,request);
			response.setContentType(CommonUtil.extractExtn(attachment.getName()));
			response.setStatus(200);
			response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''"+attachment.getName());
			InputStream is = new FileInputStream(file);
			CommonUtil.copyStream(is,response.getOutputStream());
			is.close();
		}
		catch(RuntimeException e) {
			if(e.getMessage().equals("The id doesn't exist")) {
				response.setContentType("application/json");
				response.setStatus(422);
				response.getWriter().append(CommonUtil.getUnprocessableEntityError("fileId").toString());
			}
			else {
				response.setContentType("application/json");
				response.setStatus(500);
				response.getWriter().append(CommonUtil.getInternalServerError().toString());
			}
		}
	}
	
	public static void updateFileName(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			int fileId = CommonUtil.getFileIdfromRequestURI(request.getRequestURI());
			JSONObject content = CommonUtil.getBody(request);
			FileAPI fileAPi = new FileAPIImpl();
			Attachment attachment = fileAPi.updateFileName(fileId, content.getString("fileName"));
			response.getWriter().append(attachment.toJSONObject().toString());
			response.setContentType("application/json");
			response.setStatus(200);
		}
		catch(JSONException e) {
			response.setContentType("application/json");
			response.setStatus(422);
			response.getWriter().append(CommonUtil.getUnprocessableEntityError("fileName").toString());
		}
		catch(RuntimeException e) {
			response.setContentType("application/json");
			response.setStatus(500);
			response.getWriter().append(CommonUtil.getInternalServerError().toString());
		}
	}
	
	public static void deleteFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			int fileId = CommonUtil.getFileIdfromRequestURI(request.getRequestURI());
			FileAPI fileAPi = new FileAPIImpl();
			fileAPi.deleteFile(fileId,request);
			response.setStatus(204);
		}
		catch(RuntimeException e) {
			if(e.getMessage().equals("The id doesn't exist")) {
				response.setContentType("application/json");
				response.setStatus(422);
				response.getWriter().append(CommonUtil.getUnprocessableEntityError("fileId").toString());
			}
			else {
				response.setContentType("application/json");
				response.setStatus(500);
				response.getWriter().append(CommonUtil.getInternalServerError().toString());
			}
		}
	}
}
