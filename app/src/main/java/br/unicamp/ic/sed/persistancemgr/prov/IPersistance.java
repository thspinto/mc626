package br.unicamp.ic.sed.persistancemgr.prov;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface IPersistance {
	/**
	 * given a filename f will create a new File object
	 * 	File file = new File(filename);
		FileOutputStream fop = new FileOutputStream(file, false);
		//always overwrite
		file.createNewFile();
	 * */
	FileOutputStream createFileOutputStream(String f) throws IOException;

	/**
	 * given a filename FileOutputStream fop <br>
	 * it will flush write and close it <br>
	 *
	 * fop.flush(); <br>
	 * fop.close();
	 * */
	void closeFileOutputStream(FileOutputStream fop) throws IOException;


	/**
	 * creates a nre InputStream based on the following:
	 * FileInputStream in = new FileInputStream(filepath); <br>
	 * return in;
	 * */
	InputStream createInputStream(String filepath) throws IOException;

	/**
	 * closes the inputStream based on the following: <br>
	 * f.close();
	 * */
	void closeFileInputStream(InputStream f) throws IOException;

	/**
	 * creates an inputStream based on the URL resource <br>
	 * url.openStream();
	 * */
	InputStream createInputStream(URL url) throws IOException;

	/**
	 * used on initBook BookManager as following: <br>
	 * return getClass().getResourceAsStream(filePath);
	 * */
	InputStream getResourceAsStream(String filePath) throws IOException;
}
