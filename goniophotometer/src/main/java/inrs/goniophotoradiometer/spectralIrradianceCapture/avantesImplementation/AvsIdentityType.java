package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class AvsIdentityType extends Structure {

	public byte		m_aSerial[] 		= new byte[10];
	public byte		m_aUserFriendlyId[]	= new byte[64];
	public byte		m_Status;
	
	
	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {"m_aSerial", "m_aUserFriendlyId", "m_Status"});
	}

	public static final int		STATUS_UNKNOWN					= 0;
	public static final int		STATUS_AVAILABLE				= 1;
	public static final int		STATUS_IN_USE_BY_APPLICATION	= 2;
	public static final int		STATUS_IN_USE_BY_OTHER			= 3;
}
