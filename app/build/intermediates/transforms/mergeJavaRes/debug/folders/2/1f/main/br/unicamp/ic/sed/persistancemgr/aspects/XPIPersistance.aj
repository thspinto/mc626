package br.unicamp.ic.sed.persistancemgr.aspects;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public aspect XPIPersistance {
	
	public pointcut pcreateFileOutputStream(): 
		execution (FileOutputStream br.unicamp.ic.sed.persistancemgr.impl.IPersistanceFacade.createFileOutputStream(String));
	
	public pointcut pcloseFileOutputStream(): 
		execution (void br.unicamp.ic.sed.persistancemgr.impl.IPersistanceFacade.closeFileOutputStream(FileOutputStream));
	
	public pointcut pcloseFileInputStream(): 
		execution (void br.unicamp.ic.sed.persistancemgr.impl.IPersistanceFacade.closeFileInputStream(InputStream));
	
	public pointcut pcreateInputStream(): 
		execution (InputStream br.unicamp.ic.sed.persistancemgr.impl.IPersistanceFacade.createInputStream(String)) || 
		execution (InputStream br.unicamp.ic.sed.persistancemgr.impl.IPersistanceFacade.createInputStream(URL));
	
	public pointcut pgetResourceAsStream():
		execution (InputStream br.unicamp.ic.sed.persistancemgr.impl.IPersistanceFacade.getResourceAsStream(String));
}