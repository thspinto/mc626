package br.unicamp.ic.sed.localization.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import android.util.Log;
import br.unicamp.ic.sed.localization.prov.ILocalization;
import br.unicamp.ic.sed.localization.req.ILocalizationLoader;


class ILocalizationFacade implements ILocalization{
	private static Properties strings;
	private static Manager manager;

	public ILocalizationFacade(Manager manager) {
		super();
		//init(Locale.getDefault().toString());
		init("en");
		this.manager = manager;
	}

   /**
    * this method will be advised by AAPersistance.around(URL, InputStream)
    * @param url is the URL of the resource
    * @param in will always be null, the AAPersistance will provide the right object
    * */
	private static void loadStringFromURL(URL url, InputStream in) throws IOException {
	   strings = new Properties();
	   strings.load(in);
   }

	/**
	 * if it is english "en" load from local info
	 * if it is another language load from LocalizationLoader
	 * */
	private static void init(String language) {
		if ( language.equals("en") ){
			URL url = ILocalizationFacade.class.getResource("strings_" + language + ".txt");
			try {
				loadStringFromURL(url, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			//delegate to the component loader.
			//gets from the manager
			//this component is optional
			ILocalizationLoader localizationLoader = (ILocalizationLoader) manager.getRequiredInterface("ILocalizationLoader");
			if (localizationLoader != null) {
				final Properties str = localizationLoader.getStringsFromLocale(language);
				if (str != null)
					strings = str;
			} else {
				Log.w("debug", "Component ILocalizationLoader not found!, its an optional component");
			}
		}

//		try {
//			strings.load(url.openStream());
//		} catch (Exception e) {
//			String def = "en";
//			if (language.equals(def))
//				return false;
//
//			return init(def);
//		};
//		return true;
	}

//	private static boolean init(int type, String file) {
//		strings = new Properties();
//		switch (type) {
//		case 2: try {
//			strings.load(new FileInputStream(file));
//			return true;
//		} catch (IOException e) { return false;}
//		case 3: return init("cs");
//		case 4: return init("en");
//		case 5: return init("es");
//		case 6: return init("ca");
//		default: return init(Locale.getDefault().getLanguage());
//		}
//	}

	public String getString(String key) {
			String ret = strings.getProperty(key);
			if (ret == null)
				return key;
			return ret;
	}

	public void changeLocale(String locale) {
		init(locale);
	}

	public void loadStringsFromFile(String file) {
		ILocalizationLoader localizationLoader = (ILocalizationLoader) manager.getRequiredInterface("ILocalizationLoader");
		if (localizationLoader != null) {
			Properties properties = localizationLoader.getStringFromFile(file);
			//if file doesn't exists, then start a new properties file;
			if (properties != null) {
				strings = properties;
			} else {
				strings = new Properties();
			}
		} else {
			Log.w("debug", "Component ILocalizationLoader not found!, its a optional component");
		}
	}
}
