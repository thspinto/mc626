package br.unicamp.ic.sed.localizationloadermgr_persistance_vp.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.util.Log;
import br.unicamp.ic.sed.persistancemgr.prov.IPersistance;

public abstract aspect AAPersistance{
	private IManager mymanager = ComponentFactory.createInstance();

	public abstract pointcut pointcutloadStringFromURL(URL url, InputStream in);
	void around(URL url, InputStream in) : 
		pointcutloadStringFromURL(url, in) {
		
		Log.w("debug", "aspect AAPersistance URL from localizationloadermgr_persistance_vp");
		try {
			IPersistance persistance = (IPersistance) mymanager.getRequiredInterface("IPersistance");
			InputStream in2 = persistance.createInputStream(url);
			proceed(url, in2);
			persistance.closeFileInputStream(in2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public abstract pointcut pointcutloadStringFromString(String filePath, InputStream in);
	void around(String filePath, InputStream in) : 
		pointcutloadStringFromString(filePath, in) {
		
		Log.w("debug", "aspect AAPersistance STRING from localizationloadermgr_persistance_vp");
		try {
			IPersistance persistance = (IPersistance) mymanager.getRequiredInterface("IPersistance");
			InputStream in2 = persistance.createInputStream(filePath);
			proceed(filePath, in2);
			persistance.closeFileInputStream(in2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}