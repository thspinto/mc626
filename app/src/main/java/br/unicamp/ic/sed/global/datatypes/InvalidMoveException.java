package br.unicamp.ic.sed.global.datatypes;

import java.util.HashMap;

/**
 * Exception represents the invalid mode performed on the GUI
 * the args is essentially the Move that was performed.
 * the key is "Move" the Object returned shall be the Move done on the GUI
 * usage: Move m = (Move) InvalidMoveException.getArgs().get("Move");
 * */
public class InvalidMoveException extends Exception{

	private static final long serialVersionUID = 5728809563453521907L;
	private HashMap<String, Object> args = new HashMap<String, Object>();
	
	public InvalidMoveException() {};
	public InvalidMoveException(String msg) {
		super(msg);
	}
	
	public HashMap<String, Object> getArgs() {
		return this.args;
	}
	
	public void putArg(String key, Object value) {
		this.args.put(key, value);
	}
}
