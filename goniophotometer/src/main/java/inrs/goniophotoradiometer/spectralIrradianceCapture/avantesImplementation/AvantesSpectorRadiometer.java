package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.awt.Polygon;

import c4sci.math.algebra.Floatings;

import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;

import inrs.goniophotoradiometer.exceptions.RadiometryException;
import inrs.goniophotoradiometer.spectralIrradianceCapture.SpectralIrradiance;
import inrs.goniophotoradiometer.spectralIrradianceCapture.SpectroRadiometer;

public class AvantesSpectorRadiometer implements SpectroRadiometer {

	
	private static AvsIdentityType[]		KNOWN_DEVICES				= null;
	private static String					devicesState				= new String("No error");
	private static final long				WAIT_FOR_DATA				= 500L;
	private static final float				DEFAULT_INTEGRATION_TIME	= 1000f;
	private static final float				MAX_16BITS_IRRADIANCE_VALUE	= 65535f;
	private static final float				MAX_RELATIVE_LEVEL			= 0.90f;
	private static final float				MIN_RELATIVE_LEVEL			= 0.75f;
	private static final int				WAVELENGTH_POLYNOM_LENGTH	= 5;
	
	


	
	static{
		try{
			short _device_count = (short) AvantesLibrary.INSTANCE.AVS_Init(AvantesLibrary.USB_PORT);
			useAVSFunction ("AVS_Init", _device_count);
			
			short _available_device_count = (short)AvantesLibrary.INSTANCE.AVS_GetNrOfDevices();
			useAVSFunction("AVS_GetNrOfDevices", _available_device_count);
			
			KNOWN_DEVICES = new AvsIdentityType[_available_device_count];
			for (int _i=0; _i<_available_device_count; _i++){
				KNOWN_DEVICES[_i] = new AvsIdentityType();
			}
			IntByReference _desired_list_memory_size = new IntByReference();
			AvantesLibrary.INSTANCE.AVS_GetList(0, _desired_list_memory_size, KNOWN_DEVICES);
			useAVSFunction("AVS_GetList", AvantesLibrary.INSTANCE.AVS_GetList(_desired_list_memory_size.getValue(), _desired_list_memory_size, KNOWN_DEVICES));
		}
		catch(RadiometryException _e){
			KNOWN_DEVICES = null;
		}
	}

	
	private static void useAVSFunction(String function_name, int function_return_value) throws RadiometryException{
		if (function_return_value >= 0){
			return;
		}
		AvantesReturnCode _error = AvantesReturnCode.getCorrespondingReturnValue(function_return_value);
		devicesState = function_name + " returned " + function_return_value + " - " + _error.getReturnMeaning() + " meaning : " + _error.getReturnDescription();
		throw new RadiometryException(devicesState);
	}
	
	private static NativeLong openAVSDevice(String device_name) throws RadiometryException{
		for (AvsIdentityType _device_id : KNOWN_DEVICES){
			String _device_name = getDeviceName(_device_id.m_aUserFriendlyId);
			if (_device_name.compareTo(device_name) == 0){
				int _res_handle = AvantesLibrary.INSTANCE.AVS_Activate(_device_id);
				useAVSFunction("AVS_Activate", _res_handle);
				return new NativeLong(_res_handle);
			}
		}
		return null;
	}
	
	private static String getDeviceName(byte[] byte_device_id){
		StringBuffer _res = new StringBuffer();
		int _i = 0;
		while ((_i < byte_device_id.length)&&(byte_device_id[_i] > 0)){
			_res.append((char)byte_device_id[_i++]);
		}
		return _res.toString();
	}
	
	private NativeLong			deviceHandle;
	private ShortByReference	pixelCount;
	private float				currentIntegrationTimeMillisec;
	private float				minIntegrationTimeMillisec;
	private float				maxIntegrationTimeMillisec;
	private float				measurementTimeMillisec;
	private float				wavelengthPolynom[];
	
