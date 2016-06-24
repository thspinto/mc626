package br.unicamp.ic.sed.localization_persistance_vp.impl;

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
		
		Log.w("debug", "aspect AAPersistance from localization_persistance_vp");
		try {
			IPersistance persistance = (IPersistance) mymanager.getRequiredInterface("IPersistance");
			InputStream in2 = persistance.createInputStream(url);
			proceed(url, in2);
			persistance.closeFileInputStream(in2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}