package br.unicamp.ic.sed.engine_history_connector.impl;

import br.unicamp.ic.sed.global.datatypes.Move;
import br.unicamp.ic.sed.global.datatypes.UndoInfo;

public class Adapter implements br.unicamp.ic.sed.engine.req.IHistory{

	private IManager manager;

	public Adapter(br.unicamp.ic.sed.engine_history_connector.impl.IManager manager) {
		this.manager = manager;
	}

	public void addToHistory(Move m, UndoInfo ui) {
		br.unicamp.ic.sed.historymgr.prov.IHistory history = (br.unicamp.ic.sed.historymgr.prov.IHistory) manager.getRequiredInterface("IHistory");
		history.addToHistory(m, ui);
	}

	public void removeFromHistory(int currentMove) {
		br.unicamp.ic.sed.historymgr.prov.IHistory history = (br.unicamp.ic.sed.historymgr.prov.IHistory) manager.getRequiredInterface("IHistory");
		history.removeFromHistory(currentMove);
	}

	public Move moveListGet(int index) {
		br.unicamp.ic.sed.historymgr.prov.IHistory history = (br.unicamp.ic.sed.historymgr.prov.IHistory) manager.getRequiredInterface("IHistory");
		return history.moveListGet(index);
	}

	public int moveListSize() {
		br.unicamp.ic.sed.historymgr.prov.IHistory history = (br.unicamp.ic.sed.historymgr.prov.IHistory) manager.getRequiredInterface("IHistory");
		return history.moveListSize();
	}

	public void resetHistory() {
		br.unicamp.ic.sed.historymgr.prov.IHistory history = (br.unicamp.ic.sed.historymgr.prov.IHistory) manager.getRequiredInterface("IHistory");
		history.resetHistory();
	}

	public UndoInfo uiinfoListGet(int index) {
		br.unicamp.ic.sed.historymgr.prov.IHistory history = (br.unicamp.ic.sed.historymgr.prov.IHistory) manager.getRequiredInterface("IHistory");
		return history.uiinfoListGet(index);
	}

}
