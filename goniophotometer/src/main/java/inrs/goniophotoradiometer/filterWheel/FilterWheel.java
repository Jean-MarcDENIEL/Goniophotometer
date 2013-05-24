package inrs.goniophotoradiometer.filterWheel;

import inrs.goniophotoradiometer.exceptions.RadiometryException;

public interface FilterWheel {
	/**
	 * 
	 * @return The max number of filters that can be supported
	 */
	int getMaxFilterCount() throws RadiometryException;
	
	/**
	 * Position filter. Waits for the positioning to end before returning.
	 * @param filter_index The filter index, ranging from 1 to {@link #getMaxFilterCount()}
	 * @throws RadiometryException 
	 */
	void positionFilter(int filter_index) throws RadiometryException;
}
