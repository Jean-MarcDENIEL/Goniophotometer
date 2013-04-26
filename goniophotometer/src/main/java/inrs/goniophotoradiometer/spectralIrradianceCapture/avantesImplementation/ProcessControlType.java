package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class ProcessControlType extends Structure {

	public float		m_AnalogLow[];
	public float 		m_AnalogHigh[];
	public float		m_DigitalLow[];
	public float		m_DigitalHigh[];
	
	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {"m_AnalogLow", "m_AnalogHigh", "m_DigitalLow", "m_DigitalHigh"});
	}

	
	public ProcessControlType(){
		m_AnalogLow		= new float[2];
		m_AnalogHigh	= new float[2];
		m_DigitalLow	= new float[10];
		m_DigitalHigh	= new float[10];
	}
}
