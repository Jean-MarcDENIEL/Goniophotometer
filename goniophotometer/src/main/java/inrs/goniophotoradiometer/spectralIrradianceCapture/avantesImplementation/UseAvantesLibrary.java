package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;

public interface UseAvantesLibrary extends Library {
	UseAvantesLibrary INSTANCE = (UseAvantesLibrary) Native.loadLibrary("libUseAvantes", UseAvantesLibrary.class);
	
	int Use_AVS_GetNumPixels(NativeLong a_hDevice);
	
	int  Use_AVS_PrepareMeasure(	NativeLong	a_hDevice,
									short	m_StartPixel,
									short	m_StopPixel,
									
									float	m_IntegrationTime,
									int		m_IntegrationDelay,
									int		m_NrAverages,
						
									byte	dark_m_Enable,
									byte	dark_m_ForgetPercentage,
						
									short	smooth_m_SmoothPix,
									byte	smooth_m_SmoothModel,
						
									byte 	m_SaturationDetection,
						
									byte	trigger_m_Mode,
									byte	trigger_m_Source,
									byte	trigger_m_SourceType,
						
									short	control_m_StrobeControl,
									int		control_m_LaserDelay,
									int		control_m_LaserWidth,
									float	control_m_LaserWaveLength,
									short	control_m_StoreToRam);
}
