package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class TriggerType extends Structure {

	public byte	m_Mode;
	public byte	m_Source;
	public byte	m_SourceType;
	
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {"m_Mode", "m_Source", "m_SourceType"});
	}

	public TriggerType(){
		m_Mode			= 0;
		m_Source		= 1;
		m_SourceType	= 1;
	}
}
