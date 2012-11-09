package inrs.goniophotometer.motion.XliControlled;

interface StateDecoder {
	void decodeState(String state_string, XliControlledMotionEngine motion_engine) throws StateParsingException;
}

