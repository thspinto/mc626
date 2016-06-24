package br.unicamp.ic.sed.localization_localizationmgr_connector.impl;

public class ComponentFactory {
private static IManager manager = null;
	
	public static IManager createInstance(){
		if (manager==null)
			manager = new Manager();
		return manager;
	}
}
