package br.unicamp.ic.sed.localization.aspects;

import java.io.InputStream;
import java.net.URL;

public aspect XPILocalization {
	
	public pointcut ploadStringFromURL(URL url, InputStream in): 
		execution (void br.unicamp.ic.sed.localization.impl.ILocalizationFacade.loadStringFromURL(URL, InputStream))
		&& args(url, in);
	
}