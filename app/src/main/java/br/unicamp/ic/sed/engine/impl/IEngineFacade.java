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

package br.unicamp.ic.sed.engine.impl;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import android.content.Context;
import android.util.Log;
import br.unicamp.ic.sed.engine.impl.Game.GameState;
import br.unicamp.ic.sed.engine.prov.IEngine;
import br.unicamp.ic.sed.engine.req.IFormat;
import br.unicamp.ic.sed.engine.req.IGUIInterface;
import br.unicamp.ic.sed.engine.req.IHistory;
import br.unicamp.ic.sed.global.datatypes.ChessParseError;
import br.unicamp.ic.sed.global.datatypes.Config;
import br.unicamp.ic.sed.global.datatypes.InvalidMoveException;
import br.unicamp.ic.sed.global.datatypes.Move;
import br.unicamp.ic.sed.global.datatypes.Piece;
import br.unicamp.ic.sed.global.datatypes.Position;
import br.unicamp.ic.sed.global.datatypes.UndoInfo;

/**
 * The glue between the chess engine and the GUI.
 * @author petero
 */
public class IEngineFacade implements IEngine{
    Player humanPlayer;
    ComputerPlayer computerPlayer;
    Game game;
    Manager manager;
    //IGUIInterface gui;
    private boolean humanIsWhite;
    Thread computerThread;
    int threadStack;       // Thread stack size, or zero to use OS default

    // Search statistics
    String thinkingPV;

    class SearchListener implements Search.Listener {
        int currDepth = 0;
        int currMoveNr = 0;
        String currMove = "";
        long currNodes = 0;
        int currNps = 0;
        int currTime = 0;

        int pvDepth = 0;
        int pvScore = 0;
        boolean pvIsMate = false;
        boolean pvUpperBound = false;
        boolean pvLowerBound = false;
        String pvStr = "";

        private void setSearchInfo() {
            StringBuilder buf = new StringBuilder();
            buf.append(String.format(Locale.US, "%n[%d] ", pvDepth));
            if (pvUpperBound) {
                buf.append("<=");
            } else if (pvLowerBound) {
                buf.append(">=");
            }
            if (pvIsMate) {
                buf.append(String.format(Locale.US, "m%d", pvScore));
            } else {
                buf.append(String.format(Locale.US, "%.2f", pvScore / 100.0));
            }
            buf.append(pvStr);
            buf.append(String.format(Locale.US, "%n"));
            buf.append(String.format(Locale.US, "d:%d %d:%s t:%.2f n:%d nps:%d", currDepth,
                    currMoveNr, currMove, currTime / 1000.0, currNodes, currNps));
            final String newPV = buf.toString();
            IGUIInterface gui = (IGUIInterface) manager.getRequiredInterface("IGUIInterface");
            gui.runOnUIThread(new Runnable() {
                public void run() {
                    thinkingPV = newPV;
                    setThinkingPV();
                }
            });
        }

        public void notifyDepth(int depth) {
            currDepth = depth;
            setSearchInfo();
        }

        public void notifyCurrMove(Move m, int moveNr) {
            currMove = TextIO.moveToString(new Position(game.pos), m, false);
            currMoveNr = moveNr;
            setSearchInfo();
        }

        public void notifyPV(int depth, int score, int time, long nodes, int nps, boolean isMate,
                boolean upperBound, boolean lowerBound, ArrayList<Move> pv) {
            pvDepth = depth;
            pvScore = score;
            currTime = time;
            currNodes = nodes;
            currNps = nps;
            pvIsMate = isMate;
            pvUpperBound = upperBound;
            pvLowerBound = lowerBound;

            StringBuilder buf = new StringBuilder();
            Position pos = new Position(game.pos);
            UndoInfo ui = new UndoInfo();
            for (Move m : pv) {
                buf.append(String.format(Locale.US, " %s", TextIO.moveToString(pos, m, false)));
                pos.makeMove(m, ui);
            }
            pvStr = buf.toString();
            setSearchInfo();
        }

