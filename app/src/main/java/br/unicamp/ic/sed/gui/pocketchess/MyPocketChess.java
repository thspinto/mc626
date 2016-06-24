package br.unicamp.ic.sed.gui.pocketchess;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import br.unicamp.ic.sed.engine.impl.ComponentFactory;
import br.unicamp.ic.sed.engine.prov.IEngine;
import br.unicamp.ic.sed.engine.prov.IManager;
import br.unicamp.ic.sed.engine.req.IGUIInterface;
import br.unicamp.ic.sed.global.datatypes.Config;
import br.unicamp.ic.sed.global.datatypes.Move;
import br.unicamp.ic.sed.global.datatypes.Position;
import chess.spl.R;

public class MyPocketChess extends Activity implements IGUIInterface, OnMenuItemClickListener{
	MovableChessBoard cb;
    IEngine engineComponent;
    boolean mShowThinking;
    int mTimeLimit;
    boolean playerWhite;
    static final int ttLogSize = 16; // Use 2^ttLogSize hash entries.

    private static TextView status;

//    SharedPreferences settings;

//    private void readPrefs() {
//        mShowThinking = settings.getBoolean("showThinking", true);
//        String timeLimitStr = settings.getString("timeLimit", "5000");
//        mTimeLimit = Integer.parseInt(timeLimitStr);
//        playerWhite = settings.getBoolean("playerWhite", true);
//        boolean boardFlipped = settings.getBoolean("boardFlipped", false);
//        cb.setFlipped(boardFlipped);
//        engineComponent.setTimeLimit();
//        String fontSizeStr = settings.getString("fontSize", "12");
//        int fontSize = Integer.parseInt(fontSizeStr);
//        status.setTextSize(fontSize);
//
//    }

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
//    	Debug.startMethodTracing("pocket_chess");
    	long initTime = System.currentTimeMillis();
        super.onCreate(savedInstanceState);

//        settings = PreferenceManager.getDefaultSharedPreferences(this);
//        settings.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
//
//            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//                readPrefs();
//                engineComponent.setHumanWhite(playerWhite);
//            }
//        });

        setContentView(R.layout.layout_pocket_main);
        status = (TextView) findViewById(R.id.pocket_txt_status);

        cb = (MovableChessBoard)findViewById(R.id.BoardView);

        //create the history - required
        br.unicamp.ic.sed.historymgr.prov.IManager historyManager = br.unicamp.ic.sed.historymgr.impl.ComponentFactory.createInstance();

        //create the engine_history connector - required
        br.unicamp.ic.sed.engine_history_vp.impl.IManager engineHistoryVP = br.unicamp.ic.sed.engine_history_vp.impl.ComponentFactory.createInstance();
        engineHistoryVP.setRequiredInterface("IHistory", historyManager.getProvidedInterface("IHistory"));

        //create the engine_history connector - required
        br.unicamp.ic.sed.engine_history_connector.impl.IManager engineHistoryConnectorManager = br.unicamp.ic.sed.engine_history_connector.impl.ComponentFactory.createInstance();
        engineHistoryConnectorManager.setRequiredInterface("IHistory", historyManager.getProvidedInterface("IHistory"));

        //create the book - optional
        //bookmgr.prov.IManager bookManager = bookmgr.impl.ComponentFactory.createInstance();

        //create the formatmgr - optional
        //formatmgr.prov.IManager formatManager = formatmgr.impl.ComponentFactory.createInstance();

        //connector engine-book
        //engine_book_connector.impl.IManager engineBookConnectorManager = engine_book_connector.impl.ComponentFactory.createInstance();
        //engineBookConnectorManager.setRequiredInterface("IBook", bookManager.getProvidedInterface("IBook"));

        //connector engine-formatmgr
        //engine_format_connector.impl.IManager engineFormatConnectorManager = engine_format_connector.impl.ComponentFactory.createInstance();
        //engineFormatConnectorManager.setRequiredInterface("IFormat", formatManager.getProvidedInterface("IFormat"));

