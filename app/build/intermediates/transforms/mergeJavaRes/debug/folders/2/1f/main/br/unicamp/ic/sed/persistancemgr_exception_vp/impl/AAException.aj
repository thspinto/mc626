package br.unicamp.ic.sed.persistancemgr_exception_vp.impl;

import java.io.IOException;
import android.util.Log;
import br.unicamp.ic.sed.exception.prov.IException;

public abstract aspect AAException {
	private IManager mymanager = ComponentFactory.createInstance();

	public abstract pointcut pointcutLogException();

	after() throwing(IOException ex) : 
		pointcutLogException() {
		Log.w("debug", "aspect AAException at persistancemgr exception vp");
		IException exception = (IException) mymanager.getRequiredInterface("IException");
		exception.handleExceptionWithStackTrace(ex);
	}
}