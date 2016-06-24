package br.unicamp.ic.sed.bookmgr.impl;

import br.unicamp.ic.sed.bookmgr.prov.IManager;

public class ComponentFactory {
	private static IManager manager = null;
	
	public static IManager createInstance(){
		if (manager==null)
			manager = new Manager();
		return manager;
	}
}