        //connector exception-gui
        br.unicamp.ic.sed.exception_gui_connector.IManager exceptionGuiConnectorManager = br.unicamp.ic.sed.exception_gui_connector.ComponentFactory.createInstance();
        exceptionGuiConnectorManager.setRequiredInterface("IGUIInterface", this);

        //exception component, requires IGUIInterface from the connector exception-gui
        br.unicamp.ic.sed.exception.prov.IManager exceptionManager = br.unicamp.ic.sed.exception.impl.ComponentFactory.createInstance();
        exceptionManager.setRequiredInterface("IGUIInterface", exceptionGuiConnectorManager.getProvidedInterface("IGUIInterface"));

        //now link the engine with the exceptional component
        br.unicamp.ic.sed.engine_exception_vp.impl.IManager engineExceptionVPManager = br.unicamp.ic.sed.engine_exception_vp.impl.ComponentFactory.createInstance();
        engineExceptionVPManager.setRequiredInterface("IException", exceptionManager.getProvidedInterface("IException"));

        //engineComponent = new IEngineFacade(this);
        IManager engineManager = ComponentFactory.createInstance();
        engineManager.setRequiredInterface("IGUIInterface", this);

        //engineManager.setRequiredInterface("IBook", engineBookConnectorManager.getProvidedInterface("IBook"));
        //engineManager.setRequiredInterface("IFormat", engineFormatConnectorManager.getProvidedInterface("IFormat"));
        engineManager.setRequiredInterface("IHistory", engineHistoryConnectorManager.getProvidedInterface("IHistory"));

        engineComponent = (IEngine) engineManager.getProvidedInterface("IEngine");
        engineComponent.setThreadStackSize(32768);
//        readPrefs();

        Typeface chessFont = Typeface.createFromAsset(getAssets(), "casefont.ttf");
        cb.setFont(chessFont);
        cb.setFocusable(true);
        cb.requestFocus();
        cb.setClickable(true);

        playerWhite = true;
        mTimeLimit = 1000;
        engineComponent.newGame(playerWhite, ttLogSize, false);
//        {
//            String fen = "";
//            String moves = "";
//            String numUndo = "0";
//            String tmp;
//            if (savedInstanceState != null) {
//                tmp = savedInstanceState.getString("startFEN");
//                if (tmp != null) fen = tmp;
//                tmp = savedInstanceState.getString("moves");
//                if (tmp != null) moves = tmp;
//                tmp = savedInstanceState.getString("numUndo");
//                if (tmp != null) numUndo = tmp;
//            }
//            else {
//                tmp = settings.getString("startFEN", null);
//                if (tmp != null) fen = tmp;
//                tmp = settings.getString("moves", null);
//                if (tmp != null) moves = tmp;
//                tmp = settings.getString("numUndo", null);
//                if (tmp != null) numUndo = tmp;
//            }
//            List<String> posHistStr = new ArrayList<String>();
//            posHistStr.add(fen);
//            posHistStr.add(moves);
//            posHistStr.add(numUndo);
//            engineComponent.setPosHistory(posHistStr);
//        }

        cb.setEngineComponent(engineComponent);

        engineComponent.startGame();

        /* initialize the RateThisApp parameters */
        Config config = new Config(3, 5);
        config.setmTitleId(R.string.app_name);
        engineComponent.setRateThisAppParameters(config);
        /* finish the RateThisApp parameters */

        long endTime = System.currentTimeMillis();
        Log.w("ChessSPL", "Time to start=" + (endTime - initTime));
//        Debug.stopMethodTracing();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onPrepareOptionsMenu(menu);
    	menu.clear();

    	//LocalizationLoaderComponent
    	//localizationloadermgr.prov.IManager localizationloaderManager = (localizationloadermgr.prov.IManager) localizationloadermgr.impl.ComponentFactory.createInstance();

    	//Localization - LocalizationLoader connector
    	//localization_localizationmgr_connector.impl.IManager localizationconnector = (localization_localizationmgr_connector.impl.IManager) localization_localizationmgr_connector.impl.ComponentFactory.createInstance();
    	//localizationconnector.setRequiredInterface("ILocalizationLoader", localizationloaderManager.getProvidedInterface("ILocalizationLoader"));

    	//Localization Component
    	//localization.prov.IManager localizationManager = (localization.prov.IManager) localization.impl.ComponentFactory.createInstance();
    	//localizationManager.setRequiredInterface("ILocalizationLoader",localizationconnector.getProvidedInterface("ILocalizationLoader") );

//    	ILocalization ilocalizationservices =  (ILocalization) br.unicamp.ic.sed.localization.impl.ComponentFactory.createInstance().getProvidedInterface("ILocalization");

    	menu.add(Menu.NONE, 1, Menu.NONE, "Start Game").setOnMenuItemClickListener(this);
    	menu.add(Menu.NONE, 2, Menu.NONE, "Copyright Notice").setOnMenuItemClickListener(this);
    	menu.add(Menu.NONE, 3, Menu.NONE, "Help").setOnMenuItemClickListener(this);
    	menu.add(Menu.NONE, 4, Menu.NONE, "About").setOnMenuItemClickListener(this);
    	menu.add(Menu.NONE, 5, Menu.NONE, "Exit").setOnMenuItemClickListener(this);

    	//getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }


    public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
        case 1: //new game
            engineComponent.newGame(playerWhite, ttLogSize, false);
            engineComponent.startGame();
            return true;
        case 2: //copyright
        	Intent myIntent3 = new Intent(this.getBaseContext(), CopyrightActivity.class);
			//Intent myIntent3 = new Intent(this.getBaseContext(), GNUActivity.class);
			startActivityForResult(myIntent3, 0);
            return true;
        case 3: //HELP
        	Intent myIntent2 = new Intent(this.getBaseContext(), InstructionsActivity.class);
			startActivityForResult(myIntent2, 0);
            return true;
        case 4: //ABOUT
        	new AlertDialog.Builder (MyPocketChess.this)
            .setTitle (R.string.dialog_alert)
            .setMessage (R.string.dialog_message)
            .setIcon(R.drawable.icon_pocket)
            .setPositiveButton (R.string.dialog_ok_button, new DialogInterface.OnClickListener(){
                public void onClick (DialogInterface dialog, int whichButton){
                    setResult (RESULT_OK);
                }
            })
            .show ();
            return true;
        case 5: //EXIT
        	this.finish();
            return true;
        default:
        	return false;
		}
    }


    public void setPosition(Position pos) {
        cb.setPosition(pos);
        engineComponent.setHumanWhite(playerWhite);
    }


    public void setSelection(int sq) {
        cb.setSelection(sq);
    }


    public void setStatusString(String str) {
    	status.setText(str);
    }


    public void setMoveListString(String str) {
    }


    public void setThinkingString(String str) {
        if (!str.equals("")) status.setText(str);
    }


    public int timeLimit() {
        return mTimeLimit;
    }


    public boolean randomMode() {
        return mTimeLimit == -1;
    }


    public boolean showThinking() {
        return mShowThinking;
    }

    static final int PROMOTE_DIALOG = 0;
    static final int CLIPBOARD_DIALOG = 1;


	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROMOTE_DIALOG: {
			final CharSequence[] items = { "Queen", "Rook", "Bishop", "Knight" };
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Promote pawn to?");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					engineComponent.reportPromotePiece(item);
				}
			});
			AlertDialog alert = builder.create();
			return alert;
		}

		}
		return null;
    }


    public void requestPromotePiece() {
        runOnUIThread(new Runnable() {
            public void run() {
                showDialog(PROMOTE_DIALOG);
            }
        });
    }


    public void reportInvalidMove(Move m) {
        //String msg = String.format("Invalid move %s-%s", TextIO.squareToString(m.from), TextIO.squareToString(m.to));
    	String msg = "Invalid Move, try again";
    	Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    public void runOnUIThread(Runnable runnable) {
        runOnUiThread(runnable);
    }

    @Override
    public void onStart() {
      super.onStart();
      //begin methods of RateThisApp library
      // Monitor launch times and interval from installation
      engineComponent.setRateThisAppOnStart(this);
      // Show a dialog if criteria is satisfied
      engineComponent.setRateThisAppShowRateDialogIfNeeded(this);
      //end of RateThisApp library
    }
}
