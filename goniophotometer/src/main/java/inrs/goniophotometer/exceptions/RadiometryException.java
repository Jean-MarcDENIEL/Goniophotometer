package inrs.goniophotometer.exceptions;

public class RadiometryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7615283671252868276L;

	public RadiometryException(String msg_, Throwable cause_){
		super(msg_, cause_);
	}
	public RadiometryException(String msg_){
		super(msg_);
	}
}
