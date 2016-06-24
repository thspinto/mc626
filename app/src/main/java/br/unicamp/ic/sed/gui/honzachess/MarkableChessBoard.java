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

package br.unicamp.ic.sed.gui.honzachess;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import br.unicamp.ic.sed.engine.prov.IEngine;
import br.unicamp.ic.sed.global.datatypes.InvalidMoveException;
import br.unicamp.ic.sed.global.datatypes.Move;
import br.unicamp.ic.sed.global.datatypes.Piece;
import br.unicamp.ic.sed.global.datatypes.Position;

public class MarkableChessBoard extends View{
    private Position pos;
    private int selectedSquare;
    private float cursorX, cursorY;
    private boolean cursorVisible;
    private int x0, y0, sqSize;
    private boolean flipped;

    private Paint darkPaint;
    private Paint brightPaint;
    private Paint redOutline;
    private Paint greenOutline;
    private Paint whitePiecePaint;
    private Paint blackPiecePaint;

    private SharedPreferences settings;

    //implementing MovableBoard depending on the IEngine specs
    private IEngine engineComponent;
    public void setEngineComponent(IEngine engineComponent) {
		this.engineComponent = engineComponent;
	}
    //end implementing MovableBoard depending on IEngine specs

	//under movement begin
    private MotionEvent motionEvent;
    private boolean underMovement = false;
    public boolean isUnderMovement() {
		return underMovement;
	}
	public void setUnderMovement(boolean underMovement) {
		this.underMovement = underMovement;
	}
    //under movement end

	public MarkableChessBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        pos = new Position();
        selectedSquare = -1;
        cursorX = cursorY = 0;
        cursorVisible = false;
        x0 = y0 = sqSize = 0;
        flipped = false;

        darkPaint = new Paint();
//        darkPaint.setARGB(255, 128, 128, 128); //color is cinza
        //darkPaint.setARGB(255, 172, 111, 50); //color is dark orange
        darkPaint.setARGB(255, 100, 100, 100); //color is cinza escuro honza

        brightPaint = new Paint();
//        brightPaint.setARGB(255, 190, 190, 90); //color is yellow
        //brightPaint.setARGB(255, 201, 189, 145); //color is light orange
        brightPaint.setARGB(255, 200, 200, 200); //color is light white hoza

        redOutline = new Paint();
        redOutline.setARGB(255, 0, 255, 0);
        redOutline.setStyle(Paint.Style.FILL);
        redOutline.setAntiAlias(true);

//        greenOutline = new Paint();
//        greenOutline.setARGB(255, 0, 255, 0);
//        greenOutline.setStyle(Paint.Style.STROKE);
//        greenOutline.setAntiAlias(true);

        whitePiecePaint = new Paint();
        whitePiecePaint.setARGB(255, 255, 255, 255);
        whitePiecePaint.setAntiAlias(true);

        blackPiecePaint = new Paint();
        blackPiecePaint.setARGB(255, 0, 0, 0);
        blackPiecePaint.setAntiAlias(true);
    }

    public void setFont(Typeface tf) {
        whitePiecePaint.setTypeface(tf);
        blackPiecePaint.setTypeface(tf);
        invalidate();
    }

    /**
     * Set the board to a given state.
     * @param pos
     */
    final public void setPosition(Position pos) {
        this.pos = pos;
        invalidate();
    }

    /**
     * Set/clear the board flipped status.
     * @param flipped
     */
    final public void setFlipped(boolean flipped) {
        this.flipped = flipped;
        invalidate();
    }

    /**
     * get if the board is flipped
     * the starting position is white below and black above.
     * if flipped will be black below and white above.
     * this function will be used to flip the board.
     * */
    final public boolean getFlipped() {
    	return this.flipped;
    }

    /**
     * Set/clear the selectedSquare variable.
     * @param square The square to select, or -1 to clear selection.
     */
    final public void setSelection(int square) {
        if (square != selectedSquare) {
            selectedSquare = square;
            invalidate();
        }
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int minSize = Math.min(width, height);
        setMeasuredDimension(minSize, minSize);
    }


    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
        final int width = getWidth();
        final int height = getHeight();
        sqSize = (Math.min(width, height) - 4) / 8;
        x0 = (width - sqSize * 8) / 2;
        y0 = (height - sqSize * 8) / 2;

        //first draw the table and then draw the pieces, otherwise some pieces will be under the table
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                final int xCrd = getXCrd(x);
                final int yCrd = getYCrd(y);

                Paint paint = Position.darkSquare(x, y) ? darkPaint : brightPaint;
                canvas.drawRect(xCrd, yCrd, xCrd+sqSize, yCrd+sqSize, paint);

            }
        }

      //puts the red selection on the movement
        if (selectedSquare >= 0) {
            int selX = Position.getX(selectedSquare);
            int selY = Position.getY(selectedSquare);
            redOutline.setStrokeWidth(sqSize/(float)16);
            int x0 = getXCrd(selX);
            int y0 = getYCrd(selY);
            canvas.drawRect(x0, y0, x0 + sqSize, y0 + sqSize, redOutline);
        }

        //draw the pieces
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
            	final int xCrd = getXCrd(x);
                final int yCrd = getYCrd(y);

                int sq = Position.getSquare(x, y);
                int p = pos.getPiece(sq);

                if (isUnderMovement() && selectedSquare == sq) {
//                	Log.w("debug", "under movement x" + motionEvent.getX() + "y" + motionEvent.getY());

                	drawPiece(canvas, (int) ( motionEvent.getX() ), (int) ( motionEvent.getY() - sqSize ), p);
                } else {
//                	Log.w("debug", "NOT in movement x" + xCrd + sqSize / 2 + "y" + yCrd + sqSize / 2);
                	drawPiece(canvas, xCrd + sqSize / 2, yCrd + sqSize / 2, p);
                }

            }
        }

    }

    private final void drawPiece(Canvas canvas, int xCrd, int yCrd, int p) {
    	 String ps;
         Bitmap currentBMP = null;
         //TODO: instead of readrawing everytime, better to have a hashmap of the pieces
         //and ask for it everytime...

         switch (p) {
             case Piece.EMPTY:
                 ps = "";
                 break;
             case Piece.WKING:
                 ps = "k";
                 Bitmap bwking = BitmapFactory.decodeResource(getResources(), chess.spl.R.drawable.bk);
                 currentBMP = bwking;
                 break;
             case Piece.WQUEEN:
                 ps = "q";
                 Bitmap bwqueen = BitmapFactory.decodeResource(getResources(), chess.spl.R.drawable.bd);
                 currentBMP = bwqueen;
                 break;
             case Piece.WROOK:
                 ps = "r";
                 Bitmap bwrook = BitmapFactory.decodeResource(getResources(), chess.spl.R.drawable.bv);
                 currentBMP = bwrook;
                 break;
             case Piece.WBISHOP:
                 ps = "b";
                 Bitmap bwbishop = BitmapFactory.decodeResource(getResources(), chess.spl.R.drawable.bs);
                 currentBMP = bwbishop;
                 break;
             case Piece.WKNIGHT:
                 ps = "n";
                 Bitmap bwknight = BitmapFactory.decodeResource(getResources(), chess.spl.R.drawable.bj);
                 currentBMP = bwknight;
                 break;
             case Piece.WPAWN:
                 ps = "p";
                 Bitmap bwpawn = BitmapFactory.decodeResource(getResources(), chess.spl.R.drawable.bp);
                 currentBMP = bwpawn;
                 break;
             case Piece.BKING:
                 ps = "l";
                 Bitmap bbking = BitmapFactory.decodeResource(getResources(), chess.spl.R.drawable.ck);
                 currentBMP = bbking;
                 break;
             case Piece.BQUEEN:
                 ps = "w";
                 Bitmap bbqueen = BitmapFactory.decodeResource(getResources(), chess.spl.R.drawable.cd);
                 currentBMP = bbqueen;
                 break;
             case Piece.BROOK:
                 ps = "t";
                 Bitmap bbrook = BitmapFactory.decodeResource(getResources(), chess.spl.R.drawable.cv);
                 currentBMP = bbrook;
                 break;
             case Piece.BBISHOP:
                 ps = "v";
                 Bitmap bbbishop = BitmapFactory.decodeResource(getResources(), chess.spl.R.drawable.cs);
                 currentBMP = bbbishop;
                 break;
             case Piece.BKNIGHT:
                 ps = "m";
                 Bitmap bbknight = BitmapFactory.decodeResource(getResources(), chess.spl.R.drawable.cj);
                 currentBMP = bbknight;
                 break;
             case Piece.BPAWN:
                 ps = "o";
                 Bitmap bbpawn = BitmapFactory.decodeResource(getResources(), chess.spl.R.drawable.cp);
                 currentBMP = bbpawn;
                 break;
             default:
                 ps = "?";
                 break;
         }
         if (ps.length() > 0) {
             Paint paint = Piece.isWhite(p) ? whitePiecePaint : blackPiecePaint;
             paint.setTextSize(sqSize);
             Rect bounds = new Rect();
             paint.getTextBounds(ps, 0, ps.length(), bounds);
             int xCent = bounds.centerX();
             int yCent = bounds.centerY();


             canvas.drawBitmap(currentBMP,
             			xCrd - xCent , yCrd + yCent , null);

//             } else {
//             	canvas.drawText(ps, xCrd - xCent, yCrd - yCent, paint);
//             }
         }
    }

    private final int getXCrd(int x) {
        return x0 + sqSize * (flipped ? 7 - x : x);
    }
    private final int getYCrd(int y) {
        return y0 + sqSize * (flipped ? y : (7 - y));
    }

    /**
     * Compute the square corresponding to the coordinates of a mouse event.
     * @param evt Details about the mouse event.
     * @return The square corresponding to the mouse event, or -1 if outside board.
     */
    final int eventToSquare(MotionEvent evt) {
        int xCrd = (int)(evt.getX());
        int yCrd = (int)(evt.getY());

        int sq = -1;
        if ((xCrd >= x0) && (yCrd >= y0) && (sqSize > 0)) {
            int x = (xCrd - x0) / sqSize;
            int y = 7 - (yCrd - y0) / sqSize;
            if ((x >= 0) && (x < 8) && (y >= 0) && (y < 8)) {
                if (flipped) {
                    x = 7 - x;
                    y = 7 - y;
                }
                sq = Position.getSquare(x, y);
            }
        }
        return sq;
    }

    final Move mousePressed(int sq) {
        if (sq < 0)
            return null;
        cursorVisible = false;
        if (selectedSquare >= 0) {
            int p = pos.getPiece(selectedSquare);
            if ((p == Piece.EMPTY) || (Piece.isWhite(p) != pos.isWhiteTurn())) {
                setSelection(-1); // Remove selection of opponents last moving piece
//                Log.w("debug", "debug selection 1");
            }
        }

        int p = pos.getPiece(sq);
        if (selectedSquare >= 0) {
            if (sq != selectedSquare) {
                if ((p == Piece.EMPTY) || (Piece.isWhite(p) != pos.isWhiteTurn())) {
                    Move m = new Move(selectedSquare, sq, Piece.EMPTY);
//                    Log.w("debug", "debug moves toward this position");
                    setSelection(sq);
                    return m;
                }
            }
            setSelection(-1);
        } else {
            boolean myColor = (p != Piece.EMPTY) && (Piece.isWhite(p) == pos.isWhiteTurn());
            if (myColor) {
//            	Log.w("debug", "debug selection 3");
                setSelection(sq);
            }
        }
        return null;
    }

    public static class OnTrackballListener {
        public void onTrackballEvent(MotionEvent event) { }
    }
    OnTrackballListener otbl = null;
    public void setOnTrackballListener(OnTrackballListener onTrackballListener) {
        otbl = onTrackballListener;
    }

    public boolean onTrackballEvent(MotionEvent event) {
        if (otbl != null) {
            otbl.onTrackballEvent(event);
            return true;
        }
        return false;
    }