	@SuppressWarnings("unused")
	private AvantesSpectorRadiometer(){}
	/**
	 * 
	 * @param device_name Device handle.
	 * @param min_integration_time_millisec Minimum acceptable integration time (in milliseconds)
	 * @param max_integration_time_millisec Maximum acceptable integration time (in milliseconds)
	 * @param meas_time_millisec Measurement time (in milliseconds) used to define the number of measurements to be averaged.
	 * @throws RadiometryException
	 */
	public AvantesSpectorRadiometer(String device_name, 
			float min_integration_time_millisec, 
			float max_integration_time_millisec,
			float meas_time_millisec) 
					throws RadiometryException{
		minIntegrationTimeMillisec	= min_integration_time_millisec;
		maxIntegrationTimeMillisec	= max_integration_time_millisec;
		measurementTimeMillisec		= meas_time_millisec;
		
		if ((deviceHandle = openAVSDevice(device_name)) == null){
			throw new RadiometryException("Cannot open" + device_name + " : " + devicesState);
		}
		useAVSFunction("AVS_UseHighResAdc", AvantesLibrary.INSTANCE.AVS_UseHighResAdc(deviceHandle, 1));
		useAVSFunction("AVS_Register", AvantesLibrary.INSTANCE.AVS_Register(AvantesLibrary.DEFAULT_HWINDOW));
		
		pixelCount	= new ShortByReference();
		useAVSFunction("AVS_GetNumPixels", AvantesLibrary.INSTANCE.AVS_GetNumPixels(deviceHandle, pixelCount));
		
		currentIntegrationTimeMillisec	 = DEFAULT_INTEGRATION_TIME;
		
		wavelengthPolynom = new float[5];
		DeviceConfigType 	_parameter_data = new DeviceConfigType();
		IntByReference		_desired_size = new IntByReference();
		AvantesLibrary.INSTANCE.AVS_GetParameter(deviceHandle, 0, _desired_size, _parameter_data);
		useAVSFunction("AVS_GetParameter", AvantesLibrary.INSTANCE.AVS_GetParameter(deviceHandle, _desired_size.getValue(), _desired_size, _parameter_data));
		DetectorType _detector = _parameter_data.m_Detector;
		for (int _i=0; _i<WAVELENGTH_POLYNOM_LENGTH; _i++){
			wavelengthPolynom[_i] = _detector.m_aFit[_i];
		}
	}
	
	public SpectralIrradiance captureIrradiance()
			throws RadiometryException {
		
		SpectralIrradiance _res = new SpectralIrradiance((int)pixelCount.getValue());
		computeWavelength(_res);
		while (true){
			performMeasurement(_res);
			if (!isIntegrationTimeInAcceptableRange(_res)){
				currentIntegrationTimeMillisec = computeIntegrationTime(_res);
			}
			else{
				return _res;
			}
		}
	}
	
	private void computeWavelength(SpectralIrradiance spectral_irradiance){
		int 	_wavelength_count 	= spectral_irradiance.getWavelengthNumber();
		float[] _wavelength_tab 	= spectral_irradiance.accessWavelengthData();
		for (int _i=0; _i<_wavelength_count; _i++){
			float _val = 0.0f;
			float _f_i = (float)_i;
			for (int _pow = 0; _pow<WAVELENGTH_POLYNOM_LENGTH; _pow++){
				_val += wavelengthPolynom[_pow] * Math.pow(_f_i, (double)_pow);
			}
			_wavelength_tab[_i] = _val;
		}
	}
	
