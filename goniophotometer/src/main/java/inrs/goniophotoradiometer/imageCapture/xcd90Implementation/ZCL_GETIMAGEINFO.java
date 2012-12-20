package inrs.goniophotoradiometer.imageCapture.xcd90Implementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class ZCL_GETIMAGEINFO extends Structure {
	public boolean 			StdMode_Flag;
	public ImageStruct		Image = new ImageStruct();
	public ExtStruct		Ext = new ExtStruct();
	
	public static class ImageStruct extends Structure{
		public short PosX;
		public short PosY;
		public short Width;
		public short Height;
		/// C type : ZCL_COLORID
		public int ColorID;
		/// Effective data length
		public int DataLength;
		/// Required buffer length
		public int Buffer;
		
		@SuppressWarnings("rawtypes")
		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] {"PosX", "PosY", "Width", "Height", "ColorID", "DataLength", "Buffer"});
		}
	}
	
	public static class ExtStruct extends Structure{
		public short MaxImageX;
		public short MaxImageY;
		public short UnitSizeX;
		public short UnitSizeY;
		public short UnitPosX;
		public short UnitPosY;
		
		@SuppressWarnings("rawtypes")
		@Override
		protected List getFieldOrder() {
			return Arrays.asList(new String[] {"MaxImageX", "MaxImageY", "UnitSizeX", "UnitSizeY", "UnitPosX", "UnitPosY"});
		}
	}
	
	/*public void switchToStandard(){
		StdMode_Flag = true;
	}
	public void switchToExt(){
		StdMode_Flag = false;
	}*/
	
	
	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {"StdMode_Flag", "Image", "Ext"});
	}

}
