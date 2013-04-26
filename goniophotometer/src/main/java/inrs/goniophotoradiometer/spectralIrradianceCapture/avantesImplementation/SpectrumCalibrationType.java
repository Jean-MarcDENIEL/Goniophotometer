package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class SpectrumCalibrationType extends Structure {

	public SmoothingType	m_Smoothing;
	public float			m_CalInTime;
	public float			m_aCalibConvers[];
	
	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {
				"m_Smoothing", "m_CalInTime", "m_aCalibConvers"});
	}
	
	public SpectrumCalibrationType(){
		m_Smoothing 	= new SmoothingType();
		m_aCalibConvers	= new float[4096];
	}

}
