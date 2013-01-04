package inrs.goniophotoradiometer.imageCapture.xcd90Implementation;

import inrs.goniophotoradiometer.imageCapture.xcd90Implementation.ZCL_SETFEATUREVALUE.ZCL_FEATUREID;
import inrs.goniophotoradiometer.imageCapture.xcd90Implementation.ZCL_SETFEATUREVALUE.ZCL_SETREQID;

import java.awt.Color;
import java.awt.Graphics;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;

/**
 * This class is intended for development purpose only
 * @author jeanmarc.deniel
 *
 */
public class ZclTesting {

	private ZclTesting(){}
	
	public static void main(String[] args_){
		System.out.println("Beginning camera testing");
		
		// for ZCL V 2.10.4 only
		//ZCL_LIBRARY.INSTANCE.ZCLSetStructVersion(ZCL_LIBRARY.ZCL_LIBRARY_STRUCT_VERSION);
		
		
		// ------------------------------------
		// CAMERA LIST
		// ------------------------------------
		ZCL_LIST	_camera_list 	= new ZCL_LIST();
		_camera_list.CameraCount = 0;

		//System.out.println("      ZCLReset : " + ZCL_LIBRARY.INSTANCE.ZCLReset());

		System.out.println("Retrieving camera count");
		printoutCallbackAndWait("     GetList", ZCL_LIBRARY.INSTANCE.ZCLGetList(_camera_list));
		System.out.println("     Camera count : " + _camera_list.CameraCount);

		// ------------------------------------
		// CAMERA INFO
		// ------------------------------------
		System.out.println("Retrieving camera 0 infos:");
		printoutCallbackAndWait("     ZCLGetList ", ZCL_LIBRARY.INSTANCE.ZCLGetList(_camera_list));
		System.out.println("     CameraCount : " + _camera_list.CameraCount);
		System.out.println("     UID    : 0x" + Long.toHexString(_camera_list.Info[0].UID));
		System.out.println("     Vendor : " + convertToString(_camera_list.Info[0].VendorName));
		System.out.println("     Model  : " + convertToString(_camera_list.Info[0].ModelName));

		// ------------------------------------
		// OPENING
		// ------------------------------------

		System.out.println("Opening camera");
		final NativeLongByReference _H_CAMERA  = new NativeLongByReference();
		printoutCallbackAndWait("     ZCLOpen ", ZCL_LIBRARY.INSTANCE.ZCLOpen(_camera_list.Info[0].UID, _H_CAMERA));
		System.out.println("     hCamera : 0x" + Long.toHexString(_H_CAMERA.getValue().longValue()));
		
		// ------------------------------------
		// CAMERA INFOS
		// ------------------------------------
		System.out.println("Reading camera info");
		IntByReference _speed_info = new IntByReference();
		ZCL_CAMERAINFO _camera_info = new ZCL_CAMERAINFO();
		printoutCallbackAndWait("     ZCLCameraInfo " , ZCL_LIBRARY.INSTANCE.ZCLCameraInfo(_H_CAMERA.getValue(), _camera_info, _speed_info));
		System.out.println("     UID    : 0x" + Long.toHexString(_camera_info.UID));
		System.out.println("     Vendor : " + convertToString(_camera_info.VendorName));
		System.out.println("     Model  : " + convertToString(_camera_info.ModelName));
		System.out.println("     Speed : " + _speed_info.getValue());
		waitTime();
		 
		
		// ------------------------------------
		// CLEANING
		// ------------------------------------
		System.out.println("Cleaning");
		//System.out.println("     ZCLCameraInit : " + ZCL_LIBRARY.INSTANCE.ZCLCameraInit(_h_camera.getValue()));
		//waitTime();
		printoutCallbackAndWait("     ZCLAbortImageReqAll " , ZCL_LIBRARY.INSTANCE.ZCLAbortImageReqAll(_H_CAMERA.getValue()));
		printoutCallbackAndWait("     ZCLIsoStop ", ZCL_LIBRARY.INSTANCE.ZCLIsoStop(_H_CAMERA.getValue()));
		System.out.println		("     ZCLCloseallConvHandle "); ZCL_LIBRARY.INSTANCE.ZCLCloseAllConvHandle();
		waitTime();
		printoutCallbackAndWait("     ZCLClose  ", ZCL_LIBRARY.INSTANCE.ZCLClose(_H_CAMERA.getValue()));

		// ------------------------------------
		// RE OPENING
		// ------------------------------------
		System.out.println("Reopening camera");
		printoutCallbackAndWait("     ZCLOpen ", ZCL_LIBRARY.INSTANCE.ZCLOpen(_camera_list.Info[0].UID, _H_CAMERA));

		// ------------------------------------
		// MODE SETTING
		// ------------------------------------
		System.out.println("Setting and retrieving camera mode");
		ZCL_CAMERAMODE _camera_mode_write = new ZCL_CAMERAMODE();

		_camera_mode_write.StdMode_Flag = true;		// normal
		_camera_mode_write.u.setStandardMode();
		_camera_mode_write.u.Std.FrameRate =	ZCL_CAMERAMODE.ZCL_FPS.ZCL_Fps_75.getFpsID();
		_camera_mode_write.u.Std.Mode = 		ZCL_CAMERAMODE.ZCL_STDMODE.ZCL_VGA_MONO16.getModeID();

		printoutCallbackAndWait("     ZCLIsoRelease  ",  ZCL_LIBRARY.INSTANCE.ZCLIsoRelease(_H_CAMERA.getValue()));
		printoutCallbackAndWait("     ZCLSetCameraMode  " , ZCL_LIBRARY.INSTANCE.ZCLSetCameraMode(_H_CAMERA.getValue(), _camera_mode_write));

		// ------------------------------------
		// MODE READING
		// ------------------------------------
		ZCL_CAMERAMODE _camera_mode_read = new ZCL_CAMERAMODE();
		_camera_mode_read.u.setStandardMode();
		printoutCallbackAndWait("     ZCLNowCameraMode (Standard) " , ZCL_LIBRARY.INSTANCE.ZCLNowCameraMode(_H_CAMERA.getValue(), _camera_mode_read));
		System.out.println("     StdMode_Flag : " + _camera_mode_read.StdMode_Flag);
		if (_camera_mode_read.StdMode_Flag){
			System.out.println("     Std.Framerate : " + _camera_mode_read.u.Std.FrameRate);
			System.out.println("     Std.Mode : " + _camera_mode_read.u.Std.Mode);
		}
		else{
			_camera_mode_read.u.setExtendedMode();
			System.out.println("     ZCLNowCameraMode (Extended) : " + ZCL_LIBRARY.INSTANCE.ZCLNowCameraMode(_H_CAMERA.getValue(), _camera_mode_read));
			System.out.println("     Ext.ColorID : " + _camera_mode_read.u.Ext.ColorID);
			System.out.println("     Ext.FilterID : " + _camera_mode_read.u.Ext.FilterID);
			System.out.println("     Ext.Mode : " + _camera_mode_read.u.Ext.Mode);
		}


		// ------------------------------------
		// GAIN
		// ------------------------------------
		System.out.println("Setting camera capture settings");
		ZCL_SETFEATUREVALUE _set_gain = new ZCL_SETFEATUREVALUE();
		_set_gain.FeatureID = ZCL_SETFEATUREVALUE.ZCL_FEATUREID.ZCL_GAIN.getFeatureID();
		_set_gain.ReqID = ZCL_SETFEATUREVALUE.ZCL_SETREQID.ZCL_VALUE.getRequestID();
		_set_gain.u.switchToStd();
		final int _BASE_GAIN = 100;
		_set_gain.u.Std.Value = _BASE_GAIN;
		printoutCallbackAndWait("     Gain = " + _set_gain.u.Std.Abs_Value , ZCL_LIBRARY.INSTANCE.ZCLSetFeatureValue(_H_CAMERA.getValue(), _set_gain));
/*
		// ------------------------------------
		// TRIGGER
		// ------------------------------------
		ZCL_SETFEATUREVALUE _set_trigger = new ZCL_SETFEATUREVALUE();
		_set_trigger.FeatureID = ZCL_FEATUREID.ZCL_TRIGGER.getFeatureID();
		final boolean _triggering = false;
		if (_triggering){
			_set_trigger.ReqID = ZCL_SETREQID.ZCL_VALUE.getRequestID(); //ZCL_FEATURE_OFF.getRequestID();
			_set_trigger.u.switchToTrigger();
			_set_trigger.u.Trigger.Source = (byte) ZCL_TRIGGERSOURCE.ZCL_Trigger_Source0.getTriggerSource();
			_set_trigger.u.Trigger.Mode = (byte) ZCL_TRIGGERMODE.ZCL_Trigger_Mode0.getTriggerMode();
		}
		else{
			_set_trigger.ReqID = ZCL_SETREQID.ZCL_FEATURE_OFF.getRequestID();
			//_set_trigger.ReqID = ZCL_SETREQID.ZCL_ABSVALUE.getRequestID();
			_set_trigger.u.switchToTrigger();
			_set_trigger.u.Trigger.Mode = ZCL_TRIGGERMODE.ZCL_Trigger_Mode0.getTriggerMode();
			_set_trigger.u.Trigger.Source = ZCL_TRIGGERSOURCE.ZCL_Trigger_Source0.getTriggerSource();
			_set_trigger.u.Trigger.Abs_Value = 100.0f; // 100 milli sec ?
		}
		printoutCallbackAndWait("     Triggering " , ZCL_LIBRARY.INSTANCE.ZCLSetFeatureValue(_h_camera.getValue(), _set_trigger));
*/
		// ------------------------------------
		// SHUTTER
		// ------------------------------------
		ZCL_SETFEATUREVALUE _set_exposure_time = new ZCL_SETFEATUREVALUE();
		final float _shutter = 0.03f;
		_set_exposure_time.FeatureID = ZCL_FEATUREID.ZCL_SHUTTER.getFeatureID();
		_set_exposure_time.ReqID = ZCL_SETREQID.ZCL_ABSVALUE.getRequestID();
		_set_exposure_time.u.switchToStd();
		_set_exposure_time.u.Std.Abs_Value = _shutter;
		printoutCallbackAndWait("     Shutter = " + _shutter , ZCL_LIBRARY.INSTANCE.ZCLSetFeatureValue(_H_CAMERA.getValue(), _set_exposure_time));
		 
		// ------------------------------------
		// AUTO EXPOSURE
		// ------------------------------------		
		/*
		ZCL_SETFEATUREVALUE _set_AE = new ZCL_SETFEATUREVALUE();
		_set_AE.FeatureID = ZCL_FEATUREID.ZCL_AE.getFeatureID();
		_set_AE.ReqID = ZCL_SETREQID.ZCL_VALUE.getRequestID();
		_set_AE.u.switchToStd();
		_set_AE.u.Std.Value = (short)400;
		System.out.println("     AE : " + ZCL_LIBRARY.INSTANCE.ZCLSetFeatureValue(_h_camera.getValue(), _set_AE));
		waitTime();
		 */

		// ------------------------------------
		// GAMMA
		// ------------------------------------
		/*
		ZCL_SETFEATUREVALUE _set_gamma = new ZCL_SETFEATUREVALUE();
		_set_gamma.FeatureID = ZCL_FEATUREID.ZCL_GAMMA.getFeatureID();
		_set_gamma.ReqID = ZCL_SETREQID.ZCL_VALUE.getRequestID();
		_set_gamma.u.switchToStd();
		_set_gamma.u.Std.Value = (short)0;
		System.out.println("     Gamma : " + ZCL_LIBRARY.INSTANCE.ZCLSetFeatureValue(_h_camera.getValue(), _set_gamma));
		waitTime();
		 */

		// ------------------------------------
		// READING IMAGE INFO
		// ------------------------------------
		System.out.println("Retrieving image info : ");
		final ZCL_GETIMAGEINFO _GET_IMAGE_INFO = new ZCL_GETIMAGEINFO();
		printoutCallbackAndWait("     ZCLGetImageInfo " , ZCL_LIBRARY.INSTANCE.ZCLGetImageInfo(_H_CAMERA.getValue(), _GET_IMAGE_INFO));
		System.out.println("     .StdMode_flag : " + _GET_IMAGE_INFO.StdMode_Flag);
		if (_GET_IMAGE_INFO.StdMode_Flag){
			System.out.println("      .Image.Buffer : " + _GET_IMAGE_INFO.Image.Buffer);
			System.out.println("      .Image.ColorID : " + _GET_IMAGE_INFO.Image.ColorID);
			System.out.println("      .Image.DataLength : " + _GET_IMAGE_INFO.Image.DataLength);
			System.out.println("      .Image.Height : " + _GET_IMAGE_INFO.Image.Height);
			System.out.println("      .Image.width : " + _GET_IMAGE_INFO.Image.Width);
			System.out.println("      .Image.PosX : " + _GET_IMAGE_INFO.Image.PosX);
			System.out.println("      .Image.PosY : " + _GET_IMAGE_INFO.Image.PosY); 
		}

		// ------------------------------------
		// READING IMAGE DEPTH
		// ------------------------------------
		/*System.out.println("Reading data depth");
		ShortByReference _data_depth = new ShortByReference();
		printoutCallbackAndWait("       ZCLImageDepth " , ZCL_LIBRARY.INSTANCE.ZCLGetDataDepth(_h_camera.getValue(), _data_depth));
		System.out.println("       Data depth = " + _data_depth.getValue());
		final int _byte_count_per_pixel = _data_depth.getValue() > 8 ? 2 : 1;*/
		final int _BYTE_COUNT_PER_PIXEL = _GET_IMAGE_INFO.Image.Buffer / _GET_IMAGE_INFO.Image.Height / _GET_IMAGE_INFO.Image.Width;
		
		// ------------------------------------
		// BITSHIFT
		// ------------------------------------
		
		System.out.println("Setting bitshift");
		NativeLongByReference _htbl = new NativeLongByReference();
		if (_BYTE_COUNT_PER_PIXEL > 1){
			// 16 bits 9-2 shift
			printoutCallbackAndWait("     ZCLCreateConvHandle " , ZCL_LIBRARY.INSTANCE.ZCLCreateConvHandle(_htbl, ZCL_CONVERTMODE.ZCL_C16bit.getConvertMode(), ZCL_SHIFTID.ZCL_SFT2.getShiftID(), null));
		}
		else{
			// 8 bits : 7-0 shift
			printoutCallbackAndWait("     ZCLCreateConvHandle " , ZCL_LIBRARY.INSTANCE.ZCLCreateConvHandle(_htbl, ZCL_CONVERTMODE.ZCL_CFilterRAW8.getConvertMode(), ZCL_SHIFTID.ZCL_SFT0.getShiftID(), null));
		}
		System.out.println("     hTBL = " + _htbl.getValue().longValue());
		 
		// ------------------------------------
		// CAPTURE IMAGE
		// ------------------------------------

		System.out.println("Capturing image");
		final JFrame	_WINDOW = new JFrame();
		_WINDOW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		final Memory _CAPT = new Memory(_GET_IMAGE_INFO.Image.Buffer);

		printoutCallbackAndWait("        ZCLIsoAlloc " , ZCL_LIBRARY.INSTANCE.ZCLIsoAlloc(_H_CAMERA.getValue()));
		printoutCallbackAndWait("        ZCLImageReq " , ZCL_LIBRARY.INSTANCE.ZCLImageReq(_H_CAMERA.getValue(), _CAPT, _GET_IMAGE_INFO.Image.Buffer));
		printoutCallbackAndWait("        ZCLIsoStart " , ZCL_LIBRARY.INSTANCE.ZCLIsoStart(_H_CAMERA.getValue(), (short) 1));
		/*
				if (_triggering){
					printoutCallbackAndWait("        ZCLSoftTrigger " + true  , ZCL_LIBRARY.INSTANCE.ZCLSoftTrigger(_h_camera.getValue(), true));
					printoutCallbackAndWait("        ZCLSoftTrigger " + false , ZCL_LIBRARY.INSTANCE.ZCLSoftTrigger(_h_camera.getValue(), false));
				}
		 */
		System.out.println("... waiting for image capture ...");
		final short _DELAY = 1000;
		printoutCallbackAndWait("        ZCLImageCompleteWaitTimeOut " , ZCL_LIBRARY.INSTANCE.ZCLImageCompleteWaitTimeOut(_H_CAMERA.getValue(), _CAPT, null, null, null, _DELAY));

		// ------------------------------------
		// SHOWING RESULT
		// ------------------------------------
		System.out.println("Showing image");
		waitTime();

		final int _IMAGE_WIDTH = _GET_IMAGE_INFO.Image.Width;
		final int _IMAGE_HEIGHT = _GET_IMAGE_INFO.Image.Height;

		try {
			SwingUtilities.invokeAndWait(new Runnable(){

				public void run() {
					JPanel _panel = new JPanel(){
						private static final long serialVersionUID = 1L;
						@Override
						public void paintComponent(Graphics paint_graphics){
							int _index = 0;
							for (int _row = 0; _row<_IMAGE_HEIGHT; _row++){
								for (int _column=0; _column<_IMAGE_WIDTH;_column++){
									int _grey_level;
									final int _FF = 0xFF; 
									if (_BYTE_COUNT_PER_PIXEL == 1){
										_grey_level = _CAPT.getByte(_index) & _FF;
									}
									else{
										int _high_bits	= _CAPT.getByte(_index);
										int _low_bits	= _CAPT.getByte(_index+1);
										final int _LEFT_SHIFT = 6;
										final int _RIGHT_SHIFT = 2;
										_grey_level = ((_high_bits & _FF) << _LEFT_SHIFT) | ((_low_bits & _FF)>>> _RIGHT_SHIFT);
									}

									paint_graphics.setColor(new Color(_grey_level, _grey_level, _grey_level));
									paint_graphics.drawLine(_column, _row, _column, _row);

									_index += _BYTE_COUNT_PER_PIXEL;
								}
							}
						}

					};

					_WINDOW.setContentPane(_panel);
					_WINDOW.setSize(_IMAGE_WIDTH, _IMAGE_HEIGHT);
					_WINDOW.setVisible(true);

					_panel.setBackground(Color.red);

					_WINDOW.repaint();
				}
			});
		} catch (InvocationTargetException _e) {
			_e.printStackTrace();
		} catch (InterruptedException _e) {
			_e.printStackTrace();
		}

		// ------------------------------------
		// STOPING
		// ------------------------------------
		System.out.println("Stopping capture");
		printoutCallbackAndWait("        ZCLIsoStop " , ZCL_LIBRARY.INSTANCE.ZCLIsoStop(_H_CAMERA.getValue()));
		printoutCallbackAndWait("        ZCLAbortImageReqAll " , ZCL_LIBRARY.INSTANCE.ZCLAbortImageReqAll(_H_CAMERA.getValue()));
		printoutCallbackAndWait("        ZCLIsoRelease " , ZCL_LIBRARY.INSTANCE.ZCLIsoRelease(_H_CAMERA.getValue()));
		printoutCallbackAndWait("        ZCLClose " , ZCL_LIBRARY.INSTANCE.ZCLClose(_H_CAMERA.getValue()));

		System.out.println("End of Camera testing");
		final int _WAIT_BEFORE_EXIT = 6000;
		waitTime(_WAIT_BEFORE_EXIT);
		System.exit(0);
	}

	public static String convertToString(byte[] byte_array){
		StringBuffer _res = new StringBuffer();

		for (byte _b : byte_array){
			if (_b >0){
				_res.append((char)_b);
			}
		}

		return _res.toString();
	}

	public static void waitTime(int milli_sec){
		try {
			Thread.sleep(milli_sec);
		} catch (InterruptedException _e) {
			_e.printStackTrace();
		}
	}

	public static void waitTime(){
		final int _WAIT_MILLISEC = 200;
		waitTime(_WAIT_MILLISEC);
	}

	public static void printoutCallbackAndWait(String callback_message, boolean callback_value){
		System.out.println(callback_message + " : " + callback_value);
		waitTime();
		if (!callback_value){
			System.out.println("            --> " + STATUS_RTNCODE.decodeStatus(ZCL_LIBRARY.INSTANCE.ZCLGetLastError()));
			System.exit(1);
		}
	}
}


