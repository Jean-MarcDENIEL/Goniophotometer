package inrs.goniophotoradiometer.spectralIrradianceCapture;

import c4sci.data.HierarchicalData;

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
	
	private static final float	DEFAULT_INTEGRATION_TIME_MILLISEC = 1000f;
	
	private float[] 	wavelengthTab;
	private double[] 	irradianceTab;
	private boolean[]	saturationTab;
	private float 	integrationTimeMs;
	private int		averagesCount;
	
	@SuppressWarnings("unused")
	private SpectralIrradiance(){}
	public SpectralIrradiance(int wavelength_count){
		wavelengthTab 		= new float[wavelength_count];
		irradianceTab 		= new double[wavelength_count];
		saturationTab		= new boolean[wavelength_count];
		setIntegrationTimeMs(DEFAULT_INTEGRATION_TIME_MILLISEC);
	}
	/**
	 * Grants read/write access to wavelength data.
	 * @return wavelength data. Units depending on the user of this class. The wavelength values are sorted from lowest to highest values.
	 */
	public float[] accessWavelengthData(){
		return wavelengthTab;
	}
	/**
	 * Grants read/write access to irradiance data.
	 * @return irradiance data. Index values correspond to those of wavelength data.
	 */
	public double[] accessIrradianceData(){
		return irradianceTab;
	}
	/**
	 * Grants read/write access to saturation data. For each pixel, indicates whether or not saturation occurred during measurement.
	 * @return saturation data. Index values correspond to wavelength data. 
	 * 
	 */
	public boolean[] accessSaturationData(){
		return saturationTab;
	}
	/**
	 * 
	 * @return the number of wavelength data
	 */
	public int getWavelengthNumber(){
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
	public int getAveragesCount() {
		return averagesCount;
	}
	public void setAveragesCount(int averages_count) {
		this.averagesCount = averages_count;
	}
	public boolean isSaturated(){
		for (boolean _pixel_sat : accessSaturationData()){
			if (_pixel_sat){
				return true;
			}
		}
		return false;
	}
}
