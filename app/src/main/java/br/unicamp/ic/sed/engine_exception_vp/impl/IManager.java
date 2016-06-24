package br.unicamp.ic.sed.engine_exception_vp.impl;

import java.util.Map;

public interface IManager {
	/**
	 * @param name is the name of the provided interface
	 * @return returns an implementation of the provided interface of this component
	 * */
	public Object getProvidedInterface(String name);

	/**
	 * @param name is the name of the required interface
	 * @return returns an implementation of the provided interface of this component
	 * */
	public Object getRequiredInterface(String name);

	/**
	 * sets a required interface of this component
	 * @param name is the name of the required interface example "IFileSystem"
	 * @param facade is the interface of another component, which implements the methods by this required interface
	 * */
	public void setRequiredInterface(String name, Object facade);

	/**
	 * @return returns an array with the names of the PROVIDED interfaces of this component
	 * */
	public String[] getProvidedInterfaceNames();

	/**
	 * @return returns an array with the names of the REQUIRED interfaces of this component
	 * */
	public String[] getRequiredInterfaceNames();

	/**
	 * returns a map where each key is the provided interface name and the Class is the type of the interface
	 */
	public Map<String, Class> getProvidedInterfaceTypes();

	/**
	 * returns a map where each key is the required interface name and the Class is the type of the interface
	 */
	public Map<String, Class> getRequiredInterfaceTypes();
}
