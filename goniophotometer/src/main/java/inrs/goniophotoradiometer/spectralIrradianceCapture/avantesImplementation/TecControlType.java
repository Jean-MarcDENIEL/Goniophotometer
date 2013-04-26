package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class TecControlType extends Structure {

	public byte		m_Enable;
	public float	m_Setpoint;
	public float	m_aFit[];
	
	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {"m_Enable", "m_Setpoint", "m_aFit"});
	}
	
	public TecControlType(){
		m_aFit = new float[2];
	}

}
