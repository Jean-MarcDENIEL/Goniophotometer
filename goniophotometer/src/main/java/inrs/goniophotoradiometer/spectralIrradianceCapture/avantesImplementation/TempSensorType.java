package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class TempSensorType extends Structure {

	public float 	m_aFit[];
	
	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {"m_aFit"});
	}
	
	public TempSensorType(){
		super(Structure.ALIGN_NONE);
		m_aFit = new float[5];
	}

}
