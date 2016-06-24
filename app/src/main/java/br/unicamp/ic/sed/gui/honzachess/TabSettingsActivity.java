package br.unicamp.ic.sed.gui.honzachess;

import java.util.Locale;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import br.unicamp.ic.sed.localization.prov.ILocalization;
import chess.spl.R;

public class TabSettingsActivity extends TabActivity implements View.OnClickListener {
	RadioGroup mGroup;

	/**gets a internal number representation and return the string locale
	 * @param realId is the internal representation of the number
	 * @return the string representing the locale of this number
	 * */
	private static String getLocaleFromInternalNumber(int realId)
	{
		switch (realId) {
		case 1: return Locale.getDefault().getLanguage();
		case 3: return "cs";
		case 4: return "en";
		case 5: return "es";
		case 6: return "ca";
		case 2: return "sdcard";
		default: return "en";
		}
	}

	/**gets a number from the layoutUI and transforms into a internal number
	 * @param id from the Layout XML
	 * @return internal representation of the number
	 * */
	private static int getInternalNumberFromResourceID(int id)
	{
		switch (id) {
		case R.id.cs: return 3;
		case R.id.en: return 4;
		case R.id.es: return 5;
		case R.id.ca: return 6;
		case R.id.localedefault: return 1;
		case R.id.sdcard: return 2;
		default: return 1;
		}
	}

	/**gets a number and return the ID from the layoutUI
	 * @param realId
	 * @return number on the layout UI corresponding to the given number
	 * */
	private static int getXMLUINumberFromInternalNumber(int realId)
	{
		switch (realId) {
		case 3: return R.id.cs;
		case 4: return R.id.en;
		case 5: return R.id.es;
		case 6: return R.id.ca;
		case 2: return R.id.sdcard;
		default: return R.id.localedefault;
		}
	}

	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ILocalization ilocalizationservices =  (ILocalization) br.unicamp.ic.sed.localization.impl.ComponentFactory.createInstance().getProvidedInterface("ILocalization");

        setTitle(ilocalizationservices.getString("SETTINGS"));
        TabHost tabHost = getTabHost();
        LayoutInflater.from(this).inflate(R.layout.layout_honzachess_settings, tabHost.getTabContentView(), true);
        tabHost.addTab(tabHost.newTabSpec("level").setIndicator(ilocalizationservices.getString("LEVEL")).setContent(R.id.level));
        tabHost.addTab(tabHost.newTabSpec("locale").setIndicator(ilocalizationservices.getString("LOCALE")).setContent(R.id.locale));
   		setResult(11, new Intent());

   		// TAB locale
		Button ok = (Button)findViewById(R.id.ok);
		mGroup = (RadioGroup)findViewById(R.id.localegroup);
		SharedPreferences pref = getBaseContext().getSharedPreferences("honzasettings", MODE_PRIVATE);
		RadioButton selected = (RadioButton)findViewById(
				getXMLUINumberFromInternalNumber(pref.getInt("locale", 1)));

		selected.setChecked(true);
		ok.setText(ilocalizationservices.getString("OK"));
		RadioButton cs = (RadioButton)findViewById(R.id.cs);
		RadioButton en = (RadioButton)findViewById(R.id.en);
		RadioButton es = (RadioButton)findViewById(R.id.es);
		RadioButton ca = (RadioButton)findViewById(R.id.ca);
		RadioButton def = (RadioButton)findViewById(R.id.localedefault);
		cs.setText(ilocalizationservices.getString("CZECH"));
		en.setText(ilocalizationservices.getString("ENGLISH"));
		es.setText(ilocalizationservices.getString("SPANISH"));
		ca.setText(ilocalizationservices.getString("CATALAN"));
		def.setText(ilocalizationservices.getString("DEFAULT"));
		ok.setOnClickListener(this);

		// TAB level
		Button oklevel = (Button)findViewById(R.id.oklevel);
		oklevel.setText(ilocalizationservices.getString("OK"));
		oklevel.setOnClickListener(this);
		TextView levelstatic = (TextView)findViewById(R.id.levelstatic);
		levelstatic.setText(ilocalizationservices.getString("TIME_PER_MOVE"));
		EditText time = (EditText)findViewById(R.id.time);
		time.setText(pref.getString("timeLimit", "5000"));
 	}

	public void onClick(View v) {

		EditText time = (EditText)findViewById(R.id.time);
		try {
			Integer.valueOf(time.getText().toString());
		} catch (NumberFormatException n) {
			//if the user puts a wrong value, continue on same screen
			return;
		}
		SharedPreferences pref = getBaseContext().getSharedPreferences("honzasettings", MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();

		int internalNumber;
	    editor.putInt("locale", internalNumber = getInternalNumberFromResourceID(mGroup.getCheckedRadioButtonId()));
	    editor.putString("timeLimit", time.getText().toString());
        editor.commit();

        ILocalization ilocalizationservices =  (ILocalization) br.unicamp.ic.sed.localization.impl.ComponentFactory.createInstance().getProvidedInterface("ILocalization");
        String locale = getLocaleFromInternalNumber(internalNumber);
        if (locale.equals("sdcard")) {
        	//load strings from a file, this is hardcoded by design
        	ilocalizationservices.loadStringsFromFile("/sdcard/strings_hs.txt");
        } else {
        	//if default is selected, it will get from the system locale
        	ilocalizationservices.changeLocale(locale);
        }
//        S.init(type, AktivitaSachovnice.LOCALE_FILE);
        finish();
	}

}
