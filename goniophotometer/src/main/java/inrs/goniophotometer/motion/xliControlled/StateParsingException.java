package inrs.goniophotometer.motion.xliControlled;

class StateParsingException extends Exception{
	private static final long serialVersionUID = 4195974085525492781L;
	public StateParsingException(String err_msg){
		super(err_msg);
	}
	public StateParsingException(String err_msg, Throwable ancestor_exception){
		super(err_msg, ancestor_exception);
	}
};
