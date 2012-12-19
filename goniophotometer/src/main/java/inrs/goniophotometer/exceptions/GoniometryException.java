package inrs.goniophotometer.exceptions;

public class GoniometryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6711577645778481697L;
	
	public GoniometryException(String msg_, Throwable cause_){
		super(msg_, cause_);
	}
	public GoniometryException(String msg_){
		super(msg_);
	}
}
