package br.unicamp.ic.sed.bookmgr.aspects;

import java.io.InputStream;

public aspect XPIBook {
	public pointcut pinitBook(InputStream in): 
		execution (void br.unicamp.ic.sed.bookmgr.impl.IBookFacade.initBook(InputStream))
		&& args(in);
}