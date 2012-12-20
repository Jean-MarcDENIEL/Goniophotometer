package inrs.goniophotoradiometer.imageCapture.xcd90Implementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.Union;

public class ZCL_SETFEATUREVALUE extends Structure {
	public int Version;
	/// C type : ZCL_SETREQID
	public int ReqID;
	/// C type : ZCL_FEATUREID
	public int FeatureID;
	/// C type : u_union
	public u_union u;

	public static class u_union extends Union {
		/**
		 * C type : Std_struct
		 */
		public Std_struct Std = new Std_struct();
		/**
		 * WhiteBalance<br>
		 * C type : WhiteBalance_struct
		 */
		public WhiteBalance_struct WhiteBalance = new WhiteBalance_struct();
		/**
		 * Trigger<br>
		 * C type : Trigger_struct
		 */
		public Trigger_struct Trigger = new Trigger_struct();
		/**
		 * WhiteShading<br>
		 * C type : WhiteShading_struct
		 */
		public WhiteShading_struct WhiteShading = new WhiteShading_struct();


		public static class Std_struct extends Structure {
			public short Value;
			public float Abs_Value;
			@SuppressWarnings("rawtypes")
			@Override
			protected List getFieldOrder() {
				return Arrays.asList(new String[] {"Value", "Abs_Value"});
			}
		}
		public static class WhiteBalance_struct extends Structure{
			public short UB_Value;
			public short VR_Value;
			public float Abs_Value;
			@SuppressWarnings("rawtypes")
			@Override
			protected List getFieldOrder() {
				return Arrays.asList(new String[] {"UB_Value", "VR_Value", "Abs_Value"});
			}
		}
		public static class Trigger_struct extends Structure{
			public byte Polarity;
			public byte Source;
			public byte Mode;
			public short Parameter;
			public float Abs_Value;
			@SuppressWarnings("rawtypes")
			@Override
			protected List getFieldOrder() {
				return Arrays.asList(new String[] {"Polarity", "Source", "Mode", "Parameter", "Abs_Value"});
			}
		}
		public static class WhiteShading_struct extends Structure{
			public byte R_Value;
			public byte G_Value;
			public byte B_Value;
			public float Abs_Value;
			@SuppressWarnings("rawtypes")
			@Override
			protected List getFieldOrder() {
				return Arrays.asList(new String[] {"R_Value", "G_Value", "B_Value", "Abs_Value"});
			}
		}
		public void switchToStd(){
			setType(Std_struct.class);
		}
		public void switchToWhiteBalance(){
			setType(WhiteBalance_struct.class);
		}
		public void switchToTrigger(){
			setType(Trigger_struct.class);
		}
		public void switchToWhiteShading(){
			setType(WhiteShading_struct.class);
		}


	}
	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {"Version", "ReqID", "FeatureID", "u"});
	}
	public ZCL_SETFEATUREVALUE(){
		super(Structure.ALIGN_NONE);
		Version = ZCL_LIBRARY.INSTANCE.ZCLGetLibraryRevision();
	}
	
	public enum ZCL_FEATUREID{
		ZCL_BRIGHTNESS(0),		//    Brightness
		ZCL_AE(1),				//    Auto Exposure
		ZCL_SHARPNESS(2),		//    Sharpness
		ZCL_WHITEBALANCE(3),	//    White Balance
		ZCL_HUE(4),				//    HUE
		ZCL_SATURATION(5),		//    Saturation
		ZCL_GAMMA(6),			//    Gamma
		ZCL_SHUTTER(7),			//    Shutter
		ZCL_GAIN(8),			//    Gain
		ZCL_IRIS(9),			//    Iris
		ZCL_FOCUS(10),			//    Focus
		ZCL_TEMPERATURE(11),	//    Temperature
		ZCL_TRIGGER(12),		//    Trigger
		ZCL_TRIGGER_DELAY(13),	//    Trigger Delay
		ZCL_WHITE_SHADING(14),	//    White Shading
		ZCL_FRAMERATE(15),		//    FrameRate
		ZCL_ZOOM(16),			//    Zoom
		ZCL_PAN(17),			//    Pan
		ZCL_TILT(18),			//    Tilt
		ZCL_OPTICAL_FILTER(19),	//    Optical Filter
		ZCL_ONE_SHOT(20),		//    One Shot
		ZCL_MULTI_SHOT(21),		//    Multi Shot
		ZCL_POWER_ONOFF(22),	//    Power On Off
		ZCL_MEMORYCHANNEL(23);	//    Memory Channel
		ZCL_FEATUREID(int feature_id){
			featureID = feature_id;
		}
		private int	featureID;
		public int getFeatureID(){
			return featureID;
		}
	}
	
	public enum ZCL_SETREQID {
		ZCL_FEATURE_OFF(0),		// Feature Off
		ZCL_ONE_PUSH(1),		// One Push
		ZCL_AUTO(2),			// 
		ZCL_VALUE(3),			// Relative value
		ZCL_ABSVALUE(4);		// Absolute value
		private ZCL_SETREQID(int req_id){
			requestID = req_id;
		}
		private int requestID;
		public int getRequestID(){
			return requestID;
		}
	}
	
	public enum ZCL_TRIGGERMODE
	{
		ZCL_Trigger_Mode0(0),					// Trigger Mode0
		ZCL_Trigger_Mode1(1),					// Trigger Mode1
		ZCL_Trigger_Mode2(2),					// Trigger Mode2
		ZCL_Trigger_Mode3(3),					// Trigger Mode3
		ZCL_Trigger_Mode4(4),					// Trigger Mode4
		ZCL_Trigger_Mode5(5),					// Trigger Mode5
		ZCL_Trigger_Mode14(14),					// Trigger Mode14
		ZCL_Trigger_Mode15(15);					// Trigger Mode15
		
		private byte triggerMode;
		private ZCL_TRIGGERMODE(int trigger_mode){
			triggerMode = (byte) trigger_mode;
		}
		public byte getTriggerMode(){
			return triggerMode;
		}
	}
	
	public enum ZCL_TRIGGERSOURCE
	{
		ZCL_Trigger_Source0(0),				// Trigger Source0
		ZCL_Trigger_Source1(1),				// Trigger Source1
		ZCL_Trigger_Source2(2),				// Trigger Source2
		ZCL_Trigger_Source3(3),				// Trigger Source3
		ZCL_Software_Trigger(7);			// Software Trigger
		
		private byte triggerSource;
		private ZCL_TRIGGERSOURCE(int trigger_source){
			triggerSource = (byte) trigger_source;
		}
		public byte getTriggerSource(){
			return triggerSource;
		}
	}
	
	
	
}
