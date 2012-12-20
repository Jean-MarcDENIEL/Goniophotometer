package inrs.goniophotoradiometer.imageCapture.xcd90Implementation;

public enum ZCL_SHIFTID {
	//	15 14 13 12 ..... 2 1 0
	//
	ZCL_SFT0(0),							// 7-0ƒrƒbƒg
	ZCL_SFT1(1),							// 8-1ƒrƒbƒg
	ZCL_SFT2(2),							// 9-2ƒrƒbƒg
	ZCL_SFT3(3),							// 10-3ƒrƒbƒg
	ZCL_SFT4(4),							// 11-4ƒrƒbƒg
	ZCL_SFT5(5),							// 12-5ƒrƒbƒg
	ZCL_SFT6(6),							// 13-6ƒrƒbƒg
	ZCL_SFT7(7),							// 14-7ƒrƒbƒg
	ZCL_SFT8(8);							// 15-8ƒrƒbƒg

	private int shiftID;
	private ZCL_SHIFTID(int shift_id){
		shiftID = shift_id;
	}
	public int getShiftID(){
		return shiftID;
	}
}
