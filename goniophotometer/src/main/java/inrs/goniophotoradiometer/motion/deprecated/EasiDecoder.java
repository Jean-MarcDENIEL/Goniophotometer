package inrs.goniophotoradiometer.motion.deprecated;

import java.io.IOException;
import java.io.InputStream;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 * This class is aimed at decoding and representing the state of a motion engine.
 * @author jeanmarc.deniel
 *
 */
@SuppressWarnings("restriction")
public class EasiDecoder implements SerialPortEventListener{

	private static final int 	STATE_NB = 2; 
	private int 				currentState;
	private StringBuffer 		stringTab[];
	private InputStream			decodedStream;


	public EasiDecoder(InputStream decoded_stream){
		currentState = 0;
		stringTab = new StringBuffer[STATE_NB];
		for (int _i=0; _i<STATE_NB; _i++){
			stringTab[_i] = new StringBuffer();
		}
		decodedStream = decoded_stream;
	}

	public synchronized String getStateAndMeaning(){
		if (stringTab[currentState].charAt(0)=='*'){
			return stringTab[currentState].toString() + " = " + decodeState(stringTab[currentState].toString()); //= " : " + Integer.toString(stateTab[currentState], 2);
		}
		else{
			return stringTab[currentState].toString();
		}
	}

	public static String decodeState(String decoded_string){
		StringBuffer _res = new StringBuffer();

		/*String _status_bit_table[] = 
			{"Command Processing paused",
				"Looping",
				"Wait for trigger",
				"Running program",
				"Going home",
				"Waiting for delay timeout",
				"Registration in progress",
				"Motor energised",
				"Event triggered",
				"Input in LSEL not matching label",
				"-ve Limit seen during last move",
				"+ve limit seen during last move",
				"Moving (in motion)",
				"Stationary (in position)",
				"No registration signal seen in registration window",
				"Cannot stop within the defined registration distance"
			};*/

		String _user_fault_bit_table[] ={
				"Value is out of range",
				"Incorrect command syntax",
				"Last label already in use",
				"Label of this name is not defined",
				"Missing Z pulse when homing",
				"Homing failed - no signal detected",
				"Home signal too narrow",
				"Drive de-energized",
				"Cannot realte END statement to a label",
				"Program memory buffer full",
				"No more motion profiles available",
				"No more sequences labvel available",
				"End of travel limit hit",
				"Still moving",
				"Deceleration error",
				"Transmit buffer overflow",
				"User program nesting overflow",
				"Cannot use an undefined profile",
				"Drive not ready",
				"Save error",
				"Command not supported by this product",
				"Reserved"
		};

		int _current_bit = 0;
		for (int _i=0; (_i<decoded_string.length())&&(_current_bit<_user_fault_bit_table.length); _i++){
			if (decoded_string.charAt(_i) == '1'){
				_res.append(_user_fault_bit_table[_current_bit]);
				_res.append(" \n");
			}
			_current_bit ++;
		}
		return _res.toString();
	}


	private synchronized void switchState(){
		currentState = (currentState+1)%STATE_NB;
	}


	@SuppressWarnings("restriction")
	public void serialEvent(SerialPortEvent e_v) {
		stringTab[currentState].setLength(0);
		try{
			int _read_data = decodedStream.read();
			while ((_read_data > -1)&&
					(_read_data != '\n')){
				stringTab[this.currentState].append((char)_read_data);
				_read_data = decodedStream.read();
			}
			switchState();
		}
		catch(IOException _e){
			_e.printStackTrace();
		}

	}

};