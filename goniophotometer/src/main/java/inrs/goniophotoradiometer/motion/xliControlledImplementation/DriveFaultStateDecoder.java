package inrs.goniophotoradiometer.motion.xliControlledImplementation;

class DriveFaultStateDecoder extends BitsPaquetsStateDecoder{
		public void decodeState(String state_string,
				XliControlledMotionEngine motion_engine)
				throws StateParsingException {
			decodeState(state_string, motion_engine.getDriveFaultsTab());
		}	
}