package br.unicamp.ic.sed.formatmgr.aspects;

import java.io.FileOutputStream;

public aspect XPIFormat {
	
	public pointcut psavePGNToFile(String pgnStringFile, String filename, FileOutputStream fop): 
		execution (boolean br.unicamp.ic.sed.formatmgr.prov.IFormat.savePGNToFile(String, String, FileOutputStream))
		&& args(pgnStringFile, filename, fop);
	
//	declare soft: IOException : psavePGNToFile(String , String );
	
//	declare soft : InvalidMoveException : phumanMove() ;
//	declare soft : InvalidMoveException : pMakeHumanHumanMove()
//	public pointcut psetFENOrPGN(String s, Context c):
//		execution (void br.unicamp.ic.sed.engine.prov.IEngine.setFENOrPGN(String, Context)) 
//				&& args(s, c);
}