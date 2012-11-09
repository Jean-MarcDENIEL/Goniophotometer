package inrs.goniophotometer;

import inrs.goniophotometer.motion.EasiDecoder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;



/**
 * Parker HL80i piloting and monitoring through RS232 link.
 *
 */
@SuppressWarnings("restriction")
public class App 
{

	public static class SerialWriter implements Runnable 
    {
        OutputStream out;
        
        public SerialWriter ( OutputStream out )
        {
            this.out = out;
        }
        
        public void run ()
        {
            try
            {                
                int c = 0;
                while ( ( c = System.in.read()) > -1 )
                {
                    this.out.write(c);
                    //System.out.print(" "+c);
                }                
            }
            catch ( IOException e )
            {
                e.printStackTrace();
                System.exit(-1);
            }            
        }
    }
	
	public static void main( String[] args )
    {
        System.out.println( "Ports" );
        
        @SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier> _port_enum = CommPortIdentifier.getPortIdentifiers();
        while (_port_enum.hasMoreElements()){
        	CommPortIdentifier _port_id = _port_enum.nextElement();
        	String _name = _port_id.getName();
        	int _type = _port_id.getPortType();
        	System.out.println(_name + ": "+ _type + "= " + getPortTypeName(_type));
        	
        }
        
        try {
			CommPortIdentifier _com_id = CommPortIdentifier.getPortIdentifier("COM1");
			CommPort _com_port = _com_id.open("essai", 100);
			
			if (_com_port instanceof SerialPort){

				SerialPort _serial_com_1 = (SerialPort)_com_port;
				_serial_com_1.setSerialPortParams(9600, 
						SerialPort.DATABITS_8, 
						SerialPort.STOPBITS_1, 
						SerialPort.PARITY_NONE);
				
				EasiDecoder _decoder = new EasiDecoder(_serial_com_1.getInputStream());
				_serial_com_1.addEventListener(_decoder);
				_serial_com_1.notifyOnDataAvailable(true);
				
				OutputStream _to_com_1 = _com_port.getOutputStream();

				//int[] _msg = translateToPort("2ON\r\n2USE(1)\r\n2G");
				
				/*for (int _msg_data : _msg){
					_to_com_1.write(_msg_data);
				}*/
				System.out.println("");
				
				//byte[] _cmd = {'1','o','n'} ; //{'1','O','N'};
				//_to_com_1.write(_msg);

				
				//_to_com_1."1ON";
				(new Thread(new SerialWriter(_serial_com_1.getOutputStream()))).start();
			}
		}
        catch (NoSuchPortException _e) {
			System.out.println("No such port");
		}
        catch(Exception _ee){
        	System.out.println("Port unusuable : " + _ee.getClass().getName());
        }
        
  
    }
    
    static int[] translateToPort(String msg_str){
    	int[] _res = new int[msg_str.length()+2];
    	int _i=0;
    	for (; _i<msg_str.length(); _i++){
    		_res[_i] = (int) msg_str.charAt(_i);
    	}
    	_res[_i++] = '\r';
    	_res[_i++] = '\n';
    	return _res;
    }
  
    
    static String getPortTypeName ( int portType )
    {
        switch ( portType )
        {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }
}
