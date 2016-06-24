package br.unicamp.ic.sed.engine_history_vp.impl;

import br.unicamp.ic.sed.global.datatypes.Move;
import br.unicamp.ic.sed.global.datatypes.UndoInfo;

public aspect EngineBinder extends AAHistoryImpl {
	public pointcut longInfoIntoHistory(Move m, UndoInfo ui) : 
		br.unicamp.ic.sed.engine.aspects.XPIEngine.pAddToHistory(m, ui);
}