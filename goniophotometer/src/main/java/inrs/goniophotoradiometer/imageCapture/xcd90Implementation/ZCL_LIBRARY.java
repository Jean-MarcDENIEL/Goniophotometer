package inrs.goniophotoradiometer.imageCapture.xcd90Implementation;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.ShortByReference;

public interface ZCL_LIBRARY extends Library {
	ZCL_LIBRARY INSTANCE = (ZCL_LIBRARY) Native.loadLibrary("ZCL", ZCL_LIBRARY.class);
	
	// only for V 2.10.4 version
	//void	ZCLSetStructVersion(short Version);
	
	boolean ZCLGetList(ZCL_LIST List);
	boolean ZCLCameraInfo(NativeLong hCamera, ZCL_CAMERAINFO pInfo, IntByReference pSpeed );
	boolean ZCLOpen(long UID, NativeLongByReference hCamera);
	boolean ZCLClose(NativeLong hCamera);
	boolean ZCLCheckCameraMode(NativeLong hCamera, ZCL_CAMERAMODE Mode);
	boolean ZCLSetCameraMode(NativeLong hCamera, ZCL_CAMERAMODE Mode);
	boolean ZCLNowCameraMode(NativeLong hCamera, ZCL_CAMERAMODE Mode);
	int 	ZCLGetLibraryRevision();
	boolean ZCLSetFeatureValue(NativeLong hCamera, ZCL_SETFEATUREVALUE Value);
	boolean ZCLGetImageInfo(NativeLong hCamera, ZCL_GETIMAGEINFO Info);
	boolean ZCLIsoAlloc(NativeLong hCamera);
	boolean ZCLIsoRelease(NativeLong  hCamera );
	boolean ZCLIsoStart(NativeLong hCamera, short Frame);
	boolean ZCLIsoStop(NativeLong hCamera);
	
	boolean ZCLImageReq(NativeLong hCamera, Memory pBuf, int Len);
	boolean ZCLImageCompleteWait(NativeLong hCamera, Memory pBuf, IntByReference  pSpeed, ShortByReference pCycleTime, ShortByReference pCycleCount );
	boolean ZCLImageCompleteWaitTimeOut(NativeLong hCamera, Memory pBuf, IntByReference  pSpeed, ShortByReference pCycleTime, ShortByReference pCycleCount, short Time );
	
	boolean ZCLVSyncReq(NativeLong hCamera, IntByReference Hd );
	boolean ZCLVSyncCompleteWaitTimeOut(NativeLong hCamera, IntByReference Hd, int Time );
	
	int   	ZCLGetLastError();
	boolean ZCLAbortImageReqAll(NativeLong hCamera );
	boolean ZCLGetDataDepth(NativeLong hCamera, ShortByReference Depth);
	boolean ZCLSoftTrigger(NativeLong hCamera, boolean OnOff );
	boolean ZCLCameraInit(NativeLong   hCamera );
	void  	ZCLCloseAllConvHandle();
	boolean ZCLCreateConvHandle(NativeLongByReference hTbl, int Mode, int Shift, ZCL_COLORVALUE  pValue );
	boolean ZCLReset();


	public static final short ZCL_LIBRARY_STRUCT_VERSION = 210;





	
}
