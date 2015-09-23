import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTConnector;

class Bluetooth
{
	DataOutputStream dos;
	DataInputStream dis;
	public boolean connected;
	private final String roboName = "Karl";
	private String returnval;
	
	public String sendMethod(String method) throws IOException
	{
		returnval = "";
		for (char c : method.toCharArray())
		{
			dos.write((int)c);
			dos.flush();
		}
		
		char c = ' ';
		while ((c = (char)dis.read()) != ';')
			returnval += c;
		
		return returnval;
	}
	
	public boolean connect()
	{
		NXTConnector conn = new NXTConnector();
		boolean connected = conn.connectTo("btspp://" + roboName);
		if (!connected)
		{
			System.err.println("Failed to connect to " + roboName);
			System.exit(1);
		}
		this.connected = true;
		dos = new DataOutputStream(conn.getOutputStream());
		dis = new DataInputStream(conn.getInputStream());
		return true;
	}
}