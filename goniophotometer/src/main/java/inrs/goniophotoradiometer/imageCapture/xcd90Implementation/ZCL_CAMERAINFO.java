package inrs.goniophotoradiometer.imageCapture.xcd90Implementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class ZCL_CAMERAINFO extends Structure {

	/// C type : UINT64
	public long UID;
	
	/// C type : BYTE[256]
	public byte[] VendorName = new byte[256];

	/// C type : BYTE[256]
	public byte[] ModelName = new byte[256];

	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {"UID", "VendorName", "ModelName" });
	};
	
	public ZCL_CAMERAINFO(){
		super(Structure.ALIGN_NONE);
	}
}

