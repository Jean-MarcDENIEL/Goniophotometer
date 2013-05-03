package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;

public interface UseAvantesLibrary extends Library {
	UseAvantesLibrary INSTANCE = (UseAvantesLibrary) Native.loadLibrary("libUseAvantes", UseAvantesLibrary.class);
	
	int Use_AVS_GetNumPixels(long a_hDevice);
}