        public void notifyStats(long nodes, int nps, int time) {
            currNodes = nodes;
            currNps = nps;
            currTime = time;
            setSearchInfo();
        }
    }
    SearchListener listener;

    public IEngineFacade(Manager mgr) {
    	this.manager = mgr;

        listener = new SearchListener();
        thinkingPV = "";
        threadStack = 0;
    }

    /*
    public IEngineFacade(IGUIInterface gui) {
        this.gui = gui;
        listener = new SearchListener();
        thinkingPV = "";
        threadStack = 0;
    }
    */

    public final void setThreadStackSize(int size) {
        threadStack = size;
    }

    public final void newGame(boolean humanIsWhite, int ttLogSize, boolean verbose) {
        stopComputerThinking();
        this.humanIsWhite = humanIsWhite;
        humanPlayer = new HumanPlayer();
        computerPlayer = new ComputerPlayer(this.manager);
        computerPlayer.verbose = verbose;
        computerPlayer.setTTLogSize(ttLogSize);
        computerPlayer.setListener(listener);
        if (humanIsWhite) {
            game = new Game(humanPlayer, computerPlayer, this.manager);
        } else {
            game = new Game(computerPlayer, humanPlayer, this.manager);
        }
    }
    public final void startGame() {
    	IGUIInterface gui = (IGUIInterface) manager.getRequiredInterface("IGUIInterface");
        gui.setSelection(-1);
        updateGUI();
        startComputerThinking();
    }

    public final void setPosHistory(List<String> posHistStr) {
        try {
            String fen = posHistStr.get(0);
            Position pos = TextIO.readFEN(fen);
            game.processString("new");
            game.pos = pos;
            String[] strArr = posHistStr.get(1).split(" ");
            final int arrLen = strArr.length;
            for (int i = 0; i < arrLen; i++) {
                game.processString(strArr[i]);
            }
            int numUndo = Integer.parseInt(posHistStr.get(2));
            for (int i = 0; i < numUndo; i++) {
                game.processString("undo");
            }
        } catch (ChessParseError e) {
            // Just ignore invalid positions
        }
    }

    public final List<String> getPosHistory() {
        return game.getPosHistory();
    }

    public String getFEN() {
        return TextIO.toFEN(game.pos);
    }

    /** Convert current game to PGN format. */
    public String getPGN() {
//        StringBuilder pgn = new StringBuilder();
//        List<String> posHist = getPosHistory();
//        String fen = posHist.get(0);
//        String moves = game.getMoveListString(true);
//        if (game.getGameState() == GameState.ALIVE)
//            moves += " *";
//        int year, month, day;
//        {
//            Calendar now = GregorianCalendar.getInstance();
//            year = now.get(Calendar.YEAR);
//            month = now.get(Calendar.MONTH) + 1;
//            day = now.get(Calendar.DAY_OF_MONTH);
//        }
//        pgn.append(String.format(Locale.US, "[Date \"%04d.%02d.%02d\"]%n", year, month, day));
//        String white = "Player";
//        String black = ComputerPlayer.engineName;
//        if (!humanIsWhite) {
//            String tmp = white; white = black; black = tmp;
//        }
//        pgn.append(String.format(Locale.US, "[White \"%s\"]%n", white));
//        pgn.append(String.format(Locale.US, "[Black \"%s\"]%n", black));
//        pgn.append(String.format(Locale.US, "[Result \"%s\"]%n", game.getPGNResultString()));
//
//        if (!fen.equals(TextIO.startPosFEN)) {
//            pgn.append(String.format(Locale.US, "[FEN \"%s\"]%n", fen));
//            pgn.append("[SetUp \"1\"]\n");
//        }
//        pgn.append("\n");
//        String[] strArr = moves.split(" ");
//        int currLineLength = 0;
//        final int arrLen = strArr.length;
//        for (int i = 0; i < arrLen; i++) {
//            String move = strArr[i].trim();
//            int moveLen = move.length();
//            if (moveLen > 0) {
//                if (currLineLength + 1 + moveLen >= 80) {
//                    pgn.append("\n");
//                    pgn.append(move);
//                    currLineLength = moveLen;
//                } else {
//                    if (currLineLength > 0) {
//                        pgn.append(" ");
//                        currLineLength++;
//                    }
//                    pgn.append(move);
//                    currLineLength += moveLen;
//                }
//            }
//        }
//        pgn.append("\n\n");
//        return pgn.toString();

    	IFormat format = (IFormat) this.manager.getRequiredInterface("IFormat");
    	if (format != null) {
    		boolean gameIsAlive = game.getGameState() == GameState.ALIVE;

    		return format.getPGNFormat(null, null, GregorianCalendar.getInstance(), null,
    				"Player", ComputerPlayer.engineName, gameIsAlive, game.getPGNResultString(), null,
    				null, null,
    				getPosHistory(), game.getMoveListString(true), gameIsAlive);
    	} else {
    		Log.w("debug", "Component FormatMgr is not present, its optional");
    	}

    	return null;
    }


