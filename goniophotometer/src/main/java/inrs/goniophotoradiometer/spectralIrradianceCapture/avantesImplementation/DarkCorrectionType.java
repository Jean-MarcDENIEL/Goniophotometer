package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class DarkCorrectionType extends Structure {

	public byte	m_Enable;
	public byte	m_ForgetPercentage;
	
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {"m_Enable", "m_ForgetPercentage"});
	}

	public DarkCorrectionType(){
		m_Enable 			= 0;
		m_ForgetPercentage	= 100;
	}
}
