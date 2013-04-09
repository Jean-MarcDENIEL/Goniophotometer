package inrs.goniophotoradiometer.imageCapture.xcd90Implementation;

import java.awt.Color;
import java.awt.Graphics;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.ShortByReference;

import inrs.goniophotoradiometer.imageCapture.GrayscaleImageCapture;
import inrs.goniophotoradiometer.imageCapture.xcd90Implementation.ZCL_SETFEATUREVALUE.ZCL_FEATUREID;
import inrs.goniophotoradiometer.imageCapture.xcd90Implementation.ZCL_SETFEATUREVALUE.ZCL_SETREQID;

public class XCD90ImageCapture implements GrayscaleImageCapture {

	private static final float	MIN_SHUTTER_TIME_SEC = 1E-7f;
	private static final float	MAX_SHUTTER_TIME_SEC = 16f;
	private static final short	MAX_WAITING_TIME_MILLISEC = (short) ((MAX_SHUTTER_TIME_SEC + 1) * 1000);
	private static final short	DELAY_AFTER_CAMERA_ORDER_MILLISEC = 50;
	private static final int	EIGHT_BITS_IMAGE_BIT_COUNT = 8;
	private static final int	EIGHT_BITS_IMAGE_PIXEL_SIZE_BYTE = 1;
	private static final int	SIXTEEN_BITS_IMAGE_PIXEL_SIZE_BYTE = 2;



	private NativeLong cameraHandle;

	public XCD90ImageCapture(){
	}