	public String getPGN(String eventName, String site, Calendar date,
			String round, String nameWhitePlayer, String nameBlackPlayer,
			String gameResult, String whiteElo, String blackElo) {

    	IFormat format = (IFormat) this.manager.getRequiredInterface("IFormat");
    	if (format != null) {
    		boolean gameIsAlive = game.getGameState() == GameState.ALIVE;
    		IHistory history = (IHistory) this.manager.getRequiredInterface("IHistory");

    		if ( history != null ) {

    			return format.getPGNFormat(eventName, site, date, round,
    				nameWhitePlayer, nameBlackPlayer, gameIsAlive, gameResult, whiteElo,
    				blackElo, Integer.valueOf(history.moveListSize()).toString() ,
    				getPosHistory(), game.getMoveListString(true), gameIsAlive);
    		} else {
    			return format.getPGNFormat(eventName, site, date, round,
        				nameWhitePlayer, nameBlackPlayer, gameIsAlive, gameResult, whiteElo,
        				blackElo, Integer.valueOf(game.currentMove).toString() ,
        				getPosHistory(), game.getMoveListString(true), gameIsAlive);
    		}
    	} else {
    		Log.w("debug", "Component FormatMgr is not present, its optional");
    	}

    	return null;
	}

    public void setPGN(String pgn) throws ChessParseError {
        // First pass, remove comments
        {
            StringBuilder out = new StringBuilder();
            Scanner sc = new Scanner(pgn);
            sc.useDelimiter("");
            while (sc.hasNext()) {
                String c = sc.next();
                if (c.equals("{")) {
                    sc.skip("[^}]*}");
                } else if (c.equals(";")) {
                    sc.skip("[^\n]*\n");
                } else {
                    out.append(c);
                }
            }
            pgn = out.toString();
        }

        // Parse tag section
        Log.w("debug", "parse readFen start pos SETPGN");
        Position pos = TextIO.readFEN(TextIO.startPosFEN);
        Scanner sc = new Scanner(pgn);
        sc.useDelimiter("\\s+");
        while (sc.hasNext("\\[.*")) {
            String tagName = sc.next();
            if (tagName.length() > 1) {
                tagName = tagName.substring(1);
            } else {
                tagName = sc.next();
            }
            String tagValue = sc.findWithinHorizon(".*\\]", 0);
            tagValue = tagValue.trim();
            if (tagValue.charAt(0) == '"')
                tagValue = tagValue.substring(1);
            if (tagValue.charAt(tagValue.length()-1) == ']')
                tagValue = tagValue.substring(0, tagValue.length() - 1);
            if (tagValue.charAt(tagValue.length()-1) == '"')
                tagValue = tagValue.substring(0, tagValue.length() - 1);
            if (tagName.equals("FEN")) {
                pos = TextIO.readFEN(tagValue);
            }
        }
        game.processString("new");
        game.pos = pos;

        // Handle (ignore) recursive annotation variations
        {
            StringBuilder out = new StringBuilder();
            sc.useDelimiter("");
            int level = 0;
            while (sc.hasNext()) {
                String c = sc.next();
                if (c.equals("(")) {
                    level++;
                } else if (c.equals(")")) {
                    level--;
                } else if (level == 0) {
                    out.append(c);
                }
            }
            pgn = out.toString();
        }

        // Parse move text section
        sc = new Scanner(pgn);
        sc.useDelimiter("\\s+");
        while (sc.hasNext()) {
            String strMove = sc.next();
            strMove = strMove.replaceFirst("\\$?[0-9]*\\.*([^?!]*)[?!]*", "$1");
            if (strMove.length() == 0) continue;
            Move m = TextIO.stringToMove(game.pos, strMove);
            if (m == null)
                break;
            game.processString(strMove);
        }
    }

