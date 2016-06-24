package br.unicamp.ic.sed.localization.prov;

public interface ILocalization {
	
	/**
	 * @return the localized string for the key provided
	 * or returns the key if not found, see {@link #loadStringsFromFile(String)}
	 * */
	public String getString(String key);
	
	/**
	 * loads strings from the file provided.
	 * if there is no string provided, the method {@link #getString(String)}
	 * will return the key
	 * */
	public void loadStringsFromFile(String file);
	
	/**
	 * changes the locale of this component
	 * if the locale is other than english, it will delegate to the localizationloader.
	 * @param locale must be lowercase "en" or "ca" or "es"
	 * */
	public void changeLocale(String locale);
}
