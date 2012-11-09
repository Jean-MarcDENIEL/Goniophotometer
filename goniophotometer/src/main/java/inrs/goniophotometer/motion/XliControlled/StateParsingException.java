package inrs.goniophotometer.motion.XliControlled;

class StateParsingException extends Exception{
	private static final long serialVersionUID = 4195974085525492781L;
	public StateParsingException(String err_msg){
		super(err_msg);
	}
};