    public void setFENOrPGN(String fenPgn, Context cx) throws ChessParseError {
        try {
            Position pos = TextIO.readFEN(fenPgn);
            game.processString("new");
            game.pos = pos;
        } catch (ChessParseError e) {
            // Try read as PGN instead
            setPGN(fenPgn);
        }
        IGUIInterface gui = (IGUIInterface) manager.getRequiredInterface("IGUIInterface");
        gui.setSelection(-1);
        updateGUI();
        startComputerThinking();
    }

    /** Set color for human player. Doesn't work when computer is thinking. */
    public final void setHumanWhite(final boolean humanIsWhite) {
        if (computerThread != null)
            return;
        if (this.humanIsWhite != humanIsWhite) {
            this.humanIsWhite = humanIsWhite;
            game.processString("swap");
            startComputerThinking();
        }
    }

    public final boolean humansTurn() {
        return game.pos.isWhiteTurn() == humanIsWhite;
    }
    public final boolean computerThinking() {
        return computerThread != null;
    }

    public final void undoMove() {
        if (humansTurn()) {
            if (game.getLastMove() != null) {
                game.processString("undo");
                if (game.getLastMove() != null) {
                    game.processString("undo");
                } else {
                    game.processString("redo");
                }
                updateGUI();
                setSelection();
            }
        } else if (game.getGameState() != Game.GameState.ALIVE) {
            if (game.getLastMove() != null) {
                game.processString("undo");
                if (!humansTurn()) {
                    if (game.getLastMove() != null) {
                        game.processString("undo");
                    } else {
                        game.processString("redo");
                    }
                }
                updateGUI();
                setSelection();
            }
        }
    }

    public final void redoMove() {
        if (humansTurn()) {
            game.processString("redo");
            game.processString("redo");
            updateGUI();
            setSelection();
        }
    }


    public final void humanMove(Move m) throws InvalidMoveException{
        if (humansTurn()) {
            if (doMove(m)) {
                updateGUI();
                startComputerThinking();
            } else {
            	IGUIInterface gui = (IGUIInterface) manager.getRequiredInterface("IGUIInterface");
                gui.setSelection(-1);
            }
        }
    }

    Move promoteMove;
    public final void reportPromotePiece(int choice) {
        final boolean white = game.pos.isWhiteTurn();
        int promoteTo;
        switch (choice) {
            case 1:
                promoteTo = white ? Piece.WROOK : Piece.BROOK;
                break;
            case 2:
                promoteTo = white ? Piece.WBISHOP : Piece.BBISHOP;
                break;
            case 3:
                promoteTo = white ? Piece.WKNIGHT : Piece.BKNIGHT;
                break;
            default:
                promoteTo = white ? Piece.WQUEEN : Piece.BQUEEN;
                break;
        }
        promoteMove.promoteTo = promoteTo;
        Move m = promoteMove;
        promoteMove = null;

		try {
			humanMove(m);
		} catch (InvalidMoveException e) {
			// this is not necessary since
			// it is a promotion movement, it will be valid
		}
    }

