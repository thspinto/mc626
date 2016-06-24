package br.unicamp.ic.sed.localizationloadermgr_persistance_vp.impl;

public class ComponentFactory {
	private static IManager manager = null;

	public static IManager createInstance(){
		if (manager==null)
			manager = new Manager();
		return manager;
	}
}
