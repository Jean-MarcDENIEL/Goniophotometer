package inrs.goniophotometer.imageCapture.xcd90Implementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class ZCL_COLORVALUE extends Structure {
	public double a;
	public double b;
	public double c;
	public double d;
	
	@SuppressWarnings("rawtypes")
	@Override
	protected List getFieldOrder() {
		return Arrays.asList(new String[] {"a", "b", "c", "d"});
	}

}
