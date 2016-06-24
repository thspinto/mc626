package br.unicamp.ic.sed.localization.impl;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

class Manager implements br.unicamp.ic.sed.localization.prov.IManager{

	private Hashtable providedIterfaces = new Hashtable();

	private Hashtable provInterfaceMapTypes = new Hashtable();
	private Hashtable reqInterfaceMapTypes = new Hashtable();

	void setProvidedInterface (String name, Object facade) {
		providedIterfaces.put(name, facade);
	}

	void setProvidedInterfaceTypes(String name, Class type) {
		provInterfaceMapTypes.put(name, type);
	}

	void setRequiredInterfaceTypes(String name, Class type) {
		reqInterfaceMapTypes.put(name, type);
	}

	Manager() {
		this.setProvidedInterface("ILocalization", new ILocalizationFacade(this));
		this.setProvidedInterface("IManager", this);
		this.setProvidedInterfaceTypes("ILocalization", br.unicamp.ic.sed.localization.prov.ILocalization.class);
		this.setProvidedInterfaceTypes("IManager", br.unicamp.ic.sed.localization.prov.IManager.class);
	}

	public String[] getProvidedInterfaceNames() {
		return convertListToArray(provInterfaceMapTypes.keys());
	}

	public String[] getRequiredInterfaceNames() {
		return convertListToArray(reqInterfaceMapTypes.keys());
	}

	public Object getProvidedInterface(String name){
		return providedIterfaces.get(name);
	}


	public void setRequiredInterface(String name, Object facade) {
		reqInterfaceMapTypes.put(name, facade);
	}


	public Map<String, Class> getProvidedInterfaceTypes() {
		return provInterfaceMapTypes;
	}


	public Map<String, Class> getRequiredInterfaceTypes() {
		return reqInterfaceMapTypes;
	}


	public Object getRequiredInterface(String name) {
		return reqInterfaceMapTypes.get(name);
	}

	private String[] convertListToArray(Enumeration stringEnum){ //System.out.println("Manager.convertListToArray()");
		Vector stringVector = new Vector();
		for (Enumeration iter = stringEnum; iter.hasMoreElements();) {
			String element = (String) iter.nextElement();
			stringVector.addElement(element);
		}

		String[] stringArray = new String[stringVector.size()];
		for (int i=0; i < stringVector.size(); i++){
			stringArray[i] = (String) stringVector.elementAt(i);
		}

		return stringArray;
	}
}
