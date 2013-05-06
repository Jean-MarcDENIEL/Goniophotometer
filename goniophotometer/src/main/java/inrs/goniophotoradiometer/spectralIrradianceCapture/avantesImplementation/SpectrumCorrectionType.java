package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class SpectrumCorrectionType extends Structure {

	public float	m_aSpectrumCorrect[];
	
	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {"m_aSpectrumCorrect"});
	}
	
	public SpectrumCorrectionType(){
		super(Structure.ALIGN_NONE);
		m_aSpectrumCorrect = new float[4096];
	}

}
