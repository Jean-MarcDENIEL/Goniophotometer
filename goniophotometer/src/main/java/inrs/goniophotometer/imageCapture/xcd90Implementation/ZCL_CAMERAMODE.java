package inrs.goniophotometer.imageCapture.xcd90Implementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.Union;

public class ZCL_CAMERAMODE extends Structure {
	public boolean					StdMode_Flag;
	public u						u;
	public static class u extends Union {
		public Std		Std;
		public Ext		Ext;

		public static class Std extends Structure{

			// ZCL_STDMODE
			public int		Mode;
			// ZCL_FPS
			public int		FrameRate;

			@SuppressWarnings("rawtypes")
			@Override
			protected List getFieldOrder() {
				return Arrays.asList(new String[] {"Mode", "FrameRate"});
			}
		}
		public static class Ext extends Structure {
			// ZCL_EXTMODE Mode
			public int Mode;
			// ZCL_COLORID ColorID
			public int ColorID;
			// ZCL_CFILTERMODE FilterID
			public int FilterID;
			@SuppressWarnings("rawtypes")
			@Override
			protected List getFieldOrder() {
				return Arrays.asList(new String[] {"Mode", "ColorID", "FilterID"});
			}
		}
		public u(){
			super();
			setType(Std.class);
		}
		public void setStandardMode(){
			setType(Std.class);
		}
		public void setExtendedMode(){
			setType(Ext.class);
		}
	};

	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {"StdMode_Flag", "u"});
	}

	public ZCL_CAMERAMODE(){
		super(Structure.ALIGN_NONE);
	}

	public enum ZCL_STDMODE {
		ZCL_QQVGA		(0),		//   160 x  120 YUV(4:4:4)
		ZCL_QVGA		(1),		//   320 x  240 YUV(4:2:2)
		ZCL_VGA_YUV1	(2),		//   640 x  480 YUV(4:1:1)
		ZCL_VGA_YUV2	(3),		//   640 x  480 YUV(4:2:2)
		ZCL_VGA_RGB		(4),		//   640 x  480 RGB
		ZCL_VGA_MONO	(5),		//   640 x  480 Mono
		ZCL_VGA_MONO16	(6),		//   640 x  480 Mono16
		ZCL_SVGA_YUV	(7),		//   800 x  600 YUV(4:2:2)
		ZCL_SVGA_RGB	(8),		//   800 x  600 RGB
		ZCL_SVGA_MONO	(9),		//   800 x  600 MONO
		ZCL_SVGA_MONO16	(10),		//   800 x  600 MONO16
		ZCL_XGA_YUV		(11),		//  1024 x  768 YUV(4:2:2)
		ZCL_XGA_RGB		(12),		//  1024 x  768 RGB
		ZCL_XGA_MONO	(13),		//  1024 x  768 MONO
		ZCL_XGA_MONO16	(14),		//  1024 x  768 MONO16
		ZCL_SXGA_YUV	(15),		//  1280 x  960 YUV(4:2:2)
		ZCL_SXGA_RGB	(16),		//  1280 x  960 RGB
		ZCL_SXGA_MONO	(17),		//  1280 x  960 MONO
		ZCL_SXGA_MONO16	(18),		//  1280 x  960 MONO16
		ZCL_UXGA_YUV	(19),		//  1600 x 1200 YUV(4:2:2)
		ZCL_UXGA_RGB	(20),		//  1600 x 1200 RGB
		ZCL_UXGA_MONO	(21),		//  1600 x 1200 MONO
		ZCL_UXGA_MONO16	(2);		//  1600 x 1200 MONO16

		private ZCL_STDMODE(int mode_id){
			modeID = mode_id;
		}
		private int modeID;
		public int getModeID(){
			return modeID;
		}
	}

	public enum ZCL_FPS {
		ZCL_Fps_1875(0),        		//    FrameRate_0 (1.875fps)
		ZCL_Fps_375	(1),             	//    FrameRate_1 (3.75fps)
		ZCL_Fps_75	(2),              	//    FrameRate_2 (7.5fps)
		ZCL_Fps_15	(3),              	//    FrameRate_3 (15fps)
		ZCL_Fps_30	(4),              	//    FrameRate_4 (30fps)
		ZCL_Fps_60	(5),              	//    FrameRate_5 (60fps)
		ZCL_Fps_120	(6),             	//    FrameRate_6 (120fps)
		ZCL_Fps_240	(7);             	//    FrameRate_7 (240fps)

		private ZCL_FPS(int fps_id){
			fpsID = fps_id;
		}
		private int fpsID;
		public int getFpsID(){
			return fpsID;
		}
	}
}