//    public Move handleTrackballEvent(MotionEvent event) {
//        switch (event.getAction()) {
//        case MotionEvent.ACTION_DOWN:
//            invalidate();
//            if (cursorVisible) {
//                int x = Math.round(cursorX);
//                int y = Math.round(cursorY);
//                cursorX = x;
//                cursorY = y;
//                int sq = Position.getSquare(x, y);
//                return mousePressed(sq);
//            }
//            return null;
//        }
//        cursorVisible = true;
//        int c = flipped ? -1 : 1;
//        cursorX += c * event.getX();
//        cursorY -= c * event.getY();
//        if (cursorX < 0) cursorX = 0;
//        if (cursorX > 7) cursorX = 7;
//        if (cursorY < 0) cursorY = 0;
//        if (cursorY > 7) cursorY = 7;
//        invalidate();
//        return null;
//    }


    public boolean onTouchEvent(MotionEvent event) {
    	super.onTouchEvent(event);

		// read the preference from settings
		settings = getContext().getSharedPreferences("honzasettings",
				Context.MODE_PRIVATE);
		boolean humanHuman = settings.getBoolean("humanHuman", false);

    	//implements the ontouch events
    	 if (engineComponent.humansTurn()) {
				switch (event.getAction()) {

				case MotionEvent.ACTION_UP:
	                    int sq = eventToSquare(event);
	                    Move m = mousePressed(sq);
						try {
							if (m != null) {
								if (!humanHuman) {
									engineComponent.humanMove(m);
									setSelection(-1);
								} else {
									engineComponent.makeHumanHumanMove(m);
									MyHonzaChessActivity.playerWhite = !MyHonzaChessActivity.playerWhite;
									setSelection(-1);
								}
							}
						} catch (InvalidMoveException e) {
							// do nothing.
						}
						return false;

				default:
					break;
				}

				return true;
		}

		return false;
	}

}
