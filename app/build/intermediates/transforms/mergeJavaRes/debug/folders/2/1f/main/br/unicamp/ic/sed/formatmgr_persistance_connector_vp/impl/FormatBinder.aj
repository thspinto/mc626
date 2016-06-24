package br.unicamp.ic.sed.formatmgr_persistance_connector_vp.impl;

import java.io.FileOutputStream;

public aspect FormatBinder extends AAPersistanceImpl{
	
	public pointcut pointcutSavePGNToFile(String pgnStringFile, String filename, FileOutputStream fop) : 
		br.unicamp.ic.sed.formatmgr.aspects.XPIFormat.psavePGNToFile(pgnStringFile , filename, fop);

}