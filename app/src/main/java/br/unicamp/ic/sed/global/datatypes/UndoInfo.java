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
 * Contains enough information to undo a previous move.
 * Set by makeMove(). Used by unMakeMove().
 * @author petero
 */
public class UndoInfo {
    private int capturedPiece;
    private int castleMask;
    private int epSquare;
    private int halfMoveClock;
    
	public int getCapturedPiece() {
		return capturedPiece;
	}
	public void setCapturedPiece(int capturedPiece) {
		this.capturedPiece = capturedPiece;
	}
	public int getCastleMask() {
		return castleMask;
	}
	public void setCastleMask(int castleMask) {
		this.castleMask = castleMask;
	}
	public int getEpSquare() {
		return epSquare;
	}
	public void setEpSquare(int epSquare) {
		this.epSquare = epSquare;
	}
	public int getHalfMoveClock() {
		return halfMoveClock;
	}
	public void setHalfMoveClock(int halfMoveClock) {
		this.halfMoveClock = halfMoveClock;
	}
    
}
