package br.unicamp.ic.sed.exception.impl;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import br.unicamp.ic.sed.exception.prov.IException;
import br.unicamp.ic.sed.exception.req.IGUIInterface;
import br.unicamp.ic.sed.global.datatypes.InvalidMoveException;
import br.unicamp.ic.sed.global.datatypes.Move;

public class IExceptionFacade implements IException{

	private Manager mgr;

	public IExceptionFacade(Manager mgr) {
		this.mgr = mgr;
	}

	public void handleInvalidMoveException(InvalidMoveException e) {
		// shows invalid message on the screen
		// get the move and push it back to the interface
		HashMap<String, Object> args = e.getArgs();
		Move m = (Move) args.get("move");
		// It will call the method on the Activity that implements the
		// GUIInterface
		IGUIInterface gui = (IGUIInterface) this.mgr
				.getRequiredInterface("IGUIInterface");
		gui.reportInvalidMove(m);
	}

	public void handleExceptionWithStackTrace(Exception e) {
		Log.w("ChessSPL", "handleExceptionWithStackTrace, message e=" + e.getMessage());
		e.printStackTrace();
	}

	public void handleExceptionWithToast(Exception e, Context cx){
		Toast.makeText(cx.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
	}

}
