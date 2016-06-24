package br.unicamp.ic.sed.persistancemgr.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import br.unicamp.ic.sed.persistancemgr.prov.IPersistance;


class IPersistanceFacade implements IPersistance{

	public FileOutputStream createFileOutputStream(String f) throws IOException {
		File file = new File(f);
		FileOutputStream fop = new FileOutputStream(file, false);
		//always overwrite
		file.createNewFile();
		return fop;
	}

	public void closeFileOutputStream(FileOutputStream fop) throws IOException {
		fop.flush();
		fop.close();

		/**
		 * to test this, try save a pgn file on honza chess
		 * */
//		Log.w("debug", "throwing exception");
//		throw new IOException();
	}

	public InputStream createInputStream(String filepath) throws IOException {
		FileInputStream in = new FileInputStream(filepath);
		return in;
	}

	public void closeFileInputStream(InputStream f) throws IOException {
		f.close();
	}

	public InputStream createInputStream(URL url) throws IOException {
		return url.openStream();
	}

	public InputStream getResourceAsStream(String filePath) throws IOException {
		return getClass().getResourceAsStream(filePath);
	}
}
