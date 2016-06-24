package br.unicamp.ic.sed.historymgr.impl;

import java.util.ArrayList;
import java.util.List;

import br.unicamp.ic.sed.global.datatypes.Move;
import br.unicamp.ic.sed.global.datatypes.UndoInfo;
import br.unicamp.ic.sed.historymgr.prov.IHistory;

class IHistoryFacade implements IHistory{
	private List<Move> moveList = null; //this keeps track of the history
    private List<UndoInfo> uiInfoList = null; //this keeps track of the history

	public void addToHistory(Move m, UndoInfo ui) {
		moveList.add(m);
		uiInfoList.add(ui);
	}

	public void removeFromHistory(int currentMove) {
		moveList.remove(currentMove);
		uiInfoList.remove(currentMove);
	}

	public Move moveListGet(int index) {
		if (moveList.size() > 0) {
			return moveList.get(index);
		}
		return new Move(0,0,0);
	}

	public UndoInfo uiinfoListGet(int index) {

		if(uiInfoList.size() > 0){
			return uiInfoList.get(index);
		}
		return new UndoInfo();
	}

	public int moveListSize() {
		return moveList.size();
	}

	public Move getLastMove() {
		return null;
	}

	public void resetHistory() {
		moveList = new ArrayList<Move>();
		uiInfoList = new ArrayList<UndoInfo>();
	}

}
