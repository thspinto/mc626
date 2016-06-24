package br.unicamp.ic.sed.localizationloadermgr.prov;

import java.util.Properties;

public interface ILocalizationLoader {
	public Properties getStringFromFile(String filePath);
	public Properties getStringsFromLocale(String locale);
}