    /**
     * Move a piece from one square to another.
     * the validation if the movement was legal should be done here
     * @return True if the move was legal
     * @throws InvalidMoveException if the movement was illegal
     */
    final private boolean doMove(Move move) throws InvalidMoveException{
    	IGUIInterface gui = (IGUIInterface) manager.getRequiredInterface("IGUIInterface");
        Position pos = game.pos;
        MoveGen.MoveList moves = new MoveGen().pseudoLegalMoves(pos);
        MoveGen.removeIllegal(pos, moves);
        int promoteTo = move.promoteTo;
        for (int mi = 0; mi < moves.size; mi++) {
            Move m = moves.m[mi];
            if ((m.from == move.from) && (m.to == move.to)) {
                if ((m.promoteTo != Piece.EMPTY) && (promoteTo == Piece.EMPTY)) {
                    promoteMove = m;
                    gui.requestPromotePiece();
                    return false;
                }
                if (m.promoteTo == promoteTo) {
                    String strMove = TextIO.moveToString(pos, m, false);
                    Log.w("debug", "within domove() strMove=" + strMove);
                    game.processString(strMove);
                    //verify game repetions
                    game.processString("draw rep");
                    return true;
                }
            }
        }
        InvalidMoveException invalidMove = new InvalidMoveException();
        invalidMove.putArg("move", move);
        throw invalidMove;
    }


    final private void updateGUI() {
    	IGUIInterface gui = (IGUIInterface) manager.getRequiredInterface("IGUIInterface");
        setStatusString();

        br.unicamp.ic.sed.engine.req.IHistory history = (IHistory) manager.getRequiredInterface("IHistory");
        if (history != null) {
        	//only if there is history
        	setMoveList();
        }
        setThinkingPV();
        gui.setPosition(game.pos);
    }

