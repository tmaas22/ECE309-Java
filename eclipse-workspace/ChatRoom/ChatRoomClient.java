// Author: Thomas Matrejek

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

import javax.swing.*;

public class ChatRoomClient implements ActionListener, Runnable
{
	Socket s;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	
// Initialize our GUI
	JFrame chatWindow = new JFrame();
	JButton sendToAllButton = new JButton("SEND TO ALL");
	
	// Declare our labels
	JLabel inChatLabel = new JLabel("Enter a chat message to send here.");
	JLabel outChatLabel = new JLabel("Received chat messages displayed here.");
	JLabel blankLabel  = new JLabel("");
	
	// Declare our panels
	JPanel topPanel = new JPanel();
	JPanel bottomPanel = new JPanel();
	
	// Declare our text areas with scrolls
	JTextArea inChatTextArea = new JTextArea();
	JTextArea outChatTextArea = new JTextArea();
	
	JScrollPane inChatScrollPane  = new JScrollPane(inChatTextArea);
	JScrollPane outChatScrollPane = new JScrollPane(outChatTextArea);
	
	
	public static void main(String[] args) throws Exception
		{
			System.out.println("\n+======+==================+\n| Name | Thomas Matrejek  |\n+======+==================+\n");
			if (args.length != 3)
				{
					System.out.println("Must pass in the following paramaters:\n  param1 - User's chat name\n  param2 - User's password\n  param3 - Network address of server");
					return;
				}
			new ChatRoomClient(args[0], args[1], args[2]);

		}
	
	public ChatRoomClient(String chatName, String password, String serverAddress) throws Exception
		{
			// Connect to serverAddress and set up Streams
			
			System.out.println("Connecting to " + serverAddress);
			s = new Socket(serverAddress, 2222);
			System.out.println("Connected to the server!");
			
			oos = new ObjectOutputStream(s.getOutputStream());
			System.out.println("Join " + chatName + " to the chat room.");
			oos.writeObject(chatName + " " + password);
			
			ois = new ObjectInputStream(s.getInputStream());
			String serverReply = (String) ois.readObject();
			System.out.println("Server reply to join request is: " + serverReply);
			
			if (!serverReply.startsWith("Welcome"))
				{
					throw new IllegalArgumentException(serverReply);
				}
			
			
			// Initialize the GUI Window
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			chatWindow.setTitle(chatName + "'s Chat Window! (Close window to leave the Chat Room.)");
			chatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			chatWindow.setSize(800,500);
			chatWindow.setVisible(true);
			
			
			
			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,inChatScrollPane, outChatScrollPane);
			
			
			// Display our labels in the top panel.
			chatWindow.getContentPane().add(topPanel, "North");
			topPanel.setLayout(new GridLayout(1,2));	
			topPanel.add(inChatLabel);
			topPanel.add(outChatLabel);

			inChatLabel.setHorizontalAlignment(JLabel.CENTER);
			outChatLabel.setHorizontalAlignment(JLabel.CENTER);
			
			
			// Display the button in the bottom panel
			chatWindow.getContentPane().add(bottomPanel, "South");
			bottomPanel.setLayout(new GridLayout(1,2));
			bottomPanel.add(sendToAllButton);
			bottomPanel.add(blankLabel);
			sendToAllButton.setBackground(Color.green);
			sendToAllButton.addActionListener(this);
			
			
			chatWindow.getContentPane().add(splitPane, "Center");
			splitPane.setDividerLocation(150);
					
			inChatTextArea.setFont(new Font("default",Font.BOLD,20));
			outChatTextArea.setFont(new Font("default",Font.BOLD,20));
			outChatTextArea.setEditable(false);
			inChatTextArea.setLineWrap(true);
			outChatTextArea.setLineWrap(true);
			inChatTextArea.setWrapStyleWord(true);
			outChatTextArea.setWrapStyleWord(true);
			
			
			Thread t = new Thread(this);
			t.start();
			
		}

	public void run()
		{
			// create a generic NewLine separator for output
			String newLine = System.lineSeparator();
			
			// Safety check on our ObjectInputStream
			if (ois == null) 
				{
					System.out.println("PROGRAM ERROR in run(): ois pointer not initialized!");
					return;
				}
			
			// Atempt to receive objects from the server
			try
				{
					while(true)
						{
							String messageFromServer = (String) ois.readObject();
							System.out.println("Received from server: " + messageFromServer);
							outChatTextArea.append(messageFromServer + newLine);
							outChatTextArea.setCaretPosition(outChatTextArea.getDocument().getLength());
						}
				}
			catch(Exception e)
				{
					outChatTextArea.append("ERROR RECEIVING CHAT FROM THE CHAT SERVER. "
							+ "\nCLOSE THIS WINDOW AND RESTART ChatRoomClient to REJOIN THE CHAT ROOM.");
					inChatTextArea.setEditable(false);
					sendToAllButton.setBackground(Color.red);
				}

		}

	public void actionPerformed(ActionEvent arg0)
		{
			// Check for correct initialization of GLOBAL variables
			if (oos == null)
				{
					System.out.println("PROGRAM ERROR in actionPerformed(): oos pointer not initialized!");
					return;
				}

			System.out.println("sendToAllButton was pushed.");
			String chatMessage = inChatTextArea.getText().trim();
			
			if (chatMessage.length() == 0) return;
			
			// Attempt to send message to server
			try
				{
					oos.writeObject(chatMessage);
					inChatTextArea.setText("");
					System.out.println("sending entered message \"" + chatMessage + "\" to the server. ");
				}
			catch(Exception e)
				{
					outChatTextArea.append("ERROR SENDING CHAT TO THE CHAT SERVER."
							+ "\nCLOSE THIS WINDOW AND RESTART ChatRoomCLient TO REJOIN THE CHAT ROOM.");
					inChatTextArea.setEditable(false);
					sendToAllButton.setBackground(Color.red);
				}
		}



}
