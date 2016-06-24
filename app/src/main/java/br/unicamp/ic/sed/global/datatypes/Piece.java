/*
    CuckooChess - A java chess program.
    Copyright (C) 2011  Peter Österlund, peterosterlund2@gmail.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package br.unicamp.ic.sed.global.datatypes;

/**
 * Constants for different piece types.
 * @author petero
 */
public class Piece {
	
    public static final int EMPTY = 0;

    public static final int WKING = 1;
    public static final int WQUEEN = 2;
    public static final int WROOK = 3;
    public static final int WBISHOP = 4;
    public static final int WKNIGHT = 5;
    public static final int WPAWN = 6;

    public static final int BKING = 7;
    public static final int BQUEEN = 8;
    public static final int BROOK = 9;
    public static final int BBISHOP = 10;
    public static final int BKNIGHT = 11;
    public static final int BPAWN = 12;

    public static final int nPieceTypes = 13;

    /**
     * Return true if p is a white piece, false otherwise.
     * Note that if p is EMPTY, an unspecified value is returned.
     */
    public static final boolean isWhite(int pType) {
        return pType < BKING;
    }
    public static final int makeWhite(int pType) {
        return pType < BKING ? pType : pType - (BKING - WKING);
    }
    public static final int makeBlack(int pType) {
        return ((pType > EMPTY) && (pType < BKING)) ? pType + (BKING - WKING) : pType;
    }
    
    
    //from class Evaluate
    public static final int pV =   92 + Parameters.instance().getIntPar("pV");
    public static final int nV =  385 + Parameters.instance().getIntPar("nV");
    public static final int bV =  385 + Parameters.instance().getIntPar("bV");
    public static final int rV =  593 + Parameters.instance().getIntPar("rV");
    public static final int qV = 1244 + Parameters.instance().getIntPar("qV");
    public static final int kV = 9900; // Used by SEE algorithm, but not included in board material sums

    public static final int[] pieceValue;
    static {
        // Initialize material table
        pieceValue = new int[Piece.nPieceTypes];
        pieceValue[Piece.WKING  ] = kV;
        pieceValue[Piece.WQUEEN ] = qV;
        pieceValue[Piece.WROOK  ] = rV;
        pieceValue[Piece.WBISHOP] = bV;
        pieceValue[Piece.WKNIGHT] = nV;
        pieceValue[Piece.WPAWN  ] = pV;
        pieceValue[Piece.BKING  ] = kV;
        pieceValue[Piece.BQUEEN ] = qV;
        pieceValue[Piece.BROOK  ] = rV;
        pieceValue[Piece.BBISHOP] = bV;
        pieceValue[Piece.BKNIGHT] = nV;
        pieceValue[Piece.BPAWN  ] = pV;
        pieceValue[Piece.EMPTY  ] =  0;
    }
}
