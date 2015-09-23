import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Main extends JFrame
{
	private static final long serialVersionUID = 4648172894076113183L;

	final Bluetooth bluetooth = new Bluetooth();
	final USBThreadInitiator USB = new USBThreadInitiator();
	final JTextField input = new JTextField("Command", 25), imageInput = new JTextField("Image URL", 25);
	final JButton sendButton = new JButton("Send"), printButton = new JButton("DRUCKEN!");
	final JButton BTConnectButton = new JButton("Connect via BT"), USBConnectButton = new JButton("Connect via USB");
	final JButton imgLoadButton = new JButton("Load Image");
	final Anzeige anzeige = new Anzeige();
	final JPanel inputPanel = new JPanel(), buttonPanel = new JPanel(), imagePanel = new JPanel();
	JLabel picLabel;
	BufferedImage coloredImage = null, blackAndWhiteImage = null;
	
	boolean useBluetooth = false;
	
	public Main() 
	{
		addWindowListener(new WindowAdapter() {});
		
		setTitle("Drucker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 970, 580);
		
		setContentPane(anzeige);
		anzeige.repaint();

		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (useBluetooth)
					try
					{
						System.out.println(bluetooth.sendMethod(input.getText()));
					}
					catch (IOException e2)
					{
						e2.printStackTrace();
					}
				else
					try
					{
						System.out.println(USB.sendMethod(input.getText()));
					}
					catch (IOException e2)
					{
						e2.printStackTrace();
					}
			}
		});
		
		BTConnectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				goBluetooth();
			}
		});
		
		USBConnectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				goUSB();
			}
		});
		
		imgLoadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				loadImage(imageInput.getText());
			}
		});
		
		printButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				print();
			}
		});
		
		inputPanel.add(input);
		inputPanel.add(sendButton);
		anzeige.add(inputPanel);
		imagePanel.add(imageInput);
		imagePanel.add(imgLoadButton);
		anzeige.add(imagePanel);
		buttonPanel.add(BTConnectButton);
		buttonPanel.add(USBConnectButton);
		anzeige.add(buttonPanel);
		anzeige.add(printButton);
		anzeige.repaint();
	}
	
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					Main frame = new Main();
					frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	public void goBluetooth()
	{
		useBluetooth = true;
		bluetooth.connect();
		anzeige.remove(BTConnectButton);
		anzeige.remove(USBConnectButton);
		anzeige.repaint();
	}
	
	public void goUSB()
	{
		USB.connect();
		anzeige.remove(BTConnectButton);
		anzeige.remove(USBConnectButton);
		anzeige.repaint();
	}
	
	public void loadImage(String imgURL)
	{
		try
		{
			anzeige.remove(picLabel);
		}
		catch (Exception e){}

		try
		{
			coloredImage = ImageIO.read(new File(imgURL));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		blackAndWhiteImage = new BufferedImage(
                coloredImage.getWidth(),
                coloredImage.getHeight(),
                BufferedImage.TYPE_BYTE_BINARY);
		for (int x = 0; x < coloredImage.getWidth(); x++)
			for (int y = 0; y < coloredImage.getHeight(); y++)
				blackAndWhiteImage.setRGB(x, y, coloredImage.getRGB(x, y));
		
		picLabel = new JLabel(new ImageIcon(blackAndWhiteImage.getScaledInstance(100, Math.round((float)100 / (float)blackAndWhiteImage.getWidth() * (float)blackAndWhiteImage.getHeight()), 0)));
		anzeige.add(picLabel);
		anzeige.repaint();
		System.out.println(imgURL);
	}
	
	public void print()
	{
		if (blackAndWhiteImage.getHeight() > 300 || blackAndWhiteImage.getWidth() > 150) return;
		
		for (int y = 0; y < blackAndWhiteImage.getHeight(); y++)
		{
			for (int x = 0; x < blackAndWhiteImage.getWidth(); x++)
			{
				int color = blackAndWhiteImage.getRGB(x, y);
				int red = (color & 0x00ff0000) >> 16;
				int green = (color & 0x0000ff00) >> 8;
				int blue = (color & 0x000000ff);
				int total = red + green + blue;
				
				
				if (total == 0)
				{
					if (useBluetooth)
					{
						try
						{
							System.out.print("Attempting to send method: \"mover.XY(" + x*10 + "," + y*5 + ");\" - ");
							System.out.println("Client responded with: " + bluetooth.sendMethod("mover.XY(" + x*10 + "," + y*5 + ");"));
						}
						catch (IOException e)
						{
							System.out.println("Encountered Exception:");
							e.printStackTrace();
						}
						
						try
						{
							System.out.print("Attempting to send method: \"mover.s()\" - ");
							System.out.println("Client responded with: " + bluetooth.sendMethod("mover.s();"));
						}
						catch (IOException e)
						{
							System.out.println("Encountered Exception:");
							e.printStackTrace();
						}
					}
					else
					{
						try
						{
							System.out.print("Attempting to send method: \"mover.XY(" + x*10 + "," + y*5 + ");\" - ");
							System.out.println("Client responded with: " + USB.sendMethod("mover.XY(" + x*10 + "," + y*5 + ");"));
						}
						catch (IOException e)
						{
							System.out.println("Encountered Exception:");
							e.printStackTrace();
						}
						
						try
						{
							System.out.print("Attempting to send method: \"mover.s()\" - ");
							System.out.println("Client responded with: " + USB.sendMethod("mover.s();"));
						}
						catch (IOException e)
						{
							System.out.println("Encountered Exception:");
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
