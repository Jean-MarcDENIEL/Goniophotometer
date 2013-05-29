package inrs.goniophotoradiometer.spectralIrradianceCapture;

import inrs.goniophotoradiometer.exceptions.RadiometryException;

/**
 * This interface represents spectroradiometers.
 * @author jeanmarc.deniel
 *
 */
public interface SpectroRadiometer {
	/**
	 * 
	 * @return
	 * @throws RadiometryException in the case the measurement is not possible.
	 */
	SpectralIrradiance captureIrradiance() throws RadiometryException;
	/**
	 * 
	 * @return The number of wavelength-dependant irradiance values (W/nm/m²) to represent irradiance
	 * @throws RadiometryException
	 */
	int getWavelengthCount() throws RadiometryException;
}