	private boolean isIntegrationTimeInAcceptableRange(SpectralIrradiance irradiance_values){
		double min_acceptable_irradiance_value = MIN_RELATIVE_LEVEL * MAX_16BITS_IRRADIANCE_VALUE;
		double max_acceptable_irradiance_value = MAX_RELATIVE_LEVEL * MAX_16BITS_IRRADIANCE_VALUE;
		double max_irradiance_value = irradiance_values.getMaxIrradianceValue();
		return 
				((max_irradiance_value > max_acceptable_irradiance_value)&&
						(Floatings.isGreater(currentIntegrationTimeMillisec, minIntegrationTimeMillisec))) ||
				(( max_irradiance_value > min_acceptable_irradiance_value) &&
						(Floatings.isLess(currentIntegrationTimeMillisec, maxIntegrationTimeMillisec)));
	}
	
	private float computeIntegrationTime(SpectralIrradiance irradiance_values) throws RadiometryException{
		double max_achievable_irradiance_value = MAX_RELATIVE_LEVEL * MAX_RELATIVE_LEVEL* MAX_16BITS_IRRADIANCE_VALUE;
		double max_irradiance_value = irradiance_values.getMaxIrradianceValue();
		if (max_irradiance_value > 0.0){
			throw new RadiometryException("Abnormal low irradiance levels");
		}
		float _res = (float)(irradiance_values.getIntegrationTimeMs() * max_achievable_irradiance_value / max_irradiance_value);
		return Math.min(maxIntegrationTimeMillisec, Math.max(minIntegrationTimeMillisec, _res));
	}
	
	private void performMeasurement(SpectralIrradiance irradiance_result) throws RadiometryException{
		
		irradiance_result.setIntegrationTimeMs(currentIntegrationTimeMillisec);
		
		MeasConfigType _meas_config = new MeasConfigType();
		_meas_config.m_StartPixel 						= 0;
		_meas_config.m_StopPixel 						= (char) (pixelCount.getValue() - 1);
		_meas_config.m_IntegrationTime					= currentIntegrationTimeMillisec;
		_meas_config.m_IntegrationDelay					= 0;
		_meas_config.m_NrAverages						= (int)(measurementTimeMillisec/currentIntegrationTimeMillisec);
		_meas_config.m_CorDynDark.m_Enable				= 0;
		_meas_config.m_CorDynDark.m_ForgetPercentage 	= 0;
		_meas_config.m_Smoothing.m_SmoothPix 			= 0;
		_meas_config.m_Smoothing.m_SmoothModel			= 0;
		_meas_config.m_SaturationDetection				= 0;
		_meas_config.m_Trigger.m_Mode					= 0;
		_meas_config.m_Trigger.m_Source					= 0;
		_meas_config.m_Trigger.m_SourceType				= 0;
		_meas_config.m_Control.m_StrobeControl			= 0;
		_meas_config.m_Control.m_LaserDelay				= 0;
		_meas_config.m_Control.m_LaserWidth				= 0;
		_meas_config.m_Control.m_LaserWaveLength		= 0f;
		_meas_config.m_Control.m_StoreToRam				= 0;
		useAVSFunction("AVS_PrepareMEasure", AvantesLibrary.INSTANCE.AVS_PrepareMeasure(deviceHandle, _meas_config));

		useAVSFunction("AVS_Measure", AvantesLibrary.INSTANCE.AVS_Measure(deviceHandle, AvantesLibrary.DEFAULT_HWINDOW, (short)1));
		try {
			Thread.sleep((long) (WAIT_FOR_DATA + currentIntegrationTimeMillisec));
			while (true){
				int _data_available;
				useAVSFunction("AVS_PollScan", _data_available = AvantesLibrary.INSTANCE.AVS_PollScan(deviceHandle));
				if (_data_available != 0){
					useAVSFunction("AVS_GetScopeData", AvantesLibrary.INSTANCE.AVS_GetScopeData(deviceHandle, new IntByReference(), irradiance_result.accessIrradianceData()));
					return;
				}
				else{
					Thread.sleep(WAIT_FOR_DATA);
				}
			}
		} 
		catch (InterruptedException _e) {
			throw new RadiometryException("Measurement interrupted", _e);
		}
	}

}
