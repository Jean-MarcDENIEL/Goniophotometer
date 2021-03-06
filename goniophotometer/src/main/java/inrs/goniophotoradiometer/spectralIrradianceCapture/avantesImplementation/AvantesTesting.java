package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;

public class AvantesTesting {
	
	public static void main(String[] str_args) throws InterruptedException{
		
		System.out.println("0^0 = " + Math.pow(0.0, 0.0));
		
		int _device_init;
		short _init_param = (short)0;
		testAVSFunction("AVS_Init("+_init_param+")", _device_init = AvantesLibrary.INSTANCE.AVS_Init(_init_param));
		
		int _device_count;
		testAVSFunction("AVS_GetNrOfDevice()", _device_count = AvantesLibrary.INSTANCE.AVS_GetNrOfDevices());
		
		IntByReference 	_p_required_size = new IntByReference(0);
		AvsIdentityType _p_list[] = new AvsIdentityType[_device_count];
		for (int _i=0; _i<_p_list.length; _i++){
			_p_list[_i] = new AvsIdentityType();
		}
		int _list_count;
		testAVSFunction("AVS_GetList", _list_count = AvantesLibrary.INSTANCE.AVS_GetList(0, _p_required_size, _p_list));
		testAVSFunction("AVS_GetList", _list_count = AvantesLibrary.INSTANCE.AVS_GetList(_p_required_size.getValue(), _p_required_size, _p_list));
		System.out.println("Required size (bytes) = " + _p_required_size.getValue());
		for (AvsIdentityType _id_type : _p_list){
			System.out.println("   status : " + _id_type.m_Status);
			System.out.print  ("   serial : ");
			for (byte _serial_byte : _id_type.m_aSerial){
				System.out.print("" + (char)_serial_byte);
			}
			System.out.println("");
			System.out.print  ("   user friendly id : ");
			for (byte _id_byte : _id_type.m_aUserFriendlyId){
				System.out.print("" + (char)_id_byte);
			}
			System.out.println("");
		}
		
		NativeLong _handle = new NativeLong(0);
		_handle.setValue(AvantesLibrary.INSTANCE.AVS_Activate(_p_list[0]));
		testAVSFunction("AVS_Activate", (int) ( _handle.longValue()));
		if (_handle.longValue() == 1000L){
			System.out.println("INVALID_AVS_HANDLE_VALUE");
			System.exit(1);
		}
		
		testAVSFunction("AVS_UseHighResAdc", AvantesLibrary.INSTANCE.AVS_UseHighResAdc(_handle, 1));
		
		testAVSFunction("AVS_Register", AvantesLibrary.INSTANCE.AVS_Register(AvantesLibrary.DEFAULT_HWINDOW));
		
		byte[] _FPGAVersion 	= new byte[16];
		byte[] _FirmwareVersion	= new byte[16];
		byte[] _DLLVersion		= new byte[16];
		//testAVSFunction("AVS_GetVersionInfo", AvantesLibrary.INSTANCE.AVS_GetVersionInfo(_handle, _FPGAVersion, _FirmwareVersion, _DLLVersion));
		
		//ShortByReference _pixel_count = new ShortByReference();
		ShortByReference _pixel_count = new ShortByReference((short) 35);
		
		
		testAVSFunction("AVS_GetNumPixels", AvantesLibrary.INSTANCE.AVS_GetNumPixels(_handle, _pixel_count));
		System.out.println("   Pixel count = " + _pixel_count.getValue());
			
		int _nr_averages = 5;
		int _int_time = 1000;
		
		MeasConfigType _meas_config = new MeasConfigType();
		_meas_config.m_StartPixel 			= 0;
		_meas_config.m_StopPixel 			= (char) (_pixel_count.getValue() - 1);
		_meas_config.m_IntegrationTime		= _int_time;					// 1000 ms
		_meas_config.m_IntegrationDelay		= 0;							// FPGA clock cycles
		_meas_config.m_NrAverages			= _nr_averages;
		_meas_config.m_CorDynDark.m_Enable	= 0;
		_meas_config.m_CorDynDark.m_ForgetPercentage = 0;
		_meas_config.m_Smoothing.m_SmoothPix = 0;
		_meas_config.m_Smoothing.m_SmoothModel	= 0;
		_meas_config.m_SaturationDetection	= 1;
		_meas_config.m_Trigger.m_Mode		= 0;
		_meas_config.m_Trigger.m_Source		= 0;
		_meas_config.m_Trigger.m_SourceType	= 0;
		_meas_config.m_Control.m_StrobeControl	= 0;
		_meas_config.m_Control.m_LaserDelay		= 0;
		_meas_config.m_Control.m_LaserWidth		= 0;
		_meas_config.m_Control.m_LaserWaveLength	= 0f;
		_meas_config.m_Control.m_StoreToRam			= 0;
		testAVSFunction("AVS_PrepareMEasure", AvantesLibrary.INSTANCE.AVS_PrepareMeasure(_handle, _meas_config));

		System.out.println("Measuring ...");
		testAVSFunction("AVS_Measure", AvantesLibrary.INSTANCE.AVS_Measure(_handle, AvantesLibrary.DEFAULT_HWINDOW, (short)1));
		
		DeviceConfigType 	_parameter_data = new DeviceConfigType();
		IntByReference		_desired_size = new IntByReference();
		AvantesLibrary.INSTANCE.AVS_GetParameter(_handle, 0, _desired_size, _parameter_data);
		System.out.println("  desired size of parameter data : " + _desired_size.getValue());
		testAVSFunction("AVS_GetParameter", AvantesLibrary.INSTANCE.AVS_GetParameter(_handle, _desired_size.getValue(), _desired_size, _parameter_data));
		System.out.println("   -> m_Len : " + _parameter_data.m_Len);
		System.out.print("   ->useFriendly : ");
		for (byte _friendly : _parameter_data.m_aUserFriendlyId){
			System.out.print((char)_friendly);
		}
		System.out.println("");

		DetectorType _detector = _parameter_data.m_Detector;
		System.out.println("    -> type : " + _detector.m_SensorType);
		System.out.println("    -> nrPixel : " + _detector.m_NrPixels);
		for (int _i=0; _i<5; _i++){
			System.out.println("   -> wvl poly : " + _detector.m_aFit[_i]);
		}
		
		
		testAVSFunction("AVs_PollScan", AvantesLibrary.INSTANCE.AVS_PollScan(_handle));	
		Thread.sleep(_nr_averages*_int_time + 500);
		testAVSFunction("AVS_PollScan", AvantesLibrary.INSTANCE.AVS_PollScan(_handle));
		
		double _meas_result[] = new double[_pixel_count.getValue()];
		IntByReference _time_stamp = new IntByReference();
		testAVSFunction("AVS_GetScopeData", AvantesLibrary.INSTANCE.AVS_GetScopeData(_handle, _time_stamp, _meas_result));
		double _pix = 0.0;
		for (double _val : _meas_result){
			double _wave = 0.0;
			for (int _i=0; _i<5; _i++){
				_wave += Math.pow(_pix, (double)_i) * (double)_detector.m_aFit[_i];
			}
			System.out.println(((int)_wave) + " :    "  + (_val == 65535.0? "65535.0 --- SATURATED" : _val));
			
			_pix += 1.0;
		}
		
		byte[] _saturation_tab = new byte[_pixel_count.getValue()];
		testAVSFunction("AVS_GetSaturatedPixels", AvantesLibrary.INSTANCE.AVS_GetSaturatedPixels(_handle, _saturation_tab));
		_pix = 0.0;
		for (byte _sat_val : _saturation_tab){
			double _wave = 0.0;
			for (int _i=0; _i<5; _i++){
				_wave += Math.pow(_pix, (double)_i) * (double)_detector.m_aFit[_i];
			}
			System.out.println(((int)_wave) + " :   "  + (_sat_val == 1 ? "  SATURATED" : " - "));
			_pix += 1.0;
		}
		
		testAVSFunction("AVS_Deactivate", AvantesLibrary.INSTANCE.AVS_Deactivate(_handle));
		
		testAVSFunction("AVS_Done()", AvantesLibrary.INSTANCE.AVS_Done());
		
	}
	
	public static void testAVSFunction(String function_name, int avs_value){
		try{
			AvantesReturnCode _ret_code = AvantesReturnCode.getCorrespondingReturnValue(avs_value);
			if (_ret_code != AvantesReturnCode.ERR_SUCCESS){
				System.out.println(function_name + " ... returned error : " + avs_value + " = "+ _ret_code.getReturnMeaning() + " meaning that " + _ret_code.getReturnDescription());
				System.out.println("Exiting with code 1");
				System.exit(1);
			}
			else{
				System.out.println(function_name + " ... passing with return code " + avs_value);
			}
		}
		catch(IllegalArgumentException _e){
			System.out.println(function_name + " ... passing with return code " + avs_value);
		}
	}
}
