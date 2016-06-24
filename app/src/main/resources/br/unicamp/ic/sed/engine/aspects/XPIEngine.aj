package br.unicamp.ic.sed.engine.aspects;

import android.content.Context;
import br.unicamp.ic.sed.global.datatypes.InvalidMoveException;
import br.unicamp.ic.sed.global.datatypes.Move;
import br.unicamp.ic.sed.global.datatypes.UndoInfo;

public aspect XPIEngine {
	
	public pointcut phumanMove(): 
		execution (void br.unicamp.ic.sed.engine.prov.IEngine.humanMove(Move));
	
	public pointcut pMakeHumanHumanMove(): 
		execution (void br.unicamp.ic.sed.engine.prov.IEngine.makeHumanHumanMove(Move));
	
	public pointcut pAddToHistory(Move m, UndoInfo ui):
		execution(void br.unicamp.ic.sed.engine.impl.Game.addToHistory(Move, UndoInfo))
		&& args(m, ui);
	
	declare soft : InvalidMoveException : phumanMove() ;
	declare soft : InvalidMoveException : pMakeHumanHumanMove();
	
	
	public pointcut psetFENOrPGN(String s, Context c):
		execution (void br.unicamp.ic.sed.engine.prov.IEngine.setFENOrPGN(String, Context)) 
				&& args(s, c);
}
