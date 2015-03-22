/**
 * The MIT License (MIT)
Copyright (c) 2015 Saqib Nizam Shamsi
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class Main {
	
	private JButton[] centralEvents, branchEvents, preference;
	private int category;
	private String id;
	private JFrame frame;
	private JPanel getId, voting;
	private JTextField field;
	private Connection connection;
	private boolean[] voted;
	private JPasswordField passwordField;
	
	Main() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, SQLException
	{
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		frame = new JFrame("Colosseum 2015");
		/*String iconFilePath = new File("").getAbsolutePath();
		iconFilePath += "//res//icon.png";*/
		URL url = Main.class.getResource("icon.png");
		/*String iconFilePath = url.toString();
		System.out.println(iconFilePath);*/
		frame.setIconImage(new ImageIcon(url).getImage());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		connection = DBManager.getConnection();
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, SQLException
	{
		new Main().passwordScreen();
	}
	
	public void passwordScreen()
	{
		JPanel pwd = new JPanel(new GridBagLayout());
		pwd.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		passwordField = new JPasswordField(15);
		passwordField.addActionListener(new ValidatePassword());
		JLabel label = new JLabel("Password: ");
		JButton login = new JButton("Log In");
		login.addActionListener(new ValidatePassword());
		JButton change = new JButton("Change Password");
		change.addActionListener(new ChangePassword());
		
		JPanel container = new JPanel(new FlowLayout());
		container.add(label);
		container.add(passwordField);
		container.add(login);
		//container.add(change);
		
		pwd.add(container);
		frame.getContentPane().add(BorderLayout.CENTER, pwd);
		frame.setBounds(300, 100, 680, 550);
		frame.setVisible(true);
	}
	
	public void start()
	{
		JLabel label = new JLabel("ID: ");
		getId = new JPanel(new GridBagLayout());
		getId.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		field = new JTextField(15);
		field.addActionListener(new GetId());
		JButton button = new JButton("OK");
		button.addActionListener(new GetId());
		JPanel container = new JPanel(new FlowLayout());
		container.add(label);
		container.add(field);
		container.add(button);
		getId.add(container);
		
	}
	
	public void vote() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{		
		voting = new JPanel(new BorderLayout());
		voting.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JTabbedPane tabsThree = new JTabbedPane();
		tabsThree.setFocusable(false);
		
		JTabbedPane tabsTwo = new JTabbedPane();
		tabsTwo.setFocusable(false);
		
		EventInfo branch = new EventInfo(Files.getNAME_OF_THE_FILE_CONTAINING_NAMES_OF_BRANCH_EVENTS());
		EventInfo central = new EventInfo(Files.getNAME_OF_THE_FILE_CONTAINING_NAMES_OF_CENTRAL_EVENTS());
		
		centralEvents = new JButton[central.getNoOfEvents()];
		branchEvents = new JButton[branch.getNoOfEvents()];
		int total = branch.getNoOfEvents() + central.getNoOfEvents();
		preference = new JButton[total];
		String[] allEvents = new String[total];
		
		int idx = 0;
		String[] names;
		names = central.getEventNames();
		for(int i = 0; names[i] != null; i++, idx++)
			allEvents[idx] = names[i];
		names = branch.getEventNames();
		for(int i = 0; names[i] != null; i++, idx++)
			allEvents[idx] = names[i];
		
		/*Create the Panels corresponding to Events*/
		JPanel centralPanel = getPanel(central.getEventNames(), central.getNoOfEvents(), centralEvents, new VoteCentral(), !voted[0]);
		JPanel branchPanel = getPanel(branch.getEventNames(), branch.getNoOfEvents(), branchEvents, new VoteBranch(), !voted[1]);
		branchPanel.setSize(400, 400);
		
		if(category == 1)
		{
			tabsTwo.addTab("Central Events", centralPanel);
			tabsTwo.addTab("Branch Events", branchPanel);
			voting.add(tabsTwo);
		}
		else
		{
			JPanel preferencePanel = getPanel(allEvents, total, preference, new VotePreference(), !voted[2]);
			tabsThree.addTab("Central Events", centralPanel);			
			tabsThree.addTab("Branch Events", branchPanel);			
			tabsThree.addTab("Preference", preferencePanel);
			voting.add(tabsThree);
		}
		
		frame.getContentPane().removeAll();
		frame.getContentPane().add(voting);
		frame.repaint();
		frame.setVisible(true);
	}
	
	public JPanel getPanel(String[] eventNames, int noOfEvents, JButton[] buttons, ActionListener actionListener, boolean enabled)
	{
		int gridSize;
		gridSize = (int)Math.ceil(Math.sqrt(noOfEvents));
		GridLayout g = new GridLayout(gridSize, gridSize);
		JPanel panel = new JPanel(g);
		for(int i = 0; i < noOfEvents; i++)
		{
			JButton b = new JButton(eventNames[i]);
			b.setFocusable(false);
			b.addActionListener(actionListener);
			b.setEnabled(enabled);
			panel.add(b);
			buttons[i] = b;
		}
		return panel;
	}
	
	private void setFrameBackground(JPanel background)
	{
		frame.getContentPane().removeAll();
		frame.getContentPane().add(background);
		frame.repaint();
		frame.setVisible(true);
	}
	
	public class GetId implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			id = field.getText();
			id = id.toUpperCase();
			///System.out.println(id);
			try {
				if(id != null && id.length() > 0 && DBManager.idExists(connection, id))
				{
					String tmp = Utilities.getTableName(id);
					if(tmp.equals("college"))
						category = 1;
					else
						category = 2;
					voted = DBManager.hasVoted(connection, id, category);
					System.out.println(Arrays.toString(voted));
					if(Utilities.all(voted))
					{
						JOptionPane.showMessageDialog(frame, "Your Vote Has Already Been Cast", "Error", JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						try {
							vote();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnsupportedLookAndFeelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else
				{
					JOptionPane.showMessageDialog(frame, "Invalid ID", "Error", JOptionPane.ERROR_MESSAGE);
				}
			} catch (HeadlessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public class VoteCentral implements ActionListener
	{

		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("I came in VoteCentral");
			String name = arg0.getActionCommand();
			//System.out.println(name);
			try {
				int eventId = DBManager.getEventId(connection, category, name, "centralevents");
				//System.out.println(eventId);
				DBManager.vote(connection, id, 1, eventId, category);
				for(JButton b : centralEvents)
					b.setEnabled(false);
				voted = DBManager.hasVoted(connection, id, category);
				if(Utilities.all(voted))
				{
					JOptionPane.showMessageDialog(frame, "Votes Cast Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
					setFrameBackground(getId);
					field.setText("");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public class VoteBranch implements ActionListener
	{

		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("I came in VoteBranch");
			String name = arg0.getActionCommand();
			//System.out.println(name);
			try {
				int eventId = DBManager.getEventId(connection, category, name, "branchevents");
				//System.out.println(eventId);
				DBManager.vote(connection, id, 2, eventId, category);
				for(JButton b : branchEvents)
					b.setEnabled(false);
				voted = DBManager.hasVoted(connection, id, category);
				if(Utilities.all(voted))
				{
					JOptionPane.showMessageDialog(frame, "Votes Cast Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
					setFrameBackground(getId);
					field.setText("");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public class VotePreference implements ActionListener
	{

		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("I came in VotePreference");
			String name = arg0.getActionCommand();
			//System.out.println(name);
			try {
				int eventId = DBManager.getEventId(connection, category, name, "allevents");
				//System.out.println(eventId);
				DBManager.vote(connection, id, 3, eventId, category);
				for(JButton b : preference)
					b.setEnabled(false);
				voted = DBManager.hasVoted(connection, id, category);
				if(Utilities.all(voted))
				{
					JOptionPane.showMessageDialog(frame, "Votes Cast Successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
					setFrameBackground(getId);
					field.setText("");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public class ValidatePassword implements ActionListener	
	{

		public void actionPerformed(ActionEvent arg0) {
			char[] password = passwordField.getPassword();
			passwordField.setText("");
			try {
				if(Utilities.validatePassword(new String(password)))
				{
					start();
					setFrameBackground(getId);
				}
				else
					JOptionPane.showMessageDialog(frame, "Invalid Password", "Error", JOptionPane.ERROR_MESSAGE);
			} catch (HeadlessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public class ChangePassword implements ActionListener
	{

		public void actionPerformed(ActionEvent arg0) {
			JPanel panel = new JPanel();
			JLabel label = new JLabel("Old Password:");
			JPasswordField pass = new JPasswordField(10);
			panel.add(label);
			panel.add(pass);
			String[] options = new String[]{"OK", "Cancel"};
			int option = JOptionPane.showOptionDialog(frame, panel, "Change Password",
			                         JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
			                         null, options, options[1]);
			if(option == 0) // pressing OK button
			{
			    char[] password = pass.getPassword();
			    try {
					if(!Utilities.validatePassword(new String(password)))
						JOptionPane.showMessageDialog(frame, "Invalid Password", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (HeadlessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				JPanel panel2 = new JPanel();
				JLabel label2 = new JLabel("New Password:");
				JPasswordField passs = new JPasswordField(10);
				panel2.add(label2);
				panel2.add(passs);
				String[] optionss = new String[]{"OK", "Cancel"};
				int option2 = JOptionPane.showOptionDialog(frame, panel, "Change Password",
				                         JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
				                         null, options, options[1]);
				if(option2 == 0)
				{
					char[] newpassword = passs.getPassword();
					JPanel panel3 = new JPanel();
					JLabel label3 = new JLabel("Confirm New Password:");
					JPasswordField passss = new JPasswordField(10);
					panel3.add(label3);
					panel3.add(passs);
					String[] optionsss = new String[]{"OK", "Cancel"};
					int option3 = JOptionPane.showOptionDialog(frame, panel, "Change Password",
					                         JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
					                         null, options, options[1]);
					if(option3 == 0)
					{
						char[] confirm = passss.getPassword();
						if(new String(newpassword).equals(new String(confirm)))
							try {
								Utilities.setPassword(new String(newpassword));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						else
							JOptionPane.showMessageDialog(frame, "Passwords Do Not Match", "Error", JOptionPane.ERROR_MESSAGE);
						
					}
					
				}
			}
		}
		
	}
}
