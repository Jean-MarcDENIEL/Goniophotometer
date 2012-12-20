package inrs.goniophotoradiometer.motion.xliControlled;
/**
 * Decoder of a value string returned by a controller.<br>
 * @author jeanmarc.deniel
 *
 */
interface StateDecoder {
	/**
	 * Decodes a string returned by a controller.<br>
	 * The string begins by '*' and end with '\n'.
	 * @param state_string The return value to decode
	 * @param motion_engine The motion engine data to update
	 * @throws StateParsingException
	 */
	void decodeState(String state_string, XliControlledMotionEngine motion_engine) throws StateParsingException;
}

