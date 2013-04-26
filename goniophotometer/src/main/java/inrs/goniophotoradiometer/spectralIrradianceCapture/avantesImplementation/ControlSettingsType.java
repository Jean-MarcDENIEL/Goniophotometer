package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class ControlSettingsType extends Structure {

	public short	m_StrobeControl;
	public int		m_LaserDelay;
	public int		m_LaserWidth;
	public float	m_LaserWaveLength;
	public short	m_StoreToRam;
	
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {"m_StrobeControl", "m_LaserDelay", "m_LaserWidth", "m_LaserWaveLength", "m_StoreToRam"});
	}

	public ControlSettingsType(){
		m_StrobeControl		= 0;
		m_LaserDelay		= 0;
		m_LaserWidth		= 0;
		m_LaserWaveLength	= 0;
		m_StoreToRam		= 0;
	}
}
