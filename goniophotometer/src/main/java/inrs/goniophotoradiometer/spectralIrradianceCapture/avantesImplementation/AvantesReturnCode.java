package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

public enum AvantesReturnCode {
	ERR_SUCCESS					(0,	"Success",							"Operation succeeded"),
	ERR_INVALID_PARAMETER		(-1,"Invalid Parameter",				"Function called with invalid parameter value"),
	ERR_OPERATION_NOT_SUPPORTED	(-2,"Operation not supported",			"e.g Function called to use 16bit ADC mode, with 14 bits ADC hardware"),
	ERR_DEVICE_NOt_FOUND		(-3,"Device not found",					"Opening communication failed or time-out during communication occured"),
	ERR_INVALID_DEVICE_ID		(-4,"Invalid device ID",				"AvsHandle is unknown of the DLL"),
	ERR_OPERATION_PENDING		(-5,"Operation pending",				"Function called while result of previous call to AVS_Measure is not received yet"),
	ERR_TIMEOUT					(-6,"Timeout",							"No answer received from device"),
	ERR_RESERVED				(-7,"Reserved",							"Reserved by the system"),
	ERR_INVALID_MEAS_DATA		(-8,"Invalid measurement data",			"No measurement data is received at the point AVS_GetScopeData is called"),
	ERR_INVALID_SIZE			(-9,"Invalid size",						"Allocated buffer size is too small"),
	ERR_INVALID_PIXEL_RANGE		(-10,"Invalid pixel range",				"Measurement preparation failed because pixel range is invalid"),
	ERR_INVALID_INT_TIME		(-11,"Invalid integration time",		"Meaasurement preparation failed because integration time is invalid"),
	ERR_INVALID_COMBINATION		(-12,"Invalid combination",				"Measurement preparation failed because of an invalid combination of parameters"),
	ERR_RESERVED_2				(-13,"Reserved 2",						"Reserved by the system"),
	ERR_NO_MEAS_BUFFER_AVAIL	(-14,"No measurement buffer available",	"Measurement preparation failed because no measurement buffers available"),
	ERR_UNKNOWN					(-15,"Unknown",							"Unknown error reason received from the spectroradiometer"),
	ERR_COMMUNICATION			(-16,"Error in comunication",			"Error in communication occured"),
	ERR_NO_SPECTRA_IN_RAM		(-17,"No spectra in RAM",				"No more spectra available in RAM, all read or measurement not started yet"),
	ERR_INVALID_DLL_VERSION		(-18,"Invalid DLL version",				"DLL version information cannot be retrieved"),
	ERR_NO_MEMORY				(-19,"No memory",						"Memory allocation error in the DLL"),
	ERR_DLL_INITIALISATION		(-20,"DLL initialisation",				"Function called before AVS_Init() is called"),
	ERR_INVALID_STATE			(-21,"Invalid state",					"Function failed becasue AS5216 is in wrong state (e.g AVS_Measure without calling AVS_PrepareMeasurement first)"),
	ERR_INVALID_PARAMETER_NR_PIXEL	(-100,"Invalid Pixel number",		"NrOfPixel in Device data incorrect"),
	ERR_INVALID_PARAMETER_ADC_GAIN	(-101,"Invalid ADC gain",			"Gain setting is out of range"),
	ERR_INVALID_PARAMETER_ADC_OFFSET(-102,"Invalid ADC offset",			"Offset setting is out of range"),
	ERR_INVALID_MEASPARAM_AVG_SAT2	(-110,"Averaging and saturation",	"Use of sturation etection Level 2 is not compatible with the Averaging function"),
	ERR_INVALID_MEASPARAM_AVG_RAM	(-111,"Averaging and RAM",			"Use of Averaging is not compatible with the StoreToRAM function"),
	ERR_INVALID_MEASPARAM_SYNC_RAM	(-112,"Sync and RAM",				"Use of the Synchronize setting is not compatible with the StoreToRAM function"),
	ERR_INVALID_MEASPARAM_LEVEL_RAM	(-113,"Level trigg and RAM",		"Use of LevelTriggering is not compatible with the StoretoRAM function"),
	ERR_INVALID_MEASPARAM_SAT2_RAM	(-114,"Saturation and RAM",			"Use of Saturation Dectection Level 2 Parameter is not compatible with the StoreToRAM function"),
	ERR_INVALID_MEASPARAM_FWVER_RAM	(-115,"Version and RAM",			"The StreToRAM dfunction is only supported with firmware version 0.20.0.0 or later")
	;
	private int	returnValue;
	private String returnMeaning;
	private String returnDescription;
	
	private AvantesReturnCode(int ret_value, String ret_meaning, String ret_description){
		setReturnValue(ret_value);
		setReturnMeaning(ret_meaning);
		setReturnDescription(ret_description);
	}

	public static AvantesReturnCode getCorrespondingReturnValue(int ret_value) throws IllegalArgumentException {
		for (AvantesReturnCode avantes_ret_value: AvantesReturnCode.values()){
			if (avantes_ret_value.getReturnValue() == ret_value){
				return avantes_ret_value;
			}
		}
		throw new IllegalArgumentException(""+ret_value+" does not correspond to any known return value");
	}
	
	public final int getReturnValue() {
		return returnValue;
	}

	public final void setReturnValue(int return_value) {
		this.returnValue = return_value;
	}

	public final String getReturnMeaning() {
		return returnMeaning;
	}

	public final void setReturnMeaning(String return_meaning) {
		this.returnMeaning = return_meaning;
	}

	public final String getReturnDescription() {
		return returnDescription;
	}

	public final void setReturnDescription(String return_description) {
		this.returnDescription = return_description;
	}
}
