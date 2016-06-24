package br.unicamp.ic.sed.engine.prov;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import br.unicamp.ic.sed.global.datatypes.ChessParseError;
import br.unicamp.ic.sed.global.datatypes.Config;
import br.unicamp.ic.sed.global.datatypes.InvalidMoveException;
import br.unicamp.ic.sed.global.datatypes.Move;


public interface IEngine {

	public void setTimeLimit();

	/**
	 * returns the default PGN of the Game
	 * @return the string with the PGN format
	 * */
	public String getPGN();

	/** returns the PGN format according to the given parameters
	 * @return the PGN string according to the given parameters.
	 * */
	public String getPGN(String eventName, String site, Calendar date, String round,
			String nameWhitePlayer, String nameBlackPlayer,
			String gameResult,
			String whileElo, String blackElo);

	/**
	 * Saves the pgnStringFile to the filename.
	 * This function will delegate the FormatMgr component
	 * @param pgnStringFile is the String that represents the PGN.
	 * @param filename is the path where the file will be saved.
	 * @author gmw
	 * */
	public boolean savePGNToFile(String pgnStringFile, String filename);

	public String getFEN();
	public void newGame(boolean humanIsWhite, int ttLogSize, boolean verbose);
	public void startGame();
	public void setPGN(String pgn) throws ChessParseError;

	/**
	 * this function undos the movement of the computer and player
	 * which means it takes two moves back
	 * */
	public void undoMove();

	/**
	 * this function redos the movement of the player and the computer.
	 * */
	public void redoMove();
	public void setHumanWhite(final boolean humanIsWhite);
	public boolean humansTurn();
	public boolean computerThinking();
	public void humanMove(Move m) throws InvalidMoveException;

	/**
	 * internally calls the {@link #humanMove(Move)}
	 * which is designed to play when on not human vs computer or vice versa
	 * when on adding the feature to play with human-human, it should not call the
	 * */
	public void reportPromotePiece(int choice);

	/**
	 * this function was added to handle when the promotion is done manually we
	 * should not call the computer thinking when the user is playing in
	 * human-human mode
	 * @author gmw
	 * */
	public void reportPromotePieceHumanHuman(int choice);

	public void stopComputerThinking();
	public void setPosHistory(List<String> posHistStr);
	public void setThreadStackSize(int size);
	public List<String> getPosHistory();

	/**
	 * used by GUI to set the positions from the clipboard manager
	 * @param cx is the getApplicationContext() from the Activity or Service
	 * will be used to render the Toast in case of ChessParseError
	 * @param fenPgn is the String in the PGN or FEN format
	 * @throws ChessParseError is the error, it will be handled by the aspect
	 * AAExceptionImpl.aj handleExceptionWithToast
	 * */
	public void setFENOrPGN(String fenPgn, Context cx) throws ChessParseError;


	/**
	 * this function is required by honza chess to undo one movement when the
	 * user performs undo, then it assumes the role of the other player example:
	 * player is white, computer is black, white starts, black does a move, when
	 * a undo is performed, the player is now black.
	 *
	 * @author gmw
	 * @return true if the movement was undone, false otherwise, this is
	 *         necessary for honza interface
	 * */
	public boolean undoOneMove();

	/**
	 * this function is required by honza chess to redo one movement when the
	 * user performs undo, then it assumes the role of the other player example:
	 * player is white, computer is black, white starts, black does a move, when
	 * a undo is performed, the player is now black.
	 *
	 * @author gmw
	 * @return true if the redo was done, false otherwise, this is necessary for
	 *         honza interface
	 * */
	public boolean redoOneMove();

	/** this function starts the computer thinking process
	 * @author gmw
	 * */
	public void startComputerMove();

	/** this function is used when a player selects
	 *  "human-human option", and will play with another human player
	 * @author gmw
	 * */
	public void makeHumanHumanMove(Move m) throws InvalidMoveException;

	/**
	 * this function sets the configuration for the Engine on
	 * the intervals in minutes and the number of trials that the
	 * application will show the dialog to rate the application
	 * */
	public void setRateThisAppParameters(Config config);

	/**
	 * sets the RateThisApp Context to keep track of the global activity Context
	 * should call at onStartActivity
	 * */
	public void setRateThisAppOnStart(Context context);

	/**
	 * sets the RateThisApp Context to show if needed the dialog
	 * should call at onStartActivity
	 * */
	public void setRateThisAppShowRateDialogIfNeeded(Context context);
}
