/**
 * @author Caleb Lanphere
 * 
 * TextMe Application server application
 * 
 * Copyright 2024 | Caleb Lanphere | All Rights Reserved
 * 
 */

package appTextMe;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class appUIS extends JFrame implements WindowListener {
	
	static WindowListener ui;
	static netCommServer netS = new netCommServer();
	static JFrame base;
	
	/**
	 * Main, creates application for server to run under
	 */
	public static void main(String[] args) {
		base = new JFrame("Server");
		base.setSize(new Dimension(5,5));
		base.addWindowListener(ui);
		base.setVisible(true);
		
		netS.startServer();
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		netS.sendMessageNet("Closing Server"); // Sends message to users connected that server is closing
		
		netS.closeServer(); // Closes socket and all associated variables
		
		System.exit(0); // Closes application
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
