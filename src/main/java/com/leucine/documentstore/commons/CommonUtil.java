package com.leucine.documentstore.commons;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.leucine.documentstore.fileAPI.Attachment;

public class CommonUtil {

	public static final String INVALID_METHOD = "Method Doesn't exist";

	public static int getFileIdfromRequestURI(String requestURI) {
		try {
			String split[] = StringUtils.split(requestURI,"/");
			for(String splitString : split) {
				if(splitString.matches("([0-9]+)")) {
					return Integer.parseInt(splitString);
				}
			}
			throw new Exception("The id doesn't exist");
 		}
		catch(Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		
	}

	public static JSONObject convertAttachmentListToJson(List<Attachment> attachments) {
		JSONObject json = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for(Attachment attachment : attachments) {
			jsonArray.put(attachment.toJSONObject());
		}
		json.put("data", jsonArray);
		return json;
	}
	
	 public static String extractExtn(String fileName) {
		 String[] split = StringUtils.split(fileName,'.');
		 return split[split.length-1];
	}
	 
	 public static String getContentType(String fileExtn)
		{
			if (fileExtn == null )
			{
				return null;
			}
			fileExtn = fileExtn.toLowerCase();
			if (fileExtn.equals("pdf"))
			{
				return "application/pdf"; //NO I18N
			}
			if (fileExtn.equals("zip"))
			{
				return "application/zip"; //NO I18N
			}
			else if (fileExtn.equals("doc") || fileExtn.equals("docx"))
			{
				return "application/msword"; //NO I18N
			}
			else if(fileExtn.equals("mp3")) {
				return "audio/mpeg";
			}
			else if(fileExtn.equals("ogg")) {
				return "audio/ogg";
			}
			else if(fileExtn.equals("wav")) {
				return "audio/wav";
			}
			else if (fileExtn.equals("xls") || fileExtn.equals("xlsx"))
			{
				return "application/vnd.ms-excel"; //NO I18N
			}
			else if (fileExtn.equals("pps")|| fileExtn.equals("ppt"))
			{
				return "application/vnd.ms-powerpoint"; //NO I18N
			}
			else if(fileExtn.equals("pptx")){
				return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
			}
			else if (fileExtn.equals("jpg") || fileExtn.equals("jpeg") || fileExtn.equals("gif") || fileExtn.equals("png") || fileExtn.equals("bmp")|| fileExtn.equals("tif") || fileExtn.equals("psd") || fileExtn.equals("tiff") || fileExtn.equals("ico"))
			{
				fileExtn=fileExtn.equals("jpg")?"jpeg":fileExtn;
				return "image/" + fileExtn;
			}
			else if(fileExtn.equals("rtf")){
				return "application/rtf";
			}
			else if(fileExtn.equals("css")){
				return "text/css";
			}
			else if(fileExtn.equals("txt") || fileExtn.equals("text") || fileExtn.equals("sh")){
				return "text/plain";
			}
			else if(fileExtn.equals("xml")){
				return "application/xml";
			}
			else if (fileExtn.equals("dwg"))
			{
				return "image/vnd.dwg"; //NO I18N
			}
			else if(fileExtn.equals("cdr")){
				return "application/cdr";
			}
			else if(fileExtn.equals("stl")){
				return "application/vnd.ms-pki.stl";
			}
			else if(fileExtn.equals("odt")){
				return "application/vnd.google-apps.document";
			}
			else if(fileExtn.equals("odp")){
				return "application/vnd.google-apps.presentation";
			}
			return "text/html"; //NO I18N
		}
	 
	 public static int copyStream(InputStream in, OutputStream out)throws IOException
		{
			byte[] array=new byte[64*1024];
			int content_length = 0;
			while(true)
			{
				int bytesRead=in.read(array);
				if(bytesRead==-1)
				{
					break;
				}
				out.write(array,0,bytesRead);
				content_length+= bytesRead;
			}
			out.flush();
			return content_length;
		}
	 
	 public static  JSONObject getBody(HttpServletRequest request) throws IOException {

		    String body = null;
		    StringBuilder stringBuilder = new StringBuilder();
		    BufferedReader bufferedReader = null;

		    try {
		        InputStream inputStream = request.getInputStream();
		        if (inputStream != null) {
		            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		            char[] charBuffer = new char[128];
		            int bytesRead = -1;
		            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
		                stringBuilder.append(charBuffer, 0, bytesRead);
		            }
		        } else {
		            stringBuilder.append("");
		        }
		    } catch (IOException ex) {
		        throw ex;
		    } finally {
		        if (bufferedReader != null) {
		            try {
		                bufferedReader.close();
		            } catch (IOException ex) {
		                throw ex;
		            }
		        }
		    }

		    body = stringBuilder.toString();
		    return new JSONObject(body);
		}
	 
	 public static JSONObject getInternalServerError() {
		 JSONObject json = new JSONObject();
		 json.put("message", "Internal Error");
		 return json;
	 }
	 
	 public static JSONObject getUnprocessableEntityError(String entity) {
		 JSONObject json = new JSONObject();
		 json.put("message", "Invalid entity value");
		 json.put("entity", entity);
		 return json;
	 }
}

