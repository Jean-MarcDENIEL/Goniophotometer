package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class DetectorType extends Structure {

	public byte		m_SensorType;
	public short	m_NrPixels;
	public float	m_aFit[];
	public byte		m_NLEnable;
	public double	m_aNLCorrect[];
	public double	m_aLowNLCounts;
	public double	m_aHighNLCounts;
	public float	m_Gain[];
	public float	m_Reserved;
	public float	m_Offset[];
	public float	m_ExtOffset;
	public short	m_DefectivePixels[];
	
	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {
				"m_SensorType", "m_NrPixels", 
				"m_aFit", "m_NLEnable", 
				"m_aNLCorrect", "m_aLowNLCounts", "m_aHighNLCounts", "m_Gain", 
				"m_Reserved", "m_Offset", "m_ExtOffset", "m_DefectivePixels"});
	}
	
	public DetectorType(){
		m_aFit 				= new float[5];
		m_aNLCorrect		= new double[8];
		m_Gain				= new float[2];
		m_Offset			= new float[2];
		m_DefectivePixels	= new short[30];
	}

}
