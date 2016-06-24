package br.unicamp.ic.sed.bookmgr.prov;

import br.unicamp.ic.sed.global.datatypes.Move;
import br.unicamp.ic.sed.global.datatypes.Position;

public interface IBook {
	public Move getBookMove(Position pos);
}
