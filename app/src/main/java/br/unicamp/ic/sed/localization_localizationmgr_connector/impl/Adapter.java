package br.unicamp.ic.sed.localization_localizationmgr_connector.impl;

import java.util.Properties;

import br.unicamp.ic.sed.localizationloadermgr.prov.ILocalizationLoader;


class Adapter implements br.unicamp.ic.sed.localization.req.ILocalizationLoader {
	private IManager manager;
	
	public Adapter (IManager manager) {
		this.manager = manager;
	}
	
	public Properties getStringFromFile(String filePath) {
		ILocalizationLoader localizationLoader = (ILocalizationLoader) manager.getRequiredInterface("ILocalizationLoader");
		return localizationLoader.getStringFromFile(filePath);
	}

	public Properties getStringsFromLocale(String locale) {
		ILocalizationLoader localizationLoader = (ILocalizationLoader) manager.getRequiredInterface("ILocalizationLoader");
		return localizationLoader.getStringsFromLocale(locale);
	}
	
}
