package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.nio.ByteBuffer;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;

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
	int AVS_GetList(int	a_ListSize, IntByReference a_pRequiredSize, AvsIdentityType a_pList[]);
	int AVS_GetList(int	a_ListSize, IntByReference a_pRequiredSize, byte[] a_pList);
	int AVS_GetList(int	a_ListSize, IntByReference a_pRequiredSize, ByteBuffer a_pList);
	int AVS_Activate(AvsIdentityType a_pDeviceId);
	int AVS_Activate(byte[] a_pDeviceId);
	int AVS_Activate(ByteBuffer a_pDeviceId);
	int AVS_Deactivate(long a_hDeviceId);
	byte AVS_Register(int a_hWnd);
	int AVS_PrepareMeasure(long a_hDevice, MeasConfigType a_pMeasConfig);
	int AVS_Measure(long a_hDevice, int a_hWnd, short a_Nmsr);
	int AVS_GetLambda(long a_hDevice, double[] a_pWavelength);
	int AVS_GetNumPixels(long a_hDevice, ShortByReference a_pNumPixels);
	int AVS_GetParameter(long a_hDevice, int a_Size, IntByReference a_pRequiredSize, DeviceConfigType a_pData);
	int AVS_GetVersionInfo(long a_hDevice, byte[] a_pFPGAVersion, byte[] a_pFirmwareVersion, byte[] a_pDLLVersion);
	
	public static final int 	DEFAULT_HWINDOW	= 1;
	
}
