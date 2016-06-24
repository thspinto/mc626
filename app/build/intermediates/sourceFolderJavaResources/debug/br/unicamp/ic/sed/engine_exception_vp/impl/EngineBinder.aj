package br.unicamp.ic.sed.engine_exception_vp.impl;

import android.content.Context;

public aspect EngineBinder extends AAExceptionImpl{
	
	public pointcut handleExceptionInvalidMoveExceptionOnGUI() : 
		br.unicamp.ic.sed.engine.aspects.XPIEngine.phumanMove() ||
		br.unicamp.ic.sed.engine.aspects.XPIEngine.pMakeHumanHumanMove();
	
	public pointcut handleExceptionWithToast(String string, Context context):
		br.unicamp.ic.sed.engine.aspects.XPIEngine.psetFENOrPGN(string, context);

}
