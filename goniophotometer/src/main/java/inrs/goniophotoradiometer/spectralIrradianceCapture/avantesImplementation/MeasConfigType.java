package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class MeasConfigType extends Structure {

	public short 				m_StartPixel;
	public short 				m_StopPixel;
	public float				m_IntegrationTime;
	public int					m_IntegrationDelay;
	
	public int					m_NrAverages;
	public DarkCorrectionType	m_CorDynDark;
	public SmoothingType		m_Smoothing;
	public byte					m_SaturationDetection;
	
	public TriggerType			m_Trigger;
	public ControlSettingsType	m_Control;
	
	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {
				"m_StartPixel", "m_StopPixel", 
				"m_IntegrationTime", "m_IntegrationDelay", 
				"m_NrAverages", "m_CorDynDark", "m_Smoothing", "m_SaturationDetection", 
				"m_Trigger", "m_Control"});
	}

	public MeasConfigType(){
		super(Structure.ALIGN_NONE);
		m_CorDynDark 	= new DarkCorrectionType();
		m_Smoothing 	= new SmoothingType();
		m_Trigger		= new TriggerType();
		m_Control		= new ControlSettingsType();
	}
}
