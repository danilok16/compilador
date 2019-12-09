package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileController {
	private FileReader fileReader;
	private BufferedReader bufferedReader;
	
	public FileReader getFileReader() {
		return fileReader;
	}
	public void setFileReader(FileReader fileReader) {
		this.fileReader = fileReader;
	}
	public BufferedReader getBufferedReader() {
		return bufferedReader;
	}
	public void setBufferedReader(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
	}
	
	public BufferedReader openFile(String path) {
		
		try {
			this.fileReader = new FileReader(path);
			this.bufferedReader = new BufferedReader(fileReader);
			return this.bufferedReader;
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			closeFile();
		}
		return null;

	}
	
	public void closeFile() {
		try {
			if (this.bufferedReader != null) {
				this.bufferedReader.close();
			}
			if (this.fileReader != null) {
				this.fileReader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}