package br.unicamp.ic.sed.global.utils;

import br.unicamp.ic.sed.global.datatypes.Position;

public class Utils {

	/**
     * Convert a square number to a string, such as "e4".
     * this is used on the gui interface
     */
    public static final String squareToString(int square) {
        StringBuilder ret = new StringBuilder();
        int x = Position.getX(square);
        int y = Position.getY(square);
        ret.append((char) (x + 'a'));
        ret.append((char) (y + '1'));
        return ret.toString();
    }
}
