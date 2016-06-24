package br.unicamp.ic.sed.formatmgr.prov;

import java.io.FileOutputStream;
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
	 * [PlyCount "2] <br>
	 *  1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 {This opening is called the Ruy Lopez.}
4. Ba4 Nf6 5. O-O Be7 6. Re1 b5 7. Bb3 d6 8. c3 O-O 9. h3 Nb8  10. d4 Nbd7
11. c4 c6 12. cxb5 axb5 13. Nc3 Bb7 14. Bg5 b4 15. Nb1 h6 16. Bh4 c5 17. dxe5
Nxe4 18. Bxe7 Qxe7 19. exd6 Qf6 20. Nbd2 Nxd6 21. Nc4 Nxc4 22. Bxc4 Nb6
23. Ne5 Rae8 24. Bxf7+ Rxf7 25. Nxf7 Rxe1+ 26. Qxe1 Kxf7 27. Qe3 Qg5 28. Qxg5
hxg5 29. b3 Ke6 30. a3 Kd6 31. axb4 cxb4 32. Ra5 Nd5 33. f3 Bc8 34. Kf2 Bf5
35. Ra7 g6 36. Ra6+ Kc5 37. Ke1 Nf4 38. g3 Nxh3 39. Kd2 Kb5 40. Rd6 Kc5 41. Ra6
Nf2 42. g4 Bd3 43. Re6 1/2-1/2 <br>
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
	public String getPGNFormat(
			String eventName, String site, Calendar date, String round,
			String nameWhitePlayer, String nameBlackPlayer, boolean humanIsWhite,
			String gameResult,
			String whiteElo, String blackElo,
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
	public boolean savePGNToFile(String pgnStringFile, String filename, FileOutputStream fop);
}
