package inrs.goniophotoradiometer.motion.xliControlledImplementation;

import c4sci.io.serial.SerialDevice;
import c4sci.io.serial.SerialStateParsingException;

class DriveFaultStateDecoder extends BitsPaquetsStateDecoder{
	public void decodeState(String state_string,
			SerialDevice motion_engine)
					throws SerialStateParsingException {
		try{
			decodeState(state_string, ((XliControlledMotionEngine) motion_engine).getDriveFaultsTab());
		}
		catch(ClassCastException _e){
			throw new SerialStateParsingException("the serial device is not a Xli motion engine!", _e);
		}
	}	
}
