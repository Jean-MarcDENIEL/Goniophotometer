package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class IrradianceType extends Structure {

	public SpectrumCalibrationType	m_IntensityCalib;
	public byte						m_CalibrationType;
	public int						m_FiberDiameter;
	
	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {"m_IntensityCalib", "m_CalibrationType", "m_FiberDiameter"});
	}
	
	public IrradianceType(){
		super(Structure.ALIGN_NONE);
		m_IntensityCalib = new SpectrumCalibrationType();
	}

}
