package br.unicamp.ic.sed.localizationloadermgr.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import br.unicamp.ic.sed.localizationloadermgr.prov.ILocalizationLoader;


class ILocalizationLoaderFacade implements ILocalizationLoader{

	Properties strings = null;

	/**
	 * this method will be advised by AAPersistance.around(URL, InputStream)
	 * @param url is the URL of the resource
	 * @param in will always be null, the AAPersistance will provide the right object
	 * */
	private void loadStringFromURL(URL url, InputStream in)
			throws IOException {
		strings = new Properties();
		strings.load(in);
	}

	public Properties getStringsFromLocale(String locale) {
		//locale is a given place to load strings from
		//locale is the first two letters for the country the region specifics will not be supported at the time

		URL url = ILocalizationLoaderFacade.class.getResource("strings_" + locale + ".txt");
		try {
			if (url != null ) {
				/**
				 * if the language set is portuguese BR, url object will be null
				 * to make sure it will open just a valid url, test if not null
				 * if the language is not found, remember that Properties strings was set before
				 * so, no need to set it again, when getString(key) is called, it will return the key
				 * */
//				strings.load(url.openStream());
				loadStringFromURL(url, null);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return strings;
	}


	/**
	 * this method will be advised by AAPersistance.around(String, InputStream)
	 * @param filePath is the Path of the resource, example "/sdcard/strings_hd.txt"
	 * @param in will always be null, the AAPersistance will provide the right object
	 * */
	private void loadStringFromString(String filePath, InputStream in)
			throws IOException {
		strings = new Properties();
		strings.load(in);
	}

	public Properties getStringFromFile(String filePath) {
		strings = new Properties();

		try {
//			strings.load(new FileInputStream(filePath));
			loadStringFromString(filePath, null);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return strings;
	}
}
