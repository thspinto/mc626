package br.unicamp.ic.sed.engine.req;

import br.unicamp.ic.sed.global.datatypes.Move;
import br.unicamp.ic.sed.global.datatypes.UndoInfo;

public interface IHistory {

	/** stores the movement and the ui info into the historyManager
	 * @param m current move being done
	 * @param ui is the undoinfo
	 * */
	void addToHistory(Move m, UndoInfo ui);


	void removeFromHistory(int currentMove);

	/**gets the Move stored in the moveList array
	 * @param index is the number inside the moveList
	 * @return the Move that is stored in the history
	 * */
	Move moveListGet(int index);

	/**gets the undoinfo stored in the undoinfo array
	 * @param index is the number inside the moveList
	 * @return the UndoInfo that is stored in the history
	 * */
	UndoInfo uiinfoListGet(int index);

	/**
	 * gets the moveList.size() function
	 * @return returns the size of the internal MoveList
	 * */
	int moveListSize();

	/**
	 * resets the history
	 * */
	void resetHistory();
}
