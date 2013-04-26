package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class StandaloneType extends Structure {

	public byte				m_Enable;
	public MeasConfigType	m_Meas;
	public short			m_Nmsr;
	public SDCardType		m_SDCard;
	
	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {"m_Enable", "m_Meas", "m_Nmsr", "m_SDCard"});
	}
	
	public StandaloneType(){
		m_Meas		= new MeasConfigType();
		m_SDCard	= new SDCardType();
	}

}
