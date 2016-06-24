/*
    CuckooChess - A java chess program.
    Copyright (C) 2011  Peter Österlund, peterosterlund2@gmail.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package br.unicamp.ic.sed.gui.cuckoochess;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import br.unicamp.ic.sed.engine.impl.ComponentFactory;
import br.unicamp.ic.sed.engine.prov.IEngine;
import br.unicamp.ic.sed.engine.prov.IManager;
import br.unicamp.ic.sed.engine.req.IGUIInterface;
import br.unicamp.ic.sed.global.datatypes.ChessParseError;
import br.unicamp.ic.sed.global.datatypes.Config;
import br.unicamp.ic.sed.global.datatypes.InvalidMoveException;
import br.unicamp.ic.sed.global.datatypes.Move;
import br.unicamp.ic.sed.global.datatypes.Position;
import br.unicamp.ic.sed.global.utils.Utils;
import chess.spl.R;

public class MyCuckooChessActivity extends Activity implements IGUIInterface, OnMenuItemClickListener {
    MarkableChessBoard cb;
    IEngine engineComponent;
    boolean mShowThinking;
    int mTimeLimit;
    boolean playerWhite;
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

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                readPrefs();
                engineComponent.setHumanWhite(playerWhite);
            }
        });

        setContentView(R.layout.layout_cuckoochess_main);
        status = (TextView)findViewById(R.id.status);
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

        //adding persistance manager //required
        br.unicamp.ic.sed.persistancemgr.prov.IManager persistanceManager = br.unicamp.ic.sed.persistancemgr.impl.ComponentFactory.createInstance();

        //book-persistance connector vp
    	br.unicamp.ic.sed.bookmgr_persistance_vp.impl.IManager book_persistance_vp = br.unicamp.ic.sed.bookmgr_persistance_vp.impl.ComponentFactory.createInstance();
    	book_persistance_vp.setRequiredInterface("IPersistance", persistanceManager.getProvidedInterface("IPersistance"));

        //create the book - optional
        br.unicamp.ic.sed.bookmgr.prov.IManager bookManager = br.unicamp.ic.sed.bookmgr.impl.ComponentFactory.createInstance();

        //create the formatmgr - optional
        br.unicamp.ic.sed.formatmgr.prov.IManager formatManager = br.unicamp.ic.sed.formatmgr.impl.ComponentFactory.createInstance();

        //connector engine-book
        br.unicamp.ic.sed.engine_book_connector.impl.IManager engineBookConnectorManager = br.unicamp.ic.sed.engine_book_connector.impl.ComponentFactory.createInstance();
        engineBookConnectorManager.setRequiredInterface("IBook", bookManager.getProvidedInterface("IBook"));

        //connector engine-formatmgr
        br.unicamp.ic.sed.engine_format_connector.impl.IManager engineFormatConnectorManager = br.unicamp.ic.sed.engine_format_connector.impl.ComponentFactory.createInstance();
        engineFormatConnectorManager.setRequiredInterface("IFormat", formatManager.getProvidedInterface("IFormat"));

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
        engineManager.setRequiredInterface("IBook", engineBookConnectorManager.getProvidedInterface("IBook"));
        engineManager.setRequiredInterface("IFormat", engineFormatConnectorManager.getProvidedInterface("IFormat"));
        engineManager.setRequiredInterface("IHistory", engineHistoryConnectorManager.getProvidedInterface("IHistory"));
        //persistance-exception connector vp
    	br.unicamp.ic.sed.persistancemgr_exception_vp.impl.IManager persistance_exception_vp = br.unicamp.ic.sed.persistancemgr_exception_vp.impl.ComponentFactory.createInstance();
    	persistance_exception_vp.setRequiredInterface("IException", exceptionManager.getProvidedInterface("IException"));

        engineComponent = (IEngine) engineManager.getProvidedInterface("IEngine");
        engineComponent.setThreadStackSize(32768);
        readPrefs();

        Typeface chessFont = Typeface.createFromAsset(getAssets(), "casefont.ttf");
        cb.setFont(chessFont);
        cb.setFocusable(true);
        cb.requestFocus();
        cb.setClickable(true);
        cb.setLongClickable(true);

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

        cb.setOnTrackballListener(new MarkableChessBoard.OnTrackballListener() {
            public void onTrackballEvent(MotionEvent event) {
                if (engineComponent.humansTurn()) {
                    Move m = cb.handleTrackballEvent(event);
                    if (m != null) {
                        try {
							engineComponent.humanMove(m);
						} catch (InvalidMoveException e) {
							// TODO Auto-generated catch block
							//Toast.makeText(getApplicationContext(), "Invalid Move", Toast.LENGTH_SHORT).show();
							//handles this on the GUI
						}
                    }
                }
            }
        });

        /**
         * sets a long click manager on the MarkableChessBoard
         * note that MarkableChessBoard already implements the touchEvents
         * on onTouchEvent
         * */
        cb.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                if (!engineComponent.computerThinking())
                    showDialog(CLIPBOARD_DIALOG);
                return true;
            }
        });

        /* initialize the RateThisApp parameters */
        Config config = new Config(3, 5);
        config.setmTitleId(R.string.app_name);
        engineComponent.setRateThisAppParameters(config);
        /* finish the RateThisApp parameters */

        long endTime = System.currentTimeMillis();
        Log.w("ChessSPL", "Time to start=" + (endTime - initTime));
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


    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onPrepareOptionsMenu(menu);
    	menu.clear();

    	//LocalizationLoaderComponent
    	//localizationloadermgr.prov.IManager localizationloaderManager = (localizationloadermgr.prov.IManager) localizationloadermgr.impl.ComponentFactory.createInstance();

    	//Localization - LocalizationLoader connector
    	//localization_localizationmgr_connector.impl.IManager localizationconnector = (localization_localizationmgr_connector.impl.IManager) localization_localizationmgr_connector.impl.ComponentFactory.createInstance();
    	//localizationconnector.setRequiredInterface("ILocalizationLoader", localizationloaderManager.getProvidedInterface("ILocalizationLoader"));

    	//Localization Component