	public int getImageHeight() throws ImageCaptureException {
		ZCL_GETIMAGEINFO _image_info = new ZCL_GETIMAGEINFO();
		invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLGetImageInfo(cameraHandle, _image_info));
		return _image_info.Image.Height;
	}

	public int getImageWidth() throws ImageCaptureException {
		ZCL_GETIMAGEINFO _image_info = new ZCL_GETIMAGEINFO();
		invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLGetImageInfo(cameraHandle, _image_info));
		return _image_info.Image.Width;
	}

	public int getImageDepth() throws ImageCaptureException {
		ShortByReference _data_depth = new ShortByReference();
		invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLGetDataDepth(cameraHandle, _data_depth));
		return _data_depth.getValue();
	}

	public int[] captureImage(int[] image_buffer) throws ImageCaptureException {
		int _height = getImageHeight();
		int _width = getImageWidth();
		int[] _res;
		if (image_buffer == null){
			_res = new int[ _height * _width ];
		}
		else{
			_res = image_buffer;
		}
		int _byte_count_per_pixel = getImageDepth() > EIGHT_BITS_IMAGE_BIT_COUNT ? SIXTEEN_BITS_IMAGE_PIXEL_SIZE_BYTE : EIGHT_BITS_IMAGE_PIXEL_SIZE_BYTE;
		int _buffer_size = _height * _width * _byte_count_per_pixel;
		Memory _buffer = new Memory(_buffer_size);

		if (	invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLIsoAlloc(cameraHandle)) &&
				invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLImageReq(cameraHandle, _buffer, _buffer_size)) &&
				invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLIsoStart(cameraHandle, (short) 1)) &&	
				invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLImageCompleteWaitTimeOut(cameraHandle, _buffer, null, null, null, MAX_WAITING_TIME_MILLISEC))&&
				invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLIsoStop(cameraHandle)) &&
				invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLIsoRelease(cameraHandle))){
			int _buffer_index = 0;
			int _res_index = 0;
			for (int _row =0; _row<_height; _row ++){
				for (int _column = 0; _column< _width; _column ++){
					if (_byte_count_per_pixel == 1){
						// 8 bits
						_res[_res_index] = _buffer.getByte(_buffer_index) & 0xFF;
					}
					else{
						// 16 bits
						int _high_bits = _buffer.getByte(_buffer_index) & 0xFF;
						int _low_bits = _buffer.getByte(_buffer_index+1) & 0xFF;

						_res[_res_index] = (_high_bits<<8) | _low_bits;
					}
					_res_index ++;
					_buffer_index += _byte_count_per_pixel;
				}
			}
		}
		return _res;
	}


	public void setExposureTime(float seconds_of_exposure) throws ImageCaptureException {
		ZCL_SETFEATUREVALUE _set_exposure_time = new ZCL_SETFEATUREVALUE();
		_set_exposure_time.FeatureID = ZCL_FEATUREID.ZCL_SHUTTER.getFeatureID();
		_set_exposure_time.ReqID = ZCL_SETREQID.ZCL_ABSVALUE.getRequestID();
		_set_exposure_time.u.switchToStd();
		_set_exposure_time.u.Std.Abs_Value = seconds_of_exposure;
		invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLSetFeatureValue(cameraHandle, _set_exposure_time));
	}

	public float getMinExposureTime() throws ImageCaptureException {
		return MIN_SHUTTER_TIME_SEC;
	}

	public float getMaxExposureTime() throws ImageCaptureException {
		return MAX_SHUTTER_TIME_SEC;
	}

	public void setGain(float gain_value) throws ImageCaptureException {
		ZCL_SETFEATUREVALUE _set_gain = new ZCL_SETFEATUREVALUE();
		_set_gain.FeatureID = ZCL_SETFEATUREVALUE.ZCL_FEATUREID.ZCL_GAIN.getFeatureID();
		_set_gain.ReqID = ZCL_SETFEATUREVALUE.ZCL_SETREQID.ZCL_VALUE.getRequestID();
		_set_gain.u.switchToStd();
		_set_gain.u.Std.Value = (short)gain_value;
		invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLSetFeatureValue(cameraHandle, _set_gain));
	}

	public float getMinGain() throws ImageCaptureException {
		return 0f;
	}

	public float getMaxGain() throws ImageCaptureException {
		return 512f;
	}

	public void initCapture() throws ImageCaptureException {
		// retrieving camera list
		ZCL_LIST	_camera_list 	= new ZCL_LIST();
		NativeLongByReference _h_camera = new NativeLongByReference();
		_camera_list.CameraCount = 0;
		invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLGetList(_camera_list));	// first call to get camera count
		invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLGetList(_camera_list));  // second call to complete the "count" sized camera array

		// opening the first camera
		invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLOpen(_camera_list.Info[0].UID, _h_camera));
		cameraHandle = _h_camera.getValue();

		// cleaning
		invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLAbortImageReqAll(cameraHandle));
		invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLIsoStop(cameraHandle));
		ZCL_LIBRARY.INSTANCE.ZCLCloseAllConvHandle();
		invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLClose(cameraHandle));

		// re opening
		invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLOpen(_camera_list.Info[0].UID, _h_camera));
		cameraHandle = _h_camera.getValue();

		// setting camera mode
		ZCL_CAMERAMODE _camera_mode_write = new ZCL_CAMERAMODE();
		_camera_mode_write.StdMode_Flag = true;
		_camera_mode_write.u.setStandardMode();
		_camera_mode_write.u.Std.FrameRate =	ZCL_CAMERAMODE.ZCL_FPS.ZCL_Fps_75.getFpsID();
		_camera_mode_write.u.Std.Mode = 		ZCL_CAMERAMODE.ZCL_STDMODE.ZCL_VGA_MONO16.getModeID();
		invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLIsoRelease(cameraHandle));
		invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLSetCameraMode(cameraHandle, _camera_mode_write));

		// setting bit shift
		NativeLongByReference _htbl = new NativeLongByReference();
		if (getImageDepth() > 8){
			// 16 bits 9-2 shift
			invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLCreateConvHandle(_htbl, ZCL_CONVERTMODE.ZCL_C16bit.getConvertMode(), ZCL_SHIFTID.ZCL_SFT2.getShiftID(), null));
		}
		else{
			// 8 bits : 7-0 shift
			invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLCreateConvHandle(_htbl, ZCL_CONVERTMODE.ZCL_CFilterRAW8.getConvertMode(), ZCL_SHIFTID.ZCL_SFT0.getShiftID(), null));
		}


	}

	public void endCapture() throws ImageCaptureException {
		invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLAbortImageReqAll(cameraHandle));
		invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLIsoRelease(cameraHandle));
		invokeZCL(ZCL_LIBRARY.INSTANCE.ZCLClose(cameraHandle));
	}

	private boolean invokeZCL(boolean zcl_method_return) throws ImageCaptureException{
		if(!zcl_method_return){
			ImageCaptureException _e = new ImageCaptureException(STATUS_RTNCODE.decodeStatus(ZCL_LIBRARY.INSTANCE.ZCLGetLastError()));
			throw (_e);
		}
		try {
			Thread.sleep(DELAY_AFTER_CAMERA_ORDER_MILLISEC);
		} catch (InterruptedException e) {}
		return true;
	}

	public static void main(String[] args){
		try{
			XCD90ImageCapture _camera = new XCD90ImageCapture();
			System.out.println("Initializing");
			_camera.initCapture();
			System.out.println("Exposure time");
			_camera.setExposureTime(0.05f);
			final int _image_height = _camera.getImageHeight();
			final int _image_width = _camera.getImageWidth();
			final int[] _buffer = _camera.captureImage(null);


			final JFrame	_window = new JFrame();
			_window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			try {
				SwingUtilities.invokeAndWait(new Runnable(){

					public void run() {
						_window.setSize(_image_width, _image_height);
						_window.setVisible(true);
						JPanel _panel = new JPanel(){
							private static final long serialVersionUID = 1L;
							@Override
							public void paintComponent(Graphics _graphics){
								int _index = 0;
								for (int _row = 0; _row<_image_height; _row++){
									for (int _column=0; _column<_image_width;_column++){
										int _grey_level = _buffer[_index++] >>>2;
								_graphics.setColor(new Color(_grey_level, _grey_level, _grey_level));
								_graphics.drawLine(_column, _row, _column, _row);
									}
								}
							}
						};

						_window.setContentPane(_panel);

						_window.repaint();
					}
				} );
			}

			catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (int _i=0; _i< 100; _i++){
				System.out.println("Image " + _i);
				_camera.captureImage(_buffer);
				_window.repaint();

				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			_camera.endCapture();
			System.out.println("End");
			System.exit(0);
		}
		catch(ImageCaptureException _e){
			System.out.println("CaptureException : " + _e.getMessage());
			_e.printStackTrace();
			System.exit(1);
		}
	}
}
