package br.unicamp.ic.sed.global.datatypes;


/**
 * this exception is thrown when a missing optional component is executed.
 * */
public class OptionalComponentMissingException extends RuntimeException{
	public OptionalComponentMissingException(String msg) {
		super(msg);
	}
}
