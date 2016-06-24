package br.unicamp.ic.sed.exception.prov;

import android.content.Context;
import br.unicamp.ic.sed.global.datatypes.InvalidMoveException;

public interface IException {

	/**
	 * handles the Exception e depending on the specific type
	 * if it is an exception related to the InvalidMove, it will call the GUI
	 * */
	public void handleInvalidMoveException(InvalidMoveException e);

	/**
	 * Logs the exception on the logcat and java stack trace
	 * */
	public void handleExceptionWithStackTrace(Exception e);

	/**
	 * Handles information on the temporary dialog
	 * @param cx is the context of the application
	 * @param e exception being handled to the Toast message
	 * */
	public void handleExceptionWithToast(Exception e, Context cx);
}