//    	br.unicamp.ic.sed.localization.prov.IManager localizationManager = br.unicamp.ic.sed.localization.impl.ComponentFactory.createInstance();
    	//localizationManager.setRequiredInterface("ILocalizationLoader",localizationconnector.getProvidedInterface("ILocalizationLoader") );

//    	ILocalization ilocalizationservices =  (ILocalization) br.unicamp.ic.sed.localization.impl.ComponentFactory.createInstance().getProvidedInterface("ILocalization");

    	//ilocalizationservices.changeLocale("ca");

//    	menu.add(Menu.NONE, 1, Menu.NONE, ilocalizationservices.getString("NEW_GAME")).setOnMenuItemClickListener(this);
//    	menu.add(Menu.NONE, 2, Menu.NONE, ilocalizationservices.getString("UNDO")).setOnMenuItemClickListener(this);
//    	menu.add(Menu.NONE, 3, Menu.NONE, ilocalizationservices.getString("REDO")).setOnMenuItemClickListener(this);
//    	menu.add(Menu.NONE, 4, Menu.NONE, ilocalizationservices.getString("SETTINGS")).setOnMenuItemClickListener(this);

    	menu.add(Menu.NONE, 1, Menu.NONE, "New Game").setOnMenuItemClickListener(this);
    	menu.add(Menu.NONE, 2, Menu.NONE, "Undo").setOnMenuItemClickListener(this);
    	menu.add(Menu.NONE, 3, Menu.NONE, "Redo").setOnMenuItemClickListener(this);
    	menu.add(Menu.NONE, 4, Menu.NONE, "Settings").setOnMenuItemClickListener(this);
        //getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }


    public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
        case 1:
            engineComponent.newGame(playerWhite, ttLogSize, false);
            engineComponent.startGame();
            return true;
        case 2:
            engineComponent.undoMove();
            return true;
        case 3:
            engineComponent.redoMove();
            return true;
        case 4:
        {
            Intent i = new Intent(MyCuckooChessActivity.this, MyPreferenceActivity.class);
            startActivityForResult(i, 0);
            return true;
        }
        }
        return false;
    }


    /*
    @Override
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
        if (requestCode == 0) {
            readPrefs();
            engineComponent.setHumanWhite(playerWhite);
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
    	if (!str.contains("Checks")) {
    		//filtering messages like:
    		//"Black Checks!", "White Checks!"
    		//cuckoo chess does not warn the user if it is in check or not
    		status.setText(str);
    	}
    }


    public void setMoveListString(String str) {
        moveList.setText(str);
        moveListScroll.fullScroll(ScrollView.FOCUS_DOWN);
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
    static final int CLIPBOARD_DIALOG = 1;


    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case PROMOTE_DIALOG: {
            final CharSequence[] items = {"Queen", "Rook", "Bishop", "Knight"};
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


        case CLIPBOARD_DIALOG: {
            final CharSequence[] items = {"Copy Game", "Copy Position", "Paste"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Clipboard");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switch (item) {
                    case 0: {
                        String pgn = engineComponent.getPGN();
                        ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                        clipboard.setText(pgn);
                        break;
                    }
                    case 1: {
                        String fen = engineComponent.getFEN() + "\n";
                        ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                        clipboard.setText(fen);
                        break;
                    }
                    case 2: {
                        ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                        if (clipboard.hasText()) {
                            String fenPgn = clipboard.getText().toString();
                            try {
                                engineComponent.setFENOrPGN(fenPgn, getApplicationContext());
                            } catch (ChessParseError e) {
                                //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    }
                    }
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
        String msg = String.format("Invalid move %s-%s", Utils.squareToString(m.from), Utils.squareToString(m.to));
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
