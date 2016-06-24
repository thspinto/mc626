package br.unicamp.ic.sed.localizationloadermgr_persistance_vp.impl;

import java.io.InputStream;
import java.net.URL;

public aspect LocalizationLoaderBinder extends AAPersistance{
	
	public pointcut pointcutloadStringFromURL(URL url, InputStream in) : 
		br.unicamp.ic.sed.localizationloadermgr.aspects.XPILocalizationLoader.ploadStringFromURL(url, in);

	public pointcut pointcutloadStringFromString(String filePath, InputStream in) : 
		br.unicamp.ic.sed.localizationloadermgr.aspects.XPILocalizationLoader.ploadStringFromString(filePath, in);

}