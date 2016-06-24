package br.unicamp.ic.sed.persistancemgr_exception_vp.impl;

public aspect PersistanceBinder extends AAException{
	public pointcut pointcutLogException() :
		br.unicamp.ic.sed.persistancemgr.aspects.XPIPersistance.pcreateFileOutputStream() ||
		br.unicamp.ic.sed.persistancemgr.aspects.XPIPersistance.pcloseFileOutputStream() ||
		br.unicamp.ic.sed.persistancemgr.aspects.XPIPersistance.pcloseFileInputStream() ||
		br.unicamp.ic.sed.persistancemgr.aspects.XPIPersistance.pcreateInputStream() ||
		br.unicamp.ic.sed.persistancemgr.aspects.XPIPersistance.pgetResourceAsStream();
}