package br.unicamp.ic.sed.engine.req;

import br.unicamp.ic.sed.global.datatypes.Move;
import br.unicamp.ic.sed.global.datatypes.Position;

public interface IBook {
	public Move getBookMove(Position pos);
}
