import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lejos.pc.comm.NXTConnector;

public class USBThreadInitiator
{
	NXTConnector conn;
	OutputStream dos;
	InputStream dis;
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
		conn = new NXTConnector();
		System.out.println("Trying to connect to NXT via USB...");
		
		if (!conn.connectTo("usb://"))
		{
			System.out.println("No NXT found using USB. Programm wird beendet...");
			System.exit(1);
		}
		
		System.out.println("Connected to NXT via USB!");
		dis = conn.getInputStream();
		dos = conn.getOutputStream();
		
		return false;
	}
}
