package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class SmoothingType extends Structure {

	public short	m_SmoothPix;
	public byte		m_SmoothModel;
	
	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {"m_SmoothPix", "m_SmoothModel"});
	}

	public SmoothingType(){
		m_SmoothPix		= 0;
		m_SmoothModel	= 0;
	}
}
