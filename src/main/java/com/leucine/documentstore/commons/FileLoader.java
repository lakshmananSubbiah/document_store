package com.leucine.documentstore.commons;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class FileLoader {
	
	private String fileContent;
	
	public FileLoader(String fileName){
		this.fileContent = this.getFile(fileName);
	}
	
	public String getFileContent() {
		return this.fileContent;
	}
	
	private String getFile(String fileName) {
		StringBuilder result = new StringBuilder("");
		//Get file from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());

		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
}
