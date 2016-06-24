package br.unicamp.ic.sed.localization_persistance_vp.impl;

import java.io.InputStream;
import java.net.URL;

public aspect LocalizationBinder extends AAPersistance{
	
	public pointcut pointcutloadStringFromURL(URL url, InputStream in) : 
		br.unicamp.ic.sed.localization.aspects.XPILocalization.ploadStringFromURL(url, in);

}