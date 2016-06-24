package br.unicamp.ic.sed.formatmgr.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.util.Log;

class IFormatFacade implements br.unicamp.ic.sed.formatmgr.prov.IFormat{


	public String getPGNFormat(String eventName, String site, Calendar date, String round,
			String nameWhitePlayer, String nameBlackPlayer, boolean humanIsWhite,
			String gameResult,
			String whileElo, String blackElo,
			String plyCount,
			List<String> posHist,
			String moves,
			boolean GameStateIsAlive) {
		Log.w("debug", "function getPGNFormat");

		StringBuilder pgn = new StringBuilder();

        String fen = posHist.get(0);
//        String moves = game.getMoveListString(true);
//        if (game.getGameState() == GameState.ALIVE)
        if (GameStateIsAlive)
            moves += " *";

        //optional parameter Event
        if (eventName != null) {
        	pgn.append(String.format(Locale.US, "[Event \"%s\"]%n", eventName));
        }
        //optional parameter Site
        if (site != null) {
        	pgn.append(String.format(Locale.US, "[Site \"%s\"]%n", site));
        }

        int year, month, day;
        {
//            Calendar now = GregorianCalendar.getInstance();

        	Calendar now = date;
            year = now.get(Calendar.YEAR);
            month = now.get(Calendar.MONTH) + 1;
            day = now.get(Calendar.DAY_OF_MONTH);
        }
        pgn.append(String.format(Locale.US, "[Date \"%04d.%02d.%02d\"]%n", year, month, day));
        String white = nameWhitePlayer;
        String black = nameBlackPlayer;
        if (!humanIsWhite) { //reverse players if humanIs Black
            String tmp = white; white = black; black = tmp;
        }

        //optional parameter Round
        if (round != null) {
        	pgn.append(String.format(Locale.US, "[Round \"%s\"]%n", round));
        }

        pgn.append(String.format(Locale.US, "[White \"%s\"]%n", white));
        pgn.append(String.format(Locale.US, "[Black \"%s\"]%n", black));
        pgn.append(String.format(Locale.US, "[Result \"%s\"]%n", gameResult));


        //optional parameter whiteElo
        if (whileElo != null) {
        	pgn.append(String.format(Locale.US, "[WhiteElo \"%s\"]%n", whileElo));
        }
        //optional parameter whiteElo
        if (blackElo != null) {
        	pgn.append(String.format(Locale.US, "[BlackElo \"%s\"]%n", blackElo));
        }
        //optional parameter
        if (plyCount != null) {
        	pgn.append(String.format(Locale.US, "[PlyCount \"%s\"]%n", plyCount));
        }

//        if (!fen.equals(TextIO.startPosFEN)) {
//            pgn.append(String.format(Locale.US, "[FEN \"%s\"]%n", fen));
//            pgn.append("[SetUp \"1\"]\n");
//        }
        pgn.append("\n");
        String[] strArr = moves.split(" ");
        int currLineLength = 0;
        final int arrLen = strArr.length;
        for (int i = 0; i < arrLen; i++) {
            String move = strArr[i].trim();
            int moveLen = move.length();
            if (moveLen > 0) {
                if (currLineLength + 1 + moveLen >= 80) {
                    pgn.append("\n");
                    pgn.append(move);
                    currLineLength = moveLen;
                } else {
                    if (currLineLength > 0) {
                        pgn.append(" ");
                        currLineLength++;
                    }
                    pgn.append(move);
                    currLineLength += moveLen;
                }
            }
        }
        pgn.append("\n\n");
        return pgn.toString();
	}

	public boolean savePGNToFile(String pgnStringFile, String filename, FileOutputStream fop) {

		try {
//			File file = new File(filename);
//			FileOutputStream fop = new FileOutputStream(file, false);
//			//always overwrite
//			file.createNewFile();

			// get the content in bytes
			byte[] contentInBytes = pgnStringFile.getBytes();

			fop.write(contentInBytes);
//			fop.flush();
//			fop.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
//
		return true;
	}

}
