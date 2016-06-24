package br.unicamp.ic.sed.bookmgr_persistance_vp.impl;

import java.io.InputStream;

public aspect BookBinder extends AAPersistance {
	public pointcut pointcutinitBook(InputStream in):
		br.unicamp.ic.sed.bookmgr.aspects.XPIBook.pinitBook(in);
}