package br.unicamp.ic.sed.localizationloadermgr.aspects;

import java.io.InputStream;
import java.net.URL;

public aspect XPILocalizationLoader {
	public pointcut ploadStringFromURL(URL url, InputStream in): 
		execution (void br.unicamp.ic.sed.localizationloadermgr.impl.ILocalizationLoaderFacade.loadStringFromURL(URL, InputStream))
		&& args(url, in);
	
	public pointcut ploadStringFromString(String filePath, InputStream in): 
		execution (void br.unicamp.ic.sed.localizationloadermgr.impl.ILocalizationLoaderFacade.loadStringFromString(String, InputStream))
		&& args(filePath, in);
}