package br.unicamp.ic.sed.formatmgr_persistance_connector_vp.impl;

import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;
import br.unicamp.ic.sed.persistancemgr.prov.IPersistance;

public abstract aspect AAPersistanceImpl {
	private IManager mymanager = ComponentFactory.createInstance();

	public abstract pointcut pointcutSavePGNToFile(String pgnStringFile, String filename, FileOutputStream fop);
	boolean around(String pgnStringFile, String filename, FileOutputStream fop) : 
		pointcutSavePGNToFile(pgnStringFile, filename, fop) {
		
		Log.w("debug", "aspect AAPersistanceImpl FormatMgr");
		boolean valueToReturn = false;
		try {
			IPersistance persistance = (IPersistance) mymanager.getRequiredInterface("IPersistance");
			FileOutputStream fop2 = persistance.createFileOutputStream(filename);
			
			valueToReturn = proceed(pgnStringFile, filename, fop2);

			persistance.closeFileOutputStream(fop2);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return valueToReturn;
	}

}