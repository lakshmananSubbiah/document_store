package com.leucine.documentstore.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.leucine.documentstore.commons.CommonUtil;
import com.leucine.documentstore.commons.FileLoader;

/**
 * Servlet implementation class APIServlet
 */
public class APIServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public APIServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    	    throws ServletException, IOException {
    	  try {
			processRequest(request, response);
		} catch (SecurityException | NoSuchMethodException | ClassNotFoundException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	}
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    	    throws ServletException, IOException {
    	  try {
			processRequest(request, response);
		} catch (SecurityException | NoSuchMethodException | ClassNotFoundException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	}
    
    
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String functionName = getFunctionName(request.getRequestURI(),request.getMethod());
		invokeMethod(functionName,request,response);
	}
	
	private void invokeMethod(String functionName,HttpServletRequest request, HttpServletResponse response) throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object[] parameterValues  = new Object[2];
		parameterValues[0] = request;
		parameterValues[1] = response;
		Method method = getMethod(functionName);
		method.invoke(null, parameterValues);
	}
	
	private Method getMethod(String absoluteMethodName) throws SecurityException, NoSuchMethodException, ClassNotFoundException {
		String [] arr = getClassNameAndMethodName(absoluteMethodName);
		String className =arr[0];
		String methodName = arr[1];
		Class<?> clazz = Class.forName(className);
		Method[] methods = clazz.getDeclaredMethods();
		for(int i=0;i<methods.length;i++) {
			Method method = methods[i];
			if(StringUtils.equals(methodName, method.getName())) {
				return method;
			}
		}
		throw new RuntimeException(CommonUtil.INVALID_METHOD);
	}
	
	private String[] getClassNameAndMethodName(String uri) {
		String [] tokens = StringUtils.split(uri, '.');
		String methodname = tokens[tokens.length-1];
		String classname = uri.substring(0, uri.lastIndexOf(methodname)-1);
		String [] arr = {classname,methodname};
		return arr;
	}
	
	private String getFunctionName(String uri,String method) {
		String fileContent = new FileLoader("/requests.json").getFileContent();
		JSONObject json = new JSONObject(fileContent);
		uri = convertAsRegex(uri);
		return json.getString(uri+"_"+method.toLowerCase());
	}
	
	
	private String convertAsRegex(String uri) {
		String[] split = uri.split("/");
		StringBuilder urlBuilder = new StringBuilder();
		for(String splittedString : split) {
			if(splittedString.matches("document-store")) {
				
			}
			else if(splittedString.matches("([0-9]+)")) {
				urlBuilder.append("([0-9]+)");
			}
			else {
				urlBuilder.append(splittedString);
			}
			urlBuilder.append("/");
		}
		return urlBuilder.substring(1,urlBuilder.length()-1).toString();
	}

}
