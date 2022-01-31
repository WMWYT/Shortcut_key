package src.rxtx;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

public class port_operation{
    public static OutputStream out;
    public static ArrayList<String> array;
    static CommPortIdentifier portIdentifier;

    public port_operation(){
        super();
    }

    public static void port_scanner(){
	    Enumeration<?> enumComm;
        array = new ArrayList<>();

	    enumComm = CommPortIdentifier.getPortIdentifiers();
        
        while (enumComm.hasMoreElements()) {
            portIdentifier = (CommPortIdentifier) enumComm.nextElement();
	     	if(portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                array.add(portIdentifier.getName());
	    	}
	    }
    }
    
    void connect ( String portName ) throws Exception{
        portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);

                //InputStream in = serialPort.getInputStream();       //输出单片机的消息
                out = serialPort.getOutputStream();    //向单片机输入消息
                
                //(new Thread(new SerialReader(in))).start();
                //(new Thread(new SerialWriter(out))).start();
            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }
    
    /** */
    public static class SerialReader implements Runnable{
        InputStream in;
        
        public SerialReader ( InputStream in )
        {
            this.in = in;
        }
        
        public void run ()
        {
            byte[] buffer = new byte[1024];
            int len = -1;
            try
            {
                while ( ( len = this.in.read(buffer)) > -1 )
                {
                    System.out.print(new String(buffer,0,len));
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }

    /** 向单片机发送数据*/
    public static class SerialWriter implements Runnable{
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
                }
                
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }
    
    public static void read_port(String Port){
        try
        {
            (new port_operation()).connect(Port);
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }
}