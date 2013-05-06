package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class SDCardType extends Structure {

	public byte				m_Enable;
	public byte				m_SpectrumType;
	public byte				m_aFileRootName[];
	public TimeStampType	m_TimeStamp; 
	
	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {"m_Enable", "m_SpectrumType", "m_aFileRootName", "m_TimeStamp"});
	}

	public SDCardType(){
		super(Structure.ALIGN_NONE);
		m_aFileRootName = new byte[6];
		m_TimeStamp		= new TimeStampType();
	}
}
