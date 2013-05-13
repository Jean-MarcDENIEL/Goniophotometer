package inrs.goniophotoradiometer.spectralIrradianceCapture;

import c4sci.data.HierarchicalData;
import c4sci.math.algebra.Floatings;

/**
 * This class represents spectral irradiance measurements. This data is composed of index corresponding :
 * <ol>
 * 	<li> wavelength data</li>
 * 	<li> irradiance data</li>
 * </ol>
 * @author jeanmarc.deniel
 *
 */
public class SpectralIrradiance extends HierarchicalData {
	private float[] wavelengthTab;
	private double[] irradianceTab;
	private float 	integrationTimeMs;
	
	@SuppressWarnings("unused")
	private SpectralIrradiance(){}
	public SpectralIrradiance(int wavelength_count){
		wavelengthTab 		= new float[wavelength_count];
		irradianceTab 		= new double[wavelength_count];
		setIntegrationTimeMs(1f);
	}
	/**
	 * Grants read/write access to wavelength data.
	 * @return wavelength data. Units depending on the user of this class. The wavelength values are sorted from lowest to highest values.
	 */
	public float[] getWavelengthData(){
		return wavelengthTab;
	}
	/**
	 * Grants read/write access to irradiance data.
	 * @return irradiance data. Index values correspond to those of wavelength data.
	 */
	public double[] getIrradianceData(){
		return irradianceTab;
	}
	/**
	 * 
	 * @return the number of wavelength data
	 */
	public int getDataSize(){
		return wavelengthTab.length;
	}
	public final float getIntegrationTimeMs() {
		return integrationTimeMs;
	}
	/**
	 * 
	 * @param integration_time_ms The integration time in milliseconds.
	 */
	public final void setIntegrationTimeMs(float integration_time_ms) {
		this.integrationTimeMs = integration_time_ms;
	}
	
	public double getMaxIrradianceValue(){
		double _res = irradianceTab[0];
		for (double _irr : irradianceTab){
			_res = Math.max(_res, _irr);
		}
		return _res;
	}
}
