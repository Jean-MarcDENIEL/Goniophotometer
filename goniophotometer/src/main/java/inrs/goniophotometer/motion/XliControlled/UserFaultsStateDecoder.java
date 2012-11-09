package inrs.goniophotometer.motion.XliControlled;

class UserFaultsStateDecoder extends BitsPaquetsStateDecoder{
	public void decodeState(String state_string,
			XliControlledMotionEngine motion_engine)
					throws StateParsingException {
		decodeState(state_string, motion_engine.getUserFaultsTab());
	}
}
