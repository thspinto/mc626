package br.unicamp.ic.sed.engine_format_connector.impl;

import java.util.Calendar;
import java.util.List;

class Adapter implements br.unicamp.ic.sed.engine.req.IFormat {

	private IManager manager;

	public Adapter(br.unicamp.ic.sed.engine_format_connector.impl.IManager manager) {
		this.manager = manager;
	}

	public String getPGNFormat(String eventName, String site, Calendar date, String round,
			String nameWhitePlayer, String nameBlackPlayer, boolean humanIsWhite,
			String gameResult,
			String whiteElo, String blackElo,
			String plyCount,
			List<String> posHist,
			String moves,
			boolean GameStateIsAlive) {

		br.unicamp.ic.sed.formatmgr.prov.IFormat format = (br.unicamp.ic.sed.formatmgr.prov.IFormat) manager.getRequiredInterface("IFormat");
		return format.getPGNFormat(eventName, site, date, round,
				nameWhitePlayer, nameBlackPlayer, humanIsWhite, gameResult,
				whiteElo, blackElo, plyCount, posHist, moves, GameStateIsAlive);
	}

	public boolean savePGNToFile(String pgnStringFile, String filename) {
		br.unicamp.ic.sed.formatmgr.prov.IFormat format = (br.unicamp.ic.sed.formatmgr.prov.IFormat) manager.getRequiredInterface("IFormat");
		return format.savePGNToFile(pgnStringFile, filename, null);
	}

}
