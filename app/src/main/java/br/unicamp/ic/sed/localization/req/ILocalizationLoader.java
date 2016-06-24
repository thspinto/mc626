package br.unicamp.ic.sed.localization.req;

import java.util.Properties;

public interface ILocalizationLoader {
	public Properties getStringFromFile(String filePath);
	public Properties getStringsFromLocale(String locale);
}
