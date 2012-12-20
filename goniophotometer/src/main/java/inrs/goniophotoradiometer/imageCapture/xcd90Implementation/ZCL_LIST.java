package inrs.goniophotoradiometer.imageCapture.xcd90Implementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;

public class ZCL_LIST extends Structure {


	public int					CameraCount;
	//public long				__uu;
	public ZCL_CAMERAINFO[]		Info = new ZCL_CAMERAINFO[MAX_CAMERA_COUNT];

	
	public final static int 	MAX_CAMERA_COUNT = 10;
	
	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[]  {"CameraCount", "Info"});
	}
	
	public ZCL_LIST(){
		super(Structure.ALIGN_NONE);
	}
}
