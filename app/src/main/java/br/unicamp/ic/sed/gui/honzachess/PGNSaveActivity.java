package br.unicamp.ic.sed.gui.honzachess;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import br.unicamp.ic.sed.engine.prov.IEngine;
import br.unicamp.ic.sed.localization.prov.ILocalization;
import chess.spl.R;

public class PGNSaveActivity extends Activity implements OnDateSetListener {
	private int mYear = 2014;
	private int mMonth = 3;
	private int mDay = 8;

	private final String[] _RESULTS = {"*", "1-0", "0-1", "1/2-1/2"};


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	ILocalization ilocalizationservices =  (ILocalization) br.unicamp.ic.sed.localization.impl.ComponentFactory.createInstance().getProvidedInterface("ILocalization");

        this.setTitle(ilocalizationservices.getString("SAVE_PGN"));
        setContentView(R.layout.layout_honzachess_pgn_header);
        final Button save = (Button)findViewById(R.id.save);
        final Button date = (Button)findViewById(R.id.date);
        final EditText filename = (EditText)findViewById(R.id.filename);
        final EditText white = (EditText)findViewById(R.id.white);
        final EditText black = (EditText)findViewById(R.id.black);
        final EditText event = (EditText)findViewById(R.id.event);
        final EditText site = (EditText)findViewById(R.id.site);
        final EditText round = (EditText)findViewById(R.id.round);
        final EditText whiteelo = (EditText)findViewById(R.id.whiteelo);
        final EditText blackelo = (EditText)findViewById(R.id.blackelo);
        final Spinner gameResult = (Spinner)findViewById(R.id.result);

        TextView tFileName = (TextView)findViewById(R.id.filenamestatic);
        TextView tWhite = (TextView)findViewById(R.id.whitestatic);
        TextView tBlack = (TextView)findViewById(R.id.blackstatic);
        TextView tResult = (TextView)findViewById(R.id.resultstatic);
        TextView tEvent = (TextView)findViewById(R.id.eventstatic);
        TextView tSite = (TextView)findViewById(R.id.sitestatic);
        TextView tDate = (TextView)findViewById(R.id.datestatic);
        TextView tRound = (TextView)findViewById(R.id.roundstatic);
        TextView tWhiteElo = (TextView)findViewById(R.id.whiteelostatic);
        TextView tBlackElo = (TextView)findViewById(R.id.blackelostatic);

        tFileName.setText(ilocalizationservices.getString("FILE_NAME") + ":");
        tWhite.setText(ilocalizationservices.getString("WHITE") + ":");
        tBlack.setText(ilocalizationservices.getString("BLACK") + ":");
        tResult.setText(ilocalizationservices.getString("RESULT") + ":");
        tEvent.setText(ilocalizationservices.getString("EVENT") + ":");
        tSite.setText(ilocalizationservices.getString("SITE") + ":");
        tDate.setText(ilocalizationservices.getString("DATE") + ":");
        tRound.setText(ilocalizationservices.getString("ROUND") + ":");
        tWhiteElo.setText(ilocalizationservices.getString("WHITE_ELO") + ":");
        tBlackElo.setText(ilocalizationservices.getString("BLACK_ELO") + ":");


        save.setText(ilocalizationservices.getString("SAVE"));
        filename.setText("/sdcard/" + ilocalizationservices.getString("GAME") + ".pgn");
        white.setText(ilocalizationservices.getString("WHITE"));
        black.setText(ilocalizationservices.getString("BLACK"));
        event.setText(ilocalizationservices.getString("EVENT"));
        site.setText(ilocalizationservices.getString("PRAGUE"));


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
        		this, android.R.layout.simple_spinner_item,
        		_RESULTS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gameResult.setAdapter(adapter);

        date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(0);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent result = new Intent();
            	int iWhiteElo = 1550;
            	try {
            		iWhiteElo = Integer.parseInt(whiteelo.getText().toString());
            	} catch (NumberFormatException e) {};
            	int iBlackElo = 1550;
            	try {
            		iBlackElo = Integer.parseInt(blackelo.getText().toString());
            	} catch (NumberFormatException e) {};
            	int iRound = 1;
            	try {
            		iRound = Integer.parseInt(round.getText().toString());
            	} catch (NumberFormatException e) {};

            	//needs to save the information into the file and return with a result
            	IEngine engineComponent = (IEngine) br.unicamp.ic.sed.engine.impl.ComponentFactory.createInstance().getProvidedInterface("IEngine");
            	//create a meaningfull date for my API
            	Calendar myDate = GregorianCalendar.getInstance();
            	myDate.set(mYear, mMonth, mDay);

            	//now gets the PGN and save it into the filepath
            	String PGNStringToSave = engineComponent.getPGN( event.getText().toString(), site.getText().toString(), myDate, round.getText().toString(),
        				white.getText().toString(),
        				black.getText().toString(), round.getText().toString(), whiteelo.getText().toString(), blackelo.getText().toString());
            	engineComponent.savePGNToFile(PGNStringToSave, filename.getText().toString());
            	Intent i = new Intent();
            	i.putExtra("filename", filename.getText().toString());

            	setResult(RESULT_OK, i); //on getting the result, the caller activity shows the save information
                finish();
            }
        });
	}


	protected Dialog onCreateDialog(int id) {
		   if (id == 0) {
			   return new DatePickerDialog(this,
                       this,
                       mYear, mMonth, mDay);
		   }
		   return null;
	}


	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		mYear = year;
		mMonth = monthOfYear;
		mDay = dayOfMonth;

		Button date = (Button)findViewById(R.id.date);
		date.setText(year + "-" + (monthOfYear + 1) + "-" +dayOfMonth);

	}

}
