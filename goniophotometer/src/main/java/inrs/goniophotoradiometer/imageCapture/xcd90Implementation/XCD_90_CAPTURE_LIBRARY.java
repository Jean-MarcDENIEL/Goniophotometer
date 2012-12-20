package inrs.goniophotoradiometer.imageCapture.xcd90Implementation;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;

public interface XCD_90_CAPTURE_LIBRARY extends Library {

	XCD_90_CAPTURE_LIBRARY INSTANCE = (XCD_90_CAPTURE_LIBRARY) Native.loadLibrary("libXCD-90-Capture.dll", XCD_90_CAPTURE_LIBRARY.class);
	
	boolean ImageRequest( NativeLong hCamera, short Len );
	boolean ImageCompleteWaitTimeOutFromPreceedingRequest( NativeLong hCamera, byte[] pBuf, short Time, short Len );
	boolean getImage(byte[] pBuff, short Len);
}
