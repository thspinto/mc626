package br.unicamp.ic.sed.engine_history_vp.impl;

import android.util.Log;
import br.unicamp.ic.sed.global.datatypes.Move;
import br.unicamp.ic.sed.global.datatypes.UndoInfo;
import br.unicamp.ic.sed.historymgr.prov.IHistory;

public abstract aspect AAHistoryImpl {
	private IManager mymanager = ComponentFactory.createInstance();

	public abstract pointcut longInfoIntoHistory(Move m, UndoInfo ui);

	after(Move m, UndoInfo ui) : longInfoIntoHistory(m, ui) {
		if (m != null) {
			Log.w("debug", "after successful move, add to the history move=" + m);
			IHistory history = (IHistory) mymanager
					.getRequiredInterface("IHistory");
			history.addToHistory(m, ui);
		}
	}
}