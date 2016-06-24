package br.unicamp.ic.sed.gui.honzachess;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ScrollView;
import android.widget.TextView;
import br.unicamp.ic.sed.engine.impl.ComponentFactory;
import br.unicamp.ic.sed.engine.prov.IEngine;
import br.unicamp.ic.sed.engine.prov.IManager;
import br.unicamp.ic.sed.engine.req.IGUIInterface;
import br.unicamp.ic.sed.global.datatypes.Config;
import br.unicamp.ic.sed.global.datatypes.Move;
import br.unicamp.ic.sed.global.datatypes.Position;
import br.unicamp.ic.sed.localization.prov.ILocalization;
import chess.spl.R;

public class MyHonzaChessActivity extends Activity implements IGUIInterface, OnMenuItemClickListener {
    MarkableChessBoard cb;
    IEngine engineComponent;
    boolean mShowThinking;
    int mTimeLimit;
    static boolean playerWhite;
    static final int ttLogSize = 16; // Use 2^ttLogSize hash entries.

    TextView status;
    ScrollView moveListScroll;
    TextView moveList;
    TextView thinking;

    SharedPreferences settings;

    private void readPrefs() {

        mShowThinking = settings.getBoolean("showThinking", false);
        String timeLimitStr = settings.getString("timeLimit", "5000");
        mTimeLimit = Integer.parseInt(timeLimitStr);
        playerWhite = settings.getBoolean("playerWhite", true);
        boolean boardFlipped = settings.getBoolean("boardFlipped", false);
        cb.setFlipped(boardFlipped);
        engineComponent.setTimeLimit();
        String fontSizeStr = settings.getString("fontSize", "12");
        int fontSize = Integer.parseInt(fontSizeStr);
        status.setTextSize(fontSize);
        moveList.setTextSize(fontSize);
        thinking.setTextSize(fontSize);
    }

    /** Called when the activity is first created. */

    public void onCreate(Bundle savedInstanceState) {
    	long initTime = System.currentTimeMillis();

        super.onCreate(savedInstanceState);

        settings = getBaseContext().getSharedPreferences("honzasettings", MODE_PRIVATE);
        //set human-human mode to false
        Editor editor = settings.edit();
		editor.putBoolean("humanHuman", false);
		editor.commit();
//        settings.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
//            @Override
//            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//                readPrefs();
//                engineComponent.setHumanWhite(playerWhite);
//            }
//        });

        setContentView(R.layout.layout_honzachess_main);

        status = (TextView)findViewById(R.id.status);
        status.setVisibility(0); //hide the status bar
        moveListScroll = (ScrollView)findViewById(R.id.scrollView);
        moveList = (TextView)findViewById(R.id.moveList);
        thinking = (TextView)findViewById(R.id.thinking);
        cb = (MarkableChessBoard)findViewById(R.id.chessboard);

        status.setFocusable(false);
        moveListScroll.setFocusable(false);
        moveList.setFocusable(false);
        thinking.setFocusable(false);

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
        br.unicamp.ic.sed.formatmgr.prov.IManager formatManager = br.unicamp.ic.sed.formatmgr.impl.ComponentFactory.createInstance();

        //adding persistance manager //optional
        br.unicamp.ic.sed.persistancemgr.prov.IManager persistanceManager = br.unicamp.ic.sed.persistancemgr.impl.ComponentFactory.createInstance();

        //connector engine-book
        //engine_book_connector.impl.IManager engineBookConnectorManager = engine_book_connector.impl.ComponentFactory.createInstance();
        //engineBookConnectorManager.setRequiredInterface("IBook", bookManager.getProvidedInterface("IBook"));

        //connector engine-formatmgr
        br.unicamp.ic.sed.engine_format_connector.impl.IManager engineFormatConnectorManager = br.unicamp.ic.sed.engine_format_connector.impl.ComponentFactory.createInstance();
        engineFormatConnectorManager.setRequiredInterface("IFormat", formatManager.getProvidedInterface("IFormat"));

        //link format-mgr to the persistance mgr
        br.unicamp.ic.sed.formatmgr_persistance_connector_vp.impl.IManager formatmgrPersistanceVP = br.unicamp.ic.sed.formatmgr_persistance_connector_vp.impl.ComponentFactory.createInstance();
        formatmgrPersistanceVP.setRequiredInterface("IPersistance", persistanceManager.getProvidedInterface("IPersistance"));

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
        engineManager.setRequiredInterface("IFormat", engineFormatConnectorManager.getProvidedInterface("IFormat"));
        engineManager.setRequiredInterface("IHistory", engineHistoryConnectorManager.getProvidedInterface("IHistory"));

        /*starts the localization components for this application*/
        //LocalizationLoaderComponent
    	br.unicamp.ic.sed.localizationloadermgr.prov.IManager localizationloaderManager = br.unicamp.ic.sed.localizationloadermgr.impl.ComponentFactory.createInstance();

    	//Localization - LocalizationLoader connector
    	br.unicamp.ic.sed.localization_localizationmgr_connector.impl.IManager localizationconnector = br.unicamp.ic.sed.localization_localizationmgr_connector.impl.ComponentFactory.createInstance();
    	localizationconnector.setRequiredInterface("ILocalizationLoader", localizationloaderManager.getProvidedInterface("ILocalizationLoader"));

    	//localization-persistance connector vp
    	br.unicamp.ic.sed.localization_persistance_vp.impl.IManager localization_persistance_vp = br.unicamp.ic.sed.localization_persistance_vp.impl.ComponentFactory.createInstance();
    	localization_persistance_vp.setRequiredInterface("IPersistance", persistanceManager.getProvidedInterface("IPersistance"));

    	//localizationloadermgr-persistance connector vp
    	br.unicamp.ic.sed.localizationloadermgr_persistance_vp.impl.IManager localizationloadermgr_persistance_vp = br.unicamp.ic.sed.localizationloadermgr_persistance_vp.impl.ComponentFactory.createInstance();
    	localizationloadermgr_persistance_vp.setRequiredInterface("IPersistance", persistanceManager.getProvidedInterface("IPersistance"));

    	//Localization Component
    	br.unicamp.ic.sed.localization.prov.IManager localizationManager = br.unicamp.ic.sed.localization.impl.ComponentFactory.createInstance();
    	localizationManager.setRequiredInterface("ILocalizationLoader",localizationconnector.getProvidedInterface("ILocalizationLoader") );
    	/*ends the instantiation of the localization components for this application*/


    	//persistance-exception connector vp
    	br.unicamp.ic.sed.persistancemgr_exception_vp.impl.IManager persistance_exception_vp = br.unicamp.ic.sed.persistancemgr_exception_vp.impl.ComponentFactory.createInstance();
    	persistance_exception_vp.setRequiredInterface("IException", exceptionManager.getProvidedInterface("IException"));

    	mySetTitle();

        engineComponent = (IEngine) engineManager.getProvidedInterface("IEngine");
        engineComponent.setThreadStackSize(32768);
        readPrefs();

        Typeface chessFont = Typeface.createFromAsset(getAssets(), "casefont.ttf");
        cb.setFont(chessFont);
        cb.setFocusable(true);
        cb.requestFocus();
        cb.setClickable(true);

        engineComponent.newGame(playerWhite, ttLogSize, false);
        {
            String fen = "";
            String moves = "";
            String numUndo = "0";
            String tmp;
            if (savedInstanceState != null) {
                tmp = savedInstanceState.getString("startFEN");
                if (tmp != null) fen = tmp;
                tmp = savedInstanceState.getString("moves");
                if (tmp != null) moves = tmp;
                tmp = savedInstanceState.getString("numUndo");
                if (tmp != null) numUndo = tmp;
            } else {
                tmp = settings.getString("startFEN", null);
                if (tmp != null) fen = tmp;
                tmp = settings.getString("moves", null);
                if (tmp != null) moves = tmp;
                tmp = settings.getString("numUndo", null);
                if (tmp != null) numUndo = tmp;
            }
            List<String> posHistStr = new ArrayList<String>();
            posHistStr.add(fen);
            posHistStr.add(moves);
            posHistStr.add(numUndo);
            engineComponent.setPosHistory(posHistStr);
        }
        engineComponent.startGame();

        cb.setEngineComponent(engineComponent);


//        Button btn = (Button) findViewById(R.id.button1);
//        btn.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				moveList.append("PGN Text:" + engineComponent.getPGN("name", "site", Calendar.getInstance(), "1",
//						"whitePlayer",
//						"BlackPlayer", "10-1", "2000", "3000"));
//			}
//		});
        /* initialize the RateThisApp parameters */
        Config config = new Config(3, 5);
        config.setmTitleId(R.string.app_name);
        engineComponent.setRateThisAppParameters(config);
        /* finish the RateThisApp parameters */


        long endTime = System.currentTimeMillis();
        Log.w("ChessSPL", "Time to start=" + (endTime - initTime));
    }

