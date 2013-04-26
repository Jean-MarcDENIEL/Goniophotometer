package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class DeviceConfigType extends Structure {

	public short					m_Len;
	public short					m_ConfigVersion;
	public byte						m_aUserFriendlyId[];
	public DetectorType				m_Detector;
	public IrradianceType			m_Irradiance;
	public SpectrumCalibrationType	m_Reflectance;
	public SpectrumCorrectionType	m_SpectrumCorrect;
	public StandaloneType			m_StandAlone;
	public TempSensorType			m_Temperature[];
	public TecControlType			m_TecControl;
	public ProcessControlType		m_ProcessControl;
	public byte						m_aReserved[];
	
		
	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {
				"m_Len", "m_ConfigVersion", "m_aUserFriendlyId", "m_Detector", 
				"m_Irradiance", "m_Reflectance", "m_SpectrumCorrect", "m_StandAlone", 
				"m_Temperature", "m_TecControl", "m_ProcessControl", "m_aReserved"});
	}
	
	public DeviceConfigType(){
		m_aUserFriendlyId		= new byte[64];
		m_Detector				= new DetectorType();
		m_Irradiance			= new IrradianceType();
		m_Reflectance			= new SpectrumCalibrationType();
		m_SpectrumCorrect		= new SpectrumCorrectionType();
		m_StandAlone			= new StandaloneType();
		m_Temperature			= new TempSensorType[3];
		for (int _i=0; _i<3; _i++){
			m_Temperature[_i]	= new TempSensorType();
		}
		m_TecControl			= new TecControlType();
		m_ProcessControl		= new ProcessControlType();
		m_aReserved				= new byte[13832];
	}

}
