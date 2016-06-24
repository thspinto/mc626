package br.unicamp.ic.sed.engine_book_connector.impl;

import br.unicamp.ic.sed.global.datatypes.Move;
import br.unicamp.ic.sed.global.datatypes.Position;

class Adapter implements br.unicamp.ic.sed.engine.req.IBook{
	private IManager manager;
	
	public Adapter(br.unicamp.ic.sed.engine_book_connector.impl.IManager manager) {
		this.manager = manager;
	}

	public Move getBookMove(Position pos) {
		br.unicamp.ic.sed.bookmgr.prov.IBook book = (br.unicamp.ic.sed.bookmgr.prov.IBook) manager.getRequiredInterface("IBook");
		return book.getBookMove(pos);
	}	
}