    private void mySetTitle() {
    	ILocalization ilocalizationservices =  (ILocalization) br.unicamp.ic.sed.localization.impl.ComponentFactory.createInstance().getProvidedInterface("ILocalization");
        setTitle(ilocalizationservices.getString("HONZOVY_SACHY"));
    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        List<String> posHistStr = engineComponent.getPosHistory();
        outState.putString("startFEN", posHistStr.get(0));
        outState.putString("moves", posHistStr.get(1));
        outState.putString("numUndo", posHistStr.get(2));
    }


    protected void onPause() {
        List<String> posHistStr = engineComponent.getPosHistory();
        Editor editor = settings.edit();
        editor.putString("startFEN", posHistStr.get(0));
        editor.putString("moves", posHistStr.get(1));
        editor.putString("numUndo", posHistStr.get(2));
        editor.commit();
        super.onPause();
    }


    protected void onDestroy() {
        engineComponent.stopComputerThinking();
        super.onDestroy();
    }


    /**@author gmw
     * watch out this function, this is not onCreateOptionsMenu
     * onCreateOptionsMenu is called ONCE, this function is called within
     * the onCreateOptionsMenu, this is required to make sure the optionsmenu is created again*/
    public boolean onPrepareOptionsMenu(Menu menu) {
    	super.onPrepareOptionsMenu(menu);
    	menu.clear();

    	ILocalization ilocalizationservices =  (ILocalization) br.unicamp.ic.sed.localization.impl.ComponentFactory.createInstance().getProvidedInterface("ILocalization");

    	menu.add(Menu.NONE, 0, Menu.NONE, ilocalizationservices.getString("FLIP_BOARD")).setOnMenuItemClickListener(this);
    	menu.add(Menu.NONE, 5, Menu.NONE, ilocalizationservices.getString("MOVE")).setOnMenuItemClickListener(this);
    	menu.add(Menu.NONE, 1, Menu.NONE, ilocalizationservices.getString("NEW_GAME")).setOnMenuItemClickListener(this);
    	menu.add(Menu.NONE, 2, Menu.NONE, ilocalizationservices.getString("UNDO")).setOnMenuItemClickListener(this);
    	menu.add(Menu.NONE, 3, Menu.NONE, ilocalizationservices.getString("REDO")).setOnMenuItemClickListener(this);
    	menu.add(Menu.NONE, 6, Menu.NONE, ilocalizationservices.getString("SAVE_GAME")).setOnMenuItemClickListener(this);
    	menu.add(Menu.NONE, 7, Menu.NONE, ilocalizationservices.getString("HUMAN_OPONENT")).setOnMenuItemClickListener(this);
    	menu.add(Menu.NONE, 4, Menu.NONE, ilocalizationservices.getString("SETTINGS")).setOnMenuItemClickListener(this);
    	menu.add(Menu.NONE, 8, Menu.NONE, ilocalizationservices.getString("ABOUT")).setOnMenuItemClickListener(this);
    	//getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    static final int REQUEST_SAVE = 10;
    static final int REQUEST_SETTINGS = 0;

	public boolean onMenuItemClick(MenuItem item) {
		boolean valueToReturn = true;
		Editor editor = settings.edit();
		switch (item.getItemId()) {
		case 0:
			// flip board
			boolean shouldFlipp = !cb.getFlipped();
			cb.setFlipped(shouldFlipp);
			break;
		case 5:
			// perform move and reset the human human to false
			engineComponent.startComputerMove();
			playerWhite = !playerWhite;
			editor.putBoolean("humanHuman", false);
			editor.commit();
			break;
		case 6:
			// request to save the game
			Intent intentSaveActivity = new Intent();
			intentSaveActivity.setClass(MyHonzaChessActivity.this,
					PGNSaveActivity.class);
			startActivityForResult(intentSaveActivity, REQUEST_SAVE);
			break;
		case 7:
			// humam oponent
			editor.putBoolean("humanHuman", true);
			editor.commit();
			break;
		case 8:
			// about
			Intent intent = new Intent();
			intent.setClass(this, AboutActivity.class);
			startActivity(intent);
			break;
		case 1:
			// new game
			playerWhite = true;
			engineComponent.newGame(playerWhite, ttLogSize, false);
			engineComponent.startGame();
			shownMessage = false;
			break;
		case 2:
			// undo
			if (engineComponent.undoOneMove()) {
				playerWhite = !playerWhite;
				cb.setSelection(-1);
			}
			break;
		case 3:
			// redo
			if (engineComponent.redoOneMove()) {
				playerWhite = !playerWhite;
				cb.setSelection(-1);
			}
			break;
		case 4:
			// settings
			Intent i = new Intent(MyHonzaChessActivity.this,
					TabSettingsActivity.class);
			startActivityForResult(i, REQUEST_SETTINGS);
			break;
		default:
			valueToReturn = false;
			break;
		}

		return valueToReturn;
	}

    /*

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.item_new_game:
            ctrl.newGame(playerWhite, ttLogSize, false);
            ctrl.startGame();
            return true;
        case R.id.item_undo:
            ctrl.takeBackMove();
            return true;
        case R.id.item_redo:
            ctrl.redoMove();
            return true;
        case R.id.item_settings:
        {
            Intent i = new Intent(CuckooChess.this, Preferences.class);
            startActivityForResult(i, 0);
            return true;
        }
        }
        return false;
    }
	*/


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SETTINGS) {
			// on returning from about, read the preferences again;
			mySetTitle();
			readPrefs();
			engineComponent.setHumanWhite(playerWhite);
		} else {
			// if the PGNSaveActivity returns "I suceeded", then show dialog.
			if (requestCode == REQUEST_SAVE && resultCode == RESULT_OK) {
				String filename = data.getStringExtra("filename");
				ILocalization ilocalizationservices =  (ILocalization) br.unicamp.ic.sed.localization.impl.ComponentFactory.createInstance().getProvidedInterface("ILocalization");
				createDialogBox(ilocalizationservices.getString("SAVED_AS") + filename + ".");
			}
		}
	}


    public void setPosition(Position pos) {
        cb.setPosition(pos);
        engineComponent.setHumanWhite(playerWhite);
    }


    public void setSelection(int sq) {
        cb.setSelection(sq);
    }

    /** this variable is used to track down if the alert
     * dialog was displayed once it should not appear again
     * when it creates a new game this variable is set to false
     * when it is displayed it should change to true
     * it is used on {@code setStatusString createDialogBox}
     */
    private boolean shownMessage = false;


    public void setStatusString(String str) {
        //filtering the messages
    	if (str.contains("draw") || str.contains("mate")) {
    		/**
    	     * possible returns:
    	     * "Game over, white mates!"; <br>
    	     * "Game over, black mates!"; <br>
    	     * "Game over, draw by stalemate!"; <br>
    	     * "Game over, draw by repetition!" <br>
    	     * "Game over, draw by 50 move rule!" <br>
    	     * "Game over, draw by impossibility of mate!" <br>
    	     * "Game over, draw by agreement!" <br>
    	     * "Game over, white resigns!" <br>
    	     * "Game over, black resigns!" <br>
    	     * "Black Checks!" <br>
    	     * "White Checks!" <br>
    	     * */
        	//calls the localization services to translate the message to the user:
    		ILocalization ilocalizationservices =  (ILocalization) br.unicamp.ic.sed.localization.impl.ComponentFactory.createInstance().getProvidedInterface("ILocalization");

    		String msg = str;

    		if (str.contains("mate")) {
    			if (str.contains("white")) {
    				msg = ilocalizationservices.getString("WHITE_WINS") +
    						ilocalizationservices.getString("COMMA_BLACK_CHECKMATED");
    			} else {
    				msg = ilocalizationservices.getString("BLACK_WINS") +
    						ilocalizationservices.getString("COMMA_WHITE_CHECKMATED");
    			}
    		} else {
    			if (str.contains("draw")) {
    				msg = ilocalizationservices.getString("DRAW");
    				if (str.contains("stalemate")) {
    					msg += ilocalizationservices.getString("COMMA_STALEMATE");
    				} else {
    					if (str.contains("repetition")) {
    						msg += ilocalizationservices.getString("COMMA_3_TIMES_REPETITION");
    					} else {
    						if (str.contains("50 move")) {
        						msg += ilocalizationservices.getString("COMMA_50_MOVES_RULE");
        					}
    					}
    				}
    			}
    		}
        	createDialogBox(msg);

        } else {
        	//this application doesnt have the set status, sending feedback to the user
        	//status.setText(str);
        }
    }

    /** creates a dialog box with the string provided
     * @param message string to be show on the screen
     * */
	private void createDialogBox(String message) {
		if (this.shownMessage == false) {
			Dialog d = new Dialog(this);
			d.setTitle(message);
			d.show();
			shownMessage = true;
		}
	}


    public void setMoveListString(String str) {
//        moveList.setText(str);
//        moveListScroll.fullScroll(ScrollView.FOCUS_DOWN);
    }


    public void setThinkingString(String str) {
        thinking.setText(str);
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
// this is to implement the clipboard management, for now it will be commented
//    static final int CLIPBOARD_DIALOG = 1;


    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case PROMOTE_DIALOG: {
            final CharSequence[] items = {"Queen", "Rook", "Bishop", "Knight"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Promote pawn to?");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
					boolean humanHuman = settings.getBoolean("humanHuman", false);
					if (humanHuman) {
						engineComponent.reportPromotePieceHumanHuman(item);
						// playerWhite = !playerWhite;
					} else {
						engineComponent.reportPromotePiece(item);
					}
                }
            });
            AlertDialog alert = builder.create();
            return alert;
        }

// the following section is commented on purpose, it implements the clipboard stuff
//        case CLIPBOARD_DIALOG: {
//            final CharSequence[] items = {"Copy Game", "Copy Position", "Paste"};
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Clipboard");
//            builder.setItems(items, new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int item) {
//                    switch (item) {
//                    case 0: {
//                        String pgn = engineComponent.getPGN();
//                        ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
//                        clipboard.setText(pgn);
//                        break;
//                    }
//                    case 1: {
//                        String fen = engineComponent.getFEN() + "\n";
//                        ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
//                        clipboard.setText(fen);
//                        break;
//                    }
//                    case 2: {
//                        ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
//                        if (clipboard.hasText()) {
//                            String fenPgn = clipboard.getText().toString();
//                            try {
//                                engineComponent.setFENOrPGN(fenPgn, getApplicationContext());
//                            } catch (ChessParseError e) {
//                                //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                        break;
//                    }
//                    }
//                }
//            });
//            AlertDialog alert = builder.create();
//            return alert;
//        }
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

    /**
     * on the Honza Chess it does nothing on purpose
     * it is expected that this function does nothing
     * this function is fired when on tried to perform
     * an InvalidMoveException type
     * */
    public void reportInvalidMove(Move m) {
    	//do nothing;
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
