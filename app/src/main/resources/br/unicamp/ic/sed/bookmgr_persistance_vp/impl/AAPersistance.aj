package br.unicamp.ic.sed.bookmgr_persistance_vp.impl;

import java.io.IOException;
import java.io.InputStream;

import br.unicamp.ic.sed.persistancemgr.prov.IPersistance;

public abstract aspect AAPersistance {
	private IManager mymanager = ComponentFactory.createInstance();
	
	public abstract pointcut pointcutinitBook(InputStream in);
	void around(InputStream in) : 
		pointcutinitBook(in) {
		IPersistance persistance = (IPersistance) mymanager.getRequiredInterface("IPersistance");
		try{
			InputStream in2 = persistance.getResourceAsStream("/book.bin");
			proceed(in2);
			persistance.closeFileInputStream(in2);
		} catch (IOException e) {
			//empty catch, will be handled on the persistance component
		}
	}
}