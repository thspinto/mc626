package br.unicamp.ic.sed.engine.req;

import java.util.Calendar;
import java.util.List;

public interface IFormat {
	
	/**
	 * this function gets a PGN format String
	 * here is an example of PGN format String: <br>
	 * [Event "event"] <br>
	 * [Site "Prague"] <br>
	 * [Date "2009.10.1"] <br>
	 * [Round "1"] <br>
	 * [White "playerWhite"] <br>
	 * [Black "blackPlayer"] <br>
	 * [Result "1/2-1/2"] <br>
	 * [WhiteElo "2000"] <br>
	 * [BlackElo "2000"] <br>
	 * [PlyCount "2]
	 * @param eventName is optional parameter, indicates the eventName
	 * @param site is optional parameter, indicates the event site
	 * @param date is a required parameter
	 * @param round is a optional parameter
	 * @param nameWhitePlayer is a required parameter
	 * @param nameBlackPlayer is a required parameter
	 * @param humanIsWhite is required and indicates if the human should be flipped with the black or not
	 * @param gameResult is a required parameter
	 * @param whiteElo is an optional parameter, indicates the ranking of the white player
	 * @param blackElo is an optional parameter, indicates the ranking of the black player
	 * @param plyCount is an optional parameter.
	 * @param GameStateIsAlive is used to concatenate the * at the end of the moves
	 * */
	public String getPGNFormat(String eventName, String site, Calendar date, String round,
			String nameWhitePlayer, String nameBlackPlayer, boolean humanIsWhite,
			String gameResult,
			String whileElo, String blackElo,
			String plyCount,
			List<String> posHist, 
			String moves,
			boolean GameStateIsAlive);
	
	/**
	 * This function saves the PGN String to a given filename
	 * @param pgnStringFile is the String with the PGN format
	 * {@link #getPGNFormat(String, String, Calendar, String, String, String, 
	 * boolean, String, String, String, String, List, String, boolean) getPGNFormat}
	 * @param filename with path example: "/sdcard/filename.txt"
	 * */
	public boolean savePGNToFile(String pgnStringFile, String filename);
}
