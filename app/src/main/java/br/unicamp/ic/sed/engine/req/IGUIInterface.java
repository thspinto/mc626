package br.unicamp.ic.sed.engine.req;

import br.unicamp.ic.sed.global.datatypes.Move;
import br.unicamp.ic.sed.global.datatypes.Position;

public interface IGUIInterface {
	/** Update the displayed board position. */
    public void setPosition(Position pos);

    /** Mark square i as selected. Set to -1 to clear selection. */
    public void setSelection(int sq);

    /** Set the status text. */
    public void setStatusString(String str);

    /** Update the list of moves. */
    public void setMoveListString(String str);

    /** Update the computer thinking information. */
    public void setThinkingString(String str);

    /** Get the current time limit. */
    public int timeLimit();

    /** Get "random move" setting. */
    public boolean randomMode();

    /** Return true if "show thinking" is enabled. */
    public boolean showThinking();

    /** Ask what to promote a pawn to. Should call reportPromotePiece() when done. */
    public void requestPromotePiece();

    /** Run code on the GUI thread. */
    public void runOnUIThread(Runnable runnable);

    /** Report that user attempted to make an invalid move.
     *  this function is called when there is a invalid movement on the engine
     * */
    public void reportInvalidMove(Move m);
}
