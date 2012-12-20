package inrs.goniophotoradiometer.imageCapture.xcd90Implementation;

public enum ZCL_CONVERTMODE {
	ZCL_C24bit(0),							// 24bit
	ZCL_C16bit(1),							// 16bit
	ZCL_C15bit(2),							// 15bit
	ZCL_CFilter(3),							// Color Filter
	ZCL_C32bit(4),							// 32bit
	ZCL_CFilterRAW8G(5),					// RAW8 + G
	ZCL_CFilterRAW16(6),					// RAW16
	ZCL_CFilterRAW16G(7),					// RAW16 + G
	ZCL_CFilterRAW8(3);						// RAW8  = ZCL_CFilter
	
	private int convertMode;
	private ZCL_CONVERTMODE(int convert_mode){
		convertMode = convert_mode;
	}
	public int getConvertMode(){
		return convertMode;
	}
}
