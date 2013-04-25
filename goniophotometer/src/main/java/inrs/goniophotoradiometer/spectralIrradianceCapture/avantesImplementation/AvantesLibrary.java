package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface AvantesLibrary extends Library {
	AvantesLibrary INSTANCE = (AvantesLibrary) Native.loadLibrary("as5216", AvantesLibrary.class);
	/**
	 * @param a_port <ul>
	 * 	<li>-1 : use of auto-detect of USB or COM port</li>
	 * 	<li>0 : use of USB port</li>
	 * 	<li>1 : use COM1 port</li>
	 * 	<li>2 : use of COM2 port, and so on ...</li>
	 * </ul> 
	 * @return <ul>
	 * 	<li>on success : number of connected device</li>
	 * 	<li>on error : ERR_DEVICE_NOT_FOUND</li>
	 * </ul>
	 */
	int AVS_Init(short a_port);
	int AVS_Done();
	int AVS_GetNrOfDevices();
	
}
