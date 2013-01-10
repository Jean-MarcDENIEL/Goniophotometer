package inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.fileSupportImplementation;

import inrs.goniophotoradiometer.exceptions.RadiometryException;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.HierarchicalStrategy;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.MeasurementPoint;

import java.io.InputStream;
import java.io.OutputStream;

import c4sci.math.geometry.plane.PlaneVector;

/**
 * This interface describes devices that are able to load and safe their measurements data through {@link InputStream}s and {@link OutputStream}s.
 * @author jeanmarc.deniel
 *
 */
public interface FileSupportedMeasurementDevice extends HierarchicalStrategy {
	/**
	 * 
	 * @return The file names that are associated to the different parts composing the {@link MeasurementPoint}.
	 */
	String [] getMeasurementPartsNames();
	/**
	 * Performs necessary computations prior to loading {@link MeasurementPoint} parts
	 * @throws RadiometryException
	 */
	void beginLoadingSession() throws RadiometryException;
	/**
	 * Loads a {@link MeasurementPoint} part in memory
	 * @param meas_point
	 * @param part_name The file name associated t the {@link MeasurementPoint} part
	 * @param part_stream The stream used to load the {@link MeasurementPoint} part
	 * @throws RadiometryException In the cases where :
	 * <ul>
	 * <li>there is a system error</li>
	 * <li>the {@link MeasurementPoint} is not of the type (or subclass) of ones created by the {@link #createMeasurementPoint(PlaneVector)} method
	 * </ul>
	 */
	void loadMeasurementPart(MeasurementPoint meas_point, String part_name, InputStream part_stream) throws RadiometryException;
	/**
	 * Performs necessary computations after all the {@link MeasurementPoint}'s parts have been loaded.
	 * @throws RadiometryException
	 */
	void endLoadingSession() throws RadiometryException;
	/**
	 * Performs the necessary computations prior to saving the different {@link MeasurementPoint} parts.
	 * @throws RadiometryException
	 */
	void beginSavingSession() throws RadiometryException;
	/**
	 * Saves a {@link MeasurementPoint} part
	 * @param meas_point
	 * @param part_name The file name associated with the {@link MeasurementPoint} part
	 * @param part_stream The stream to save the data to
	 * @throws RadiometryException
	 */
	void saveMeasurementPart(MeasurementPoint meas_point, String part_name, OutputStream part_stream) throws RadiometryException;
	/**
	 * Performs the necessary computations after all the {@link MeasurementPoint}'s parts have been saved.
	 * @throws RadiometryException
	 */
	void endSavingSession() throws RadiometryException;
	
	/**
	 * Performs a real measurement
	 * @param meas_point The {@link MeasurementPoint} that is completely measured.
	 * @throws RadiometryException
	 */
	void performCompleteMeasurement(MeasurementPoint meas_point) throws RadiometryException;
	
}
