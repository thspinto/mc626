package br.unicamp.ic.sed.engine_exception_vp.impl;

import android.content.Context;
import android.util.Log;
import br.unicamp.ic.sed.exception.prov.IException;
import br.unicamp.ic.sed.global.datatypes.InvalidMoveException;

public abstract aspect AAExceptionImpl {
	
	private IManager mymanager = ComponentFactory.createInstance();
	
	public abstract pointcut handleExceptionInvalidMoveExceptionOnGUI();
	void around() : handleExceptionInvalidMoveExceptionOnGUI() {
		Log.w("debug", "delegation to exceptional component");
		try {
			proceed();
		} catch ( InvalidMoveException e ) {
			//delegate this to the exception component
			//Get the IExceptionHandler here and send the exception
			IException iException = (IException) mymanager.getRequiredInterface("IException");
			iException.handleInvalidMoveException(e);
		}
	}
	
	public abstract pointcut handleExceptionWithToast(String string, Context context);
	void around(String string, Context context) : handleExceptionWithToast(string, context) {
		Log.w("debug", "delegation with exceptionWithToast to the exceptional component");
		try {
			proceed(string, context);
		} catch ( Exception e ) {
			//delegate this to the exception component
			//Get the IExceptionHandler here and send the exception
			IException iException = (IException) mymanager.getRequiredInterface("IException");
			iException.handleExceptionWithToast(e, context);
		}
	}
	
}
