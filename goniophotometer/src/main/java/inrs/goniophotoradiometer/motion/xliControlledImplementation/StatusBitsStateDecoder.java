package inrs.goniophotoradiometer.motion.xliControlledImplementation;

import c4sci.io.serial.SerialDevice;
import c4sci.io.serial.SerialStateParsingException;

class StatusBitsStateDecoder extends BitsPaquetsStateDecoder{
	public void decodeState(String state_string,
			SerialDevice motion_engine)
					throws SerialStateParsingException {
		try{
			decodeState(state_string, ((XliControlledMotionEngine)motion_engine).getStatusBitsTab());
		}
		catch(ClassCastException _e){
			throw new SerialStateParsingException("The serial device is not a Xli motion engine!", _e);
		}
	}
}