    /**
     * sets the strings on the UI <br>
     * "White's Move" <br>
     * "White's Move (thinking)" <br>
     * "Black's move" <br>
     * "Black's move (thinking)" <br>
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
    final private void setStatusString() {
    	IGUIInterface gui = (IGUIInterface) manager.getRequiredInterface("IGUIInterface");
        String str = game.pos.isWhiteTurn() ? "White's move" : "Black's move";
        if (computerThread != null) str += " (thinking)";

        String gameString = game.getGameStateString();
        if (!gameString.equals("")) {
        	//if game string is not empty, then put it on the screen
        	//otherwise there is nothing to report...
        	str = gameString;
        }
        gui.setStatusString(str);
    }

    public final void setMoveList() {
    	IGUIInterface gui = (IGUIInterface) manager.getRequiredInterface("IGUIInterface");
        String str = game.getMoveListString(true);
        gui.setMoveListString(str);
    }

    public final void setThinkingPV() {
    	IGUIInterface gui = (IGUIInterface) manager.getRequiredInterface("IGUIInterface");
        String str = "";
        if (gui.showThinking()) {
            str = thinkingPV;
        }
        gui.setThinkingString(str);
    }

    final private void setSelection() {
    	IGUIInterface gui = (IGUIInterface) manager.getRequiredInterface("IGUIInterface");
        Move m = game.getLastMove();
        int sq = (m != null) ? m.to : -1;
        gui.setSelection(sq);
    }


    public void startComputerThinking() {

        if (game.pos.isWhiteTurn() != humanIsWhite) {
            if (computerThread == null) {
                Runnable run = new Runnable() {
                    public void run() {
                    	IGUIInterface gui = (IGUIInterface) manager.getRequiredInterface("IGUIInterface");
                        computerPlayer.timeLimit(gui.timeLimit(), gui.timeLimit(), gui.randomMode());


                        final String cmd = computerPlayer.getCommand(new Position(game.pos),
                                game.haveDrawOffer(), game.getHistory());
                        gui.runOnUIThread(new Runnable() {
                            public void run() {
                                game.processString(cmd);
                                thinkingPV = "";
                                updateGUI();
                                setSelection();
                                stopComputerThinking();
                            }
                        });
                    }
                };
                if (threadStack > 0) {
                    ThreadGroup tg = new ThreadGroup("searcher");
                    computerThread = new Thread(tg, run, "searcher", threadStack);
                } else {
                    computerThread = new Thread(run);
                }
                thinkingPV = "";
                updateGUI();
                computerThread.start();
            }
        }
    }

    public synchronized void stopComputerThinking() {
        if (computerThread != null) {
            computerPlayer.timeLimit(0, 0, false);
            try {
                computerThread.join();
            } catch (InterruptedException ex) {
                System.out.printf("Could not stop thread%n");
            }
            computerThread = null;
            updateGUI();
        }
    }

    public synchronized void setTimeLimit() {
    	IGUIInterface gui = (IGUIInterface) manager.getRequiredInterface("IGUIInterface");
        if (computerThread != null) {
            computerPlayer.timeLimit(gui.timeLimit(), gui.timeLimit(), gui.randomMode());
        }
    }


	public boolean undoOneMove() {
		//undo and redo depends on the history
		IHistory history = (IHistory) this.manager.getRequiredInterface("IHistory");
		if (history != null) {
			if (game.getLastMove() != null) {
				game.processString("undo");
				updateGUI();
				setSelection();
				setHumanWhite(!humanIsWhite);
				return true;
			}
		}
		return false;
	}


	public boolean redoOneMove() {
		//depends on the history
		IHistory history = (IHistory) this.manager.getRequiredInterface("IHistory");
		if (history != null) {
			//if there is a movement to be redone than do it, false otherwise;
			if (game.currentMove < history.moveListSize()) {
				game.processString("redo");
				updateGUI();
				setSelection();
				humanIsWhite = !humanIsWhite; // have to force this manually;
				// take care that this cannot call setHumanWhite(!humanIsWhite);
				// otherwise it will act like a movement
				return true;
			}
		}
		return false;
	}


	public void startComputerMove(){
		setHumanWhite(!humanIsWhite);
	}


	public boolean savePGNToFile(String pgnStringFile, String filename) {
		IFormat format = (IFormat) this.manager.getRequiredInterface("IFormat");
    	if (format != null) {
    		return format.savePGNToFile(pgnStringFile, filename);
    	} else {
    		Log.w("debug", "Component FormatMgr is not present, its optional");
    	}
    	return false;
	}


	public void makeHumanHumanMove(Move m) throws InvalidMoveException{
	     doMove(m);
         updateGUI();
	     setSelection();
	     //have to force this manually, look at the UI it needs to be set too.
	     humanIsWhite = !humanIsWhite;
	}

	public void setRateThisAppParameters(Config config){
		RateThisApp.setConfig(config);
	}

	public void setRateThisAppOnStart(Context context){
		RateThisApp.onStart(context);
	}

	public void setRateThisAppShowRateDialogIfNeeded(Context context){
		RateThisApp.showRateDialogIfNeeded(context);
	}

	public void reportPromotePieceHumanHuman(int choice) {
		final boolean white = game.pos.isWhiteTurn();
        int promoteTo;
        switch (choice) {
            case 1:
                promoteTo = white ? Piece.WROOK : Piece.BROOK;
                break;
            case 2:
                promoteTo = white ? Piece.WBISHOP : Piece.BBISHOP;
                break;
            case 3:
                promoteTo = white ? Piece.WKNIGHT : Piece.BKNIGHT;
                break;
            default:
                promoteTo = white ? Piece.WQUEEN : Piece.BQUEEN;
                break;
        }
		promoteMove.promoteTo = promoteTo;
		Move m = promoteMove;
		promoteMove = null;

		try {
			// just update the board with the new status
			// do not change the variable humanIsWhite, otherwise game will be
			// in an inconsistent state;
			doMove(m);
			updateGUI();
			setSelection();
		} catch (InvalidMoveException e) {
			// this is not necessary since
			// it is a promotion movement, it will be valid
		}
	}
}
