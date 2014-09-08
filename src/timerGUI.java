import java.awt.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import javax.swing.JLabel;
import javax.swing.JFormattedTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;


public class timerGUI extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int POS = 6;
	public static final int SEGMENTS = 7;
	public static final int SEGMENT_NUMBER = 7;
	public static final int maxDuration = 120;
	
	protected static final int FACTOR = 1000;
	protected static final int START = 0;
	protected static final int STOP = 1;
	protected static final int PREPARE = 2;
	protected static final int FIVELEFT = 3;
	
	private static Timer mainTimer = new Timer(); 
	private static Timer gameTimer = new Timer(); 
	
	protected static final int JSTART = 0;
	protected static final int JSTOP = 1;
	protected static final int JPREPARE = 2;
	protected static final int JFIVELEFT = 3;
	
//	public static int[] init = new int[]{1, 2, 3, 4, 5, 8};
	public static int[] init = new int[]{0, 0, 0, 0, 0, 0};

	private static display timeDisplay = new display(init);
	private static display alarmDisplay = new display(init);
	
	private static Polygon[][] segmentsT = new Polygon[POS][SEGMENT_NUMBER];
	private static Polygon[][] segmentsA = new Polygon[POS][SEGMENT_NUMBER];

	public static int dist = 300;
	
	private static JTextArea textAreaSched = new JTextArea();
	private static JTextArea textAreaStart = new JTextArea();
	private static JTextArea textAreaPrepare = new JTextArea();
	private static JTextArea textArea5Min = new JTextArea();
	private static JTextArea textAreaStop = new JTextArea();


	private final JFileChooser fc = new JFileChooser("sounds");
		
	public static timerGUI frame = new timerGUI();                  // window for drawing
	public static JFrame application = new JFrame();                  // the program itself	
	private static final ArrayList<myGame> gameSchedule = new ArrayList<myGame>();
	
	private static myGame actualGame = null;
	private static File[] jingles = new File[4];
			
	public timerGUI() {
		setLayout(null);
		
		// LABELS
		JLabel lblNewLabel = new JLabel("Name of Game");
		lblNewLabel.setForeground(Color.GREEN);
		lblNewLabel.setBounds(818, 26, 102, 15);
		add(lblNewLabel);
				
		JLabel lblBreak = new JLabel("Delay");
		lblBreak.setForeground(Color.GREEN);
		lblBreak.setBounds(818, 47, 102, 15);
		add(lblBreak);
				
		JLabel lblDuration = new JLabel("Duration");
		lblDuration.setForeground(Color.GREEN);
		lblDuration.setBounds(818, 72, 102, 15);
		add(lblDuration);
				
		JLabel label = new JLabel("Break");
		label.setForeground(Color.GREEN);
		label.setBounds(818, 97, 102, 15);
		add(label);
		textAreaSched.setFont(new Font("Dialog", Font.BOLD, 14));
		textAreaSched.setWrapStyleWord(true);
		textAreaSched.setBackground(Color.BLACK);
		textAreaSched.setForeground(Color.GREEN);	
		textAreaSched.setEditable(false);
		textAreaSched.setBounds(1039, 129, 268, 371);
		add(textAreaSched);
		
		textAreaPrepare.setFont(new Font("Dialog", Font.BOLD, 10));
		textAreaPrepare.setWrapStyleWord(true);
		textAreaPrepare.setBackground(Color.BLACK);
		textAreaPrepare.setForeground(Color.GREEN);	
		textAreaPrepare.setEditable(false);
		textAreaPrepare.setBounds(1193, 22, 126, 25);
		add(textAreaPrepare);
		
		textAreaStart.setFont(new Font("Dialog", Font.BOLD, 10));
		textAreaStart.setWrapStyleWord(true);
		textAreaStart.setBackground(Color.BLACK);
		textAreaStart.setForeground(Color.GREEN);	
		textAreaStart.setEditable(false);
		textAreaStart.setBounds(1193, 47, 126, 25);
		add(textAreaStart);
				
		textArea5Min.setFont(new Font("Dialog", Font.BOLD, 10));
		textArea5Min.setWrapStyleWord(true);
		textArea5Min.setBackground(Color.BLACK);
		textArea5Min.setForeground(Color.GREEN);	
		textArea5Min.setEditable(false);
		textArea5Min.setBounds(1193, 72, 126, 25);
		add(textArea5Min);
		
		textAreaStop.setFont(new Font("Dialog", Font.BOLD, 10));
		textAreaStop.setWrapStyleWord(true);
		textAreaStop.setBackground(Color.BLACK);
		textAreaStop.setForeground(Color.GREEN);	
		textAreaStop.setEditable(false);
		textAreaStop.setBounds(1193, 97, 126, 25);
		add(textAreaStop);
		
		final JFormattedTextField tfName = new JFormattedTextField();
		tfName.setBounds(926, 22, 96, 25);
		tfName.setValue("round x");
		add(tfName);
		
		NumberFormat format = NumberFormat.getInstance();
	    NumberFormatter formatter = new NumberFormatter(format);
	    formatter.setValueClass(Integer.class);
	    formatter.setAllowsInvalid(false);
	    formatter.setMinimum(0);
	    formatter.setMaximum(maxDuration);
		
		final JFormattedTextField tfDelay = new JFormattedTextField(formatter);
		tfDelay.setBounds(926, 47, 96, 25);
		tfDelay.setValue(5);
		add(tfDelay);
		
		formatter.setMinimum(10);
		final JFormattedTextField tfDuration = new JFormattedTextField(formatter);
		tfDuration.setBounds(926, 71, 96, 25);
		tfDuration.setValue(90);
		add(tfDuration);
		
		formatter.setMinimum(0);
		final JFormattedTextField tfBreak = new JFormattedTextField(formatter);
		tfBreak.setBounds(926, 96, 96, 25);
		tfBreak.setValue(10);
		add(tfBreak);
		
		// EXIT BUTTON
		JButton btnExit = new JButton("EXIT");
		btnExit.setFont(new Font("Dialog", Font.BOLD, 14));
		btnExit.setForeground(Color.GREEN);
		btnExit.setBackground(Color.BLACK);
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnExit.setBounds(1175, 512, 132, 38);
		add(btnExit);
		
		// START BUTTON
		JButton startStop = new JButton("Start");
		startStop.setFont(new Font("Dialog", Font.BOLD, 14));
		startStop.setForeground(Color.GREEN);
		startStop.setBackground(Color.BLACK);
		startStop.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{
				
			if(gameSchedule.isEmpty())
			{
				// no games defined, nothing to start...
				//actualGame = null;
			}
			else
			{
				if(actualGame == null)	
				{
					actualGame = gameSchedule.remove(0);
					updateGame();
					//gameTimer.schedule(new myAlarm(), actualGame.advance() * FACTOR);
				}				
				
				// FIXME: pause game...?
			}
			refreshTF();
			}
		});
		startStop.setBounds(793, 129, 115, 52);
		add(startStop);
		
		// NEW GAME BUTTON
		JButton btnNewButton = new JButton("Add Game");
		btnNewButton.setFont(new Font("Dialog", Font.BOLD, 14));
		btnNewButton.setForeground(Color.GREEN);
		btnNewButton.setBackground(Color.BLACK);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myGame game = new myGame((String)tfName.getValue(), (Integer)tfDelay.getValue(), (Integer)tfDuration.getValue(), (Integer)tfBreak.getValue(), jingles);
				gameSchedule.add(game);
				refreshTF();	
				// FIXME: when adding game and AFTERWARDS setting jingles: no jingle set for game
			}
		});
		btnNewButton.setBounds(907, 129, 115, 52);
		add(btnNewButton);
		
		// RESET BUTTON
		JButton resetButton = new JButton("RESET");
		resetButton.setFont(new Font("Dialog", Font.BOLD, 14));
		resetButton.setForeground(Color.GREEN);
		resetButton.setBackground(Color.BLACK);
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gameTimer.cancel();
				gameSchedule.clear();
				actualGame = null;
				alarmDisplay.setNORMAL();
				alarmDisplay.setTime(init);
				gameTimer = new Timer();
				refreshTF();				
			}
		});
		resetButton.setBounds(1039, 512, 132, 38);
		add(resetButton);
		
		JButton btnSetStartjingle = new JButton("StartJingle");
		btnSetStartjingle.setFont(new Font("Dialog", Font.BOLD, 10));
		btnSetStartjingle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("WAV files", "wav");
				fc.setFileFilter(filter);
				fc.showOpenDialog(getComponent(0));
				jingles[START] = (fc.getSelectedFile().getAbsoluteFile());
				textAreaStart.setText(fc.getSelectedFile().getName());
			}
		});
		btnSetStartjingle.setForeground(Color.GREEN);
		btnSetStartjingle.setBackground(Color.BLACK);
		btnSetStartjingle.setBounds(1039, 47, 109, 25);
		add(btnSetStartjingle);
		
		JButton btnStopjingle = new JButton("Stopjingle");
		btnStopjingle.setFont(new Font("Dialog", Font.BOLD, 10));
		btnStopjingle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("WAV files", "wav");
				fc.setFileFilter(filter);
				fc.showOpenDialog(getComponent(0));
				jingles[STOP] = (fc.getSelectedFile().getAbsoluteFile());
				textAreaStop.setText(fc.getSelectedFile().getName());

			}
		});
		btnStopjingle.setForeground(Color.GREEN);
		btnStopjingle.setBackground(Color.BLACK);
		btnStopjingle.setBounds(1039, 96, 109, 25);
		add(btnStopjingle);
		
		JButton btnNewgamejingle = new JButton("prepareJingle");
		btnNewgamejingle.setFont(new Font("Dialog", Font.BOLD, 10));
		btnNewgamejingle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("WAV files", "wav");
				fc.setFileFilter(filter);
				fc.showOpenDialog(getComponent(0));
				jingles[PREPARE] = (fc.getSelectedFile().getAbsoluteFile());
				textAreaPrepare.setText(fc.getSelectedFile().getName());

			}
		});
		btnNewgamejingle.setForeground(Color.GREEN);
		btnNewgamejingle.setBackground(Color.BLACK);
		btnNewgamejingle.setBounds(1039, 22, 109, 25);
		add(btnNewgamejingle);
		
		JButton btnminjingle = new JButton("5minJingle");
		btnminjingle.setFont(new Font("Dialog", Font.BOLD, 10));
		btnminjingle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("WAV files", "wav");
				fc.setFileFilter(filter);
				fc.showOpenDialog(getComponent(0));
				jingles[FIVELEFT] = (fc.getSelectedFile().getAbsoluteFile());
				textArea5Min.setText(fc.getSelectedFile().getName());

			}
		});
		btnminjingle.setForeground(Color.GREEN);
		btnminjingle.setBackground(Color.BLACK);
		btnminjingle.setBounds(1039, 71, 109, 25);
		add(btnminjingle);
		
		/*
		 *		Buttons to play Jingles 
		 * */
		
		JButton btnJPrep = new JButton(">");
		btnJPrep.setIcon(null);
		btnJPrep.setFont(new Font("Dialog", Font.BOLD, 10));
		btnJPrep.setForeground(Color.GREEN);
		btnJPrep.setBackground(Color.BLACK);
		btnJPrep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				try {
					playJingle(JPREPARE);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnJPrep.setBounds(1147, 22, 42, 25);
		add(btnJPrep);
	
		//----------------------
		
		JButton btnJStart = new JButton(">");
		btnJStart.setIcon(null);
		btnJStart.setFont(new Font("Dialog", Font.BOLD, 10));
		btnJStart.setForeground(Color.GREEN);
		btnJStart.setBackground(Color.BLACK);
		btnJStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				try {
					playJingle(START);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnJStart.setBounds(1147, 47, 42, 25);
		add(btnJStart);
	
		//----------------------
		
		JButton btnJ5min = new JButton(">");
		btnJ5min.setIcon(null);
		btnJ5min.setFont(new Font("Dialog", Font.BOLD, 10));
		btnJ5min.setForeground(Color.GREEN);
		btnJ5min.setBackground(Color.BLACK);
		btnJ5min.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				try {
					playJingle(JFIVELEFT);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnJ5min.setBounds(1147, 71, 42, 25);
		add(btnJ5min);
		
		//----------------------

		JButton btnJStop = new JButton(">");
		btnJStop.setIcon(null);
		btnJStop.setFont(new Font("Dialog", Font.BOLD, 10));
		btnJStop.setForeground(Color.GREEN);
		btnJStop.setBackground(Color.BLACK);
		btnJStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				try {
					playJingle(JSTOP);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnJStop.setBounds(1147, 96, 42, 25);
		add(btnJStop);
		
		int x = 0;      
        int y = 0;    
		
		for(int i = 0; i < POS; i++)
        {
        	segmentsT[i][0] = new Polygon();   
        	segmentsT[i][0].addPoint(x+20,y+8);
        	segmentsT[i][0].addPoint(x+90,y+8);
        	segmentsT[i][0].addPoint(x+98,y+15);
        	segmentsT[i][0].addPoint(x+90,y+22);
        	segmentsT[i][0].addPoint(x+20,y+22);
        	segmentsT[i][0].addPoint(x+12,y+15);
              
        	segmentsT[i][1] = new Polygon();       
        	segmentsT[i][1].addPoint(x+91,y+23);
        	segmentsT[i][1].addPoint(x+98,y+18);
        	segmentsT[i][1].addPoint(x+105,y+23);
        	segmentsT[i][1].addPoint(x+105,y+81);
        	segmentsT[i][1].addPoint(x+98,y+89);
        	segmentsT[i][1].addPoint(x+91,y+81);
       
        	segmentsT[i][2] = new Polygon();
        	segmentsT[i][2].addPoint(x+91,y+97);
        	segmentsT[i][2].addPoint(x+98,y+89);
        	segmentsT[i][2].addPoint(x+105,y+97);
        	segmentsT[i][2].addPoint(x+105,y+154);
        	segmentsT[i][2].addPoint(x+98,y+159);
        	segmentsT[i][2].addPoint(x+91,y+154);
       
        	segmentsT[i][3] = new Polygon();
        	segmentsT[i][3].addPoint(x+20,y+155);
        	segmentsT[i][3].addPoint(x+90,y+155);
        	segmentsT[i][3].addPoint(x+98,y+162);
        	segmentsT[i][3].addPoint(x+90,y+169);
        	segmentsT[i][3].addPoint(x+20,y+169);
        	segmentsT[i][3].addPoint(x+12,y+162);
       
        	segmentsT[i][4] = new Polygon();
        	segmentsT[i][4].addPoint(x+5,y+97);
        	segmentsT[i][4].addPoint(x+12,y+89);
        	segmentsT[i][4].addPoint(x+19,y+97);
        	segmentsT[i][4].addPoint(x+19,y+154);
        	segmentsT[i][4].addPoint(x+12,y+159);
        	segmentsT[i][4].addPoint(x+5,y+154);
             
        	segmentsT[i][5] = new Polygon();
        	segmentsT[i][5].addPoint(x+5,y+23);
        	segmentsT[i][5].addPoint(x+12,y+18);
        	segmentsT[i][5].addPoint(x+19,y+23);
        	segmentsT[i][5].addPoint(x+19,y+81);
        	segmentsT[i][5].addPoint(x+12,y+89);
        	segmentsT[i][5].addPoint(x+5,y+81);
        	
        	segmentsT[i][6] = new Polygon();
        	segmentsT[i][6].addPoint(x+20,y+82);
        	segmentsT[i][6].addPoint(x+90,y+82);
        	segmentsT[i][6].addPoint(x+95,y+89);
        	segmentsT[i][6].addPoint(x+90,y+96);
        	segmentsT[i][6].addPoint(x+20,y+96);
        	segmentsT[i][6].addPoint(x+15,y+89);
        	
        	x = x + 110;
        	if(i == 1 || i == 3)
        	{
        		x = x + 50;
        	}  
        }
		
			x = 0;      
			y = 0; 
		
        	for(int i = 0; i < POS; i++)
            {
        	
        	// ------------------ ALARMCLOCK:
        	
        	/*
        	 segmentsA[i][0] = new Polygon();   
        	segmentsA[i][0].addPoint(x+20,y+dist+8);
        	segmentsA[i][0].addPoint(x+90,y+dist+8);
        	segmentsA[i][0].addPoint(x+98,y+dist+15);
        	segmentsA[i][0].addPoint(x+90,y+dist+22);
        	segmentsA[i][0].addPoint(x+20,y+dist+22);
        	segmentsA[i][0].addPoint(x+12,y+dist+15);
              
        	segmentsA[i][1] = new Polygon();       
        	segmentsA[i][1].addPoint(x+91,y+dist+23);
        	segmentsA[i][1].addPoint(x+98,y+dist+18);
        	segmentsA[i][1].addPoint(x+105,y+dist+23);
        	segmentsA[i][1].addPoint(x+105,y+dist+81);
        	segmentsA[i][1].addPoint(x+98,y+dist+89);
        	segmentsA[i][1].addPoint(x+91,y+dist+81);
       
        	segmentsA[i][2] = new Polygon();
        	segmentsA[i][2].addPoint(x+91,y+dist+97);
        	segmentsA[i][2].addPoint(x+98,y+dist+89);
        	segmentsA[i][2].addPoint(x+105,y+dist+97);
        	segmentsA[i][2].addPoint(x+105,y+dist+154);
        	segmentsA[i][2].addPoint(x+98,y+dist+159);
        	segmentsA[i][2].addPoint(x+91,y+dist+154);
       
        	segmentsA[i][3] = new Polygon();
        	segmentsA[i][3].addPoint(x+20,y+dist+155);
        	segmentsA[i][3].addPoint(x+90,y+dist+155);
        	segmentsA[i][3].addPoint(x+98,y+dist+162);
        	segmentsA[i][3].addPoint(x+90,y+dist+169);
        	segmentsA[i][3].addPoint(x+20,y+dist+169);
        	segmentsA[i][3].addPoint(x+12,y+dist+162);
       
        	segmentsA[i][4] = new Polygon();
        	segmentsA[i][4].addPoint(x+5,y+dist+97);
        	segmentsA[i][4].addPoint(x+12,y+dist+89);
        	segmentsA[i][4].addPoint(x+19,y+dist+97);
        	segmentsA[i][4].addPoint(x+19,y+dist+154);
        	segmentsA[i][4].addPoint(x+12,y+dist+159);
        	segmentsA[i][4].addPoint(x+5,y+dist+154);
             
        	segmentsA[i][5] = new Polygon();
        	segmentsA[i][5].addPoint(x+5,y+dist+23);
        	segmentsA[i][5].addPoint(x+12,y+dist+18);
        	segmentsA[i][5].addPoint(x+19,y+dist+23);
        	segmentsA[i][5].addPoint(x+19,y+dist+81);
        	segmentsA[i][5].addPoint(x+12,y+dist+89);
        	segmentsA[i][5].addPoint(x+5,y+dist+81);
        	
        	segmentsA[i][6] = new Polygon();
        	segmentsA[i][6].addPoint(x+20,y+dist+82);
        	segmentsA[i][6].addPoint(x+90,y+dist+82);
        	segmentsA[i][6].addPoint(x+95,y+dist+89);
        	segmentsA[i][6].addPoint(x+90,y+dist+96);
        	segmentsA[i][6].addPoint(x+20,y+dist+96);
        	segmentsA[i][6].addPoint(x+15,y+dist+89);
        	 * */
        	
        	segmentsA[i][0] = new Polygon();   
        	segmentsA[i][0].addPoint(x+20,y+dist+6);
        	segmentsA[i][0].addPoint(x+132,y+dist+6);
        	segmentsA[i][0].addPoint(x+140,y+dist+14);
        	segmentsA[i][0].addPoint(x+132,y+dist+22);
        	segmentsA[i][0].addPoint(x+20,y+dist+22);
        	segmentsA[i][0].addPoint(x+12,y+dist+14);
              
        	segmentsA[i][1] = new Polygon();       
        	segmentsA[i][1].addPoint(x+132,y+dist+22);
        	segmentsA[i][1].addPoint(x+140,y+dist+14);
        	segmentsA[i][1].addPoint(x+148,y+dist+22);
        	segmentsA[i][1].addPoint(x+148,y+dist+138);
        	segmentsA[i][1].addPoint(x+140,y+dist+147);
        	segmentsA[i][1].addPoint(x+132,y+dist+138);
       
        	segmentsA[i][2] = new Polygon();
        	segmentsA[i][2].addPoint(x+132,y+dist+152);
        	segmentsA[i][2].addPoint(x+140,y+dist+144);
        	segmentsA[i][2].addPoint(x+148,y+dist+152);
        	segmentsA[i][2].addPoint(x+148,y+dist+268);
        	segmentsA[i][2].addPoint(x+140,y+dist+277);
        	segmentsA[i][2].addPoint(x+132,y+dist+268);
       
        	segmentsA[i][3] = new Polygon();   
        	segmentsA[i][3].addPoint(x+20,y+dist+268);
        	segmentsA[i][3].addPoint(x+132,y+dist+268);
        	segmentsA[i][3].addPoint(x+140,y+dist+276);
        	segmentsA[i][3].addPoint(x+132,y+dist+284);
        	segmentsA[i][3].addPoint(x+20,y+dist+284);
        	segmentsA[i][3].addPoint(x+12,y+dist+276);
        	      	
        	segmentsA[i][4] = new Polygon();
        	segmentsA[i][4].addPoint(x+4,y+dist+152);
        	segmentsA[i][4].addPoint(x+12,y+dist+144);
        	segmentsA[i][4].addPoint(x+20,y+dist+152);
        	segmentsA[i][4].addPoint(x+20,y+dist+268);
        	segmentsA[i][4].addPoint(x+12,y+dist+276);
        	segmentsA[i][4].addPoint(x+4,y+dist+268);
        	// <-->            
        	segmentsA[i][5] = new Polygon();       
        	segmentsA[i][5].addPoint(x+4,y+dist+22);
        	segmentsA[i][5].addPoint(x+12,y+dist+14);
        	segmentsA[i][5].addPoint(x+20,y+dist+22);
        	segmentsA[i][5].addPoint(x+20,y+dist+138);
        	segmentsA[i][5].addPoint(x+12,y+dist+146);
        	segmentsA[i][5].addPoint(x+4,y+dist+138);
        	
        	segmentsA[i][6] = new Polygon();   
        	segmentsA[i][6].addPoint(x+20,y+dist+138);
        	segmentsA[i][6].addPoint(x+132,y+dist+138);
        	segmentsA[i][6].addPoint(x+140,y+dist+146);
        	segmentsA[i][6].addPoint(x+132,y+dist+152);
        	segmentsA[i][6].addPoint(x+20,y+dist+152);
        	segmentsA[i][6].addPoint(x+12,y+dist+146);
        	
        
        	x = x + 150;
        	if(i == 1 || i == 3)
        	{
        		x = x + 50;
        	}  
        }      	
	}

	public void paintComponent(Graphics g)  // draw graphics in the panel
    {
        int x = 0;      
        int y = 0;    

		setOpaque(true);
        setBackground(Color.black);
		
        super.paintComponent(g);            // call superclass to make panel display correctly
               
        for(int i = 0; i < POS; i++)
        {
        	g.setColor(timeDisplay.getColorVals(i, 0));
         	g.fillPolygon(segmentsT[i][0]);        	
        	g.setColor(timeDisplay.getColorVals(i, 1));
         	g.fillPolygon(segmentsT[i][1]);       	
        	g.setColor(timeDisplay.getColorVals(i, 2));
         	g.fillPolygon(segmentsT[i][2]);        	
        	g.setColor(timeDisplay.getColorVals(i, 3));
         	g.fillPolygon(segmentsT[i][3]);        	
        	g.setColor(timeDisplay.getColorVals(i, 4));
         	g.fillPolygon(segmentsT[i][4]);       	
        	g.setColor(timeDisplay.getColorVals(i, 5));
         	g.fillPolygon(segmentsT[i][5]);         	
        	g.setColor(timeDisplay.getColorVals(i, 6));
         	g.fillPolygon(segmentsT[i][6]);
         	
          	g.setColor(alarmDisplay.getColorVals(i, 0));
         	g.fillPolygon(segmentsA[i][0]);        	
        	g.setColor(alarmDisplay.getColorVals(i, 1));
         	g.fillPolygon(segmentsA[i][1]);       	
        	g.setColor(alarmDisplay.getColorVals(i, 2));
         	g.fillPolygon(segmentsA[i][2]);        	
        	g.setColor(alarmDisplay.getColorVals(i, 3));
         	g.fillPolygon(segmentsA[i][3]);        	
        	g.setColor(alarmDisplay.getColorVals(i, 4));
         	g.fillPolygon(segmentsA[i][4]);       	
        	g.setColor(alarmDisplay.getColorVals(i, 5));
         	g.fillPolygon(segmentsA[i][5]);         	
        	g.setColor(alarmDisplay.getColorVals(i, 6));
         	g.fillPolygon(segmentsA[i][6]);
       	
        	x = x + 110;
        	/*
        	if(i == 1 || i == 3)
        	{
        		
             	g.setColor(Color.green);
        		g.fillArc(x+15, y+60, 20, 20, 0, 360);
        		g.fillArc(x+15, y+100, 20, 20, 0, 360);
        		
        		if(actualGame != null)
        		{
        			if(actualGame.last5Min())
        				g.setColor(Color.red);
        			else if(actualGame.breakTime())
        				g.setColor(Color.white);
        			else
        				g.setColor(Color.green);
        		}
        		else
        			g.setColor(Color.green);
        		
        		g.fillArc(x+95, y+dist+105, 20, 20, 0, 360);
        		g.fillArc(x+95, y+dist+160, 20, 20, 0, 360);
        		x = x + 100;
        	}
        	*/  	
        }
        x=220;
        g.setColor(Color.green);
    	g.fillArc(235, y+60, 20, 20, 0, 360);
    	g.fillArc(235, y+100, 20, 20, 0, 360);
    		
    	g.fillArc(510, y+60, 20, 20, 0, 360);
    	g.fillArc(510, y+100, 20, 20, 0, 360);
    		
    		if(actualGame != null)
    		{
    			if(actualGame.last5Min())
    				g.setColor(Color.red);
    			else if(actualGame.breakTime())
    				g.setColor(Color.white);
    			else
    				g.setColor(Color.green);
    		}
    		else
    			g.setColor(Color.green);
    		
    	g.fillArc(x+95, y+dist+105, 20, 20, 0, 360);
    	g.fillArc(x+95, y+dist+160, 20, 20, 0, 360);
    	g.fillArc(x+50+400, y+dist+105, 20, 20, 0, 360);
    	g.fillArc(x+50+400, y+dist+160, 20, 20, 0, 360);
 
        
    }
	
	// MAIN
	public static void main(String[] args)
	{			
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		application.getContentPane().add(frame); 
										
		application.setSize(700, 700);         // window is 500 pixels wide, 400 high
        application.setVisible(true);
        
		mainTimer.scheduleAtFixedRate(new myTimer(), 0, 1000);
	}

	public static void update()
	{
		Calendar cal = Calendar.getInstance();
		
		int[] tmpArr = new int[6];
		int tmp;
		
		tmp = cal.get(Calendar.HOUR_OF_DAY);
		tmpArr[4] = tmp % 10;
		tmpArr[5] = (tmp - tmpArr[4]) / 10;
		
		tmp = cal.get(Calendar.MINUTE);
		tmpArr[2] = tmp % 10;
		tmpArr[3] = (tmp - tmpArr[2]) / 10;
		
		tmp = cal.get(Calendar.SECOND);
		tmpArr[0] = tmp % 10;
		tmpArr[1] = (tmp - tmpArr[0]) / 10;
		
		timeDisplay.setTime(tmpArr);
		if(actualGame != null)
		{
			alarmDisplay.setTime(actualGame.getTimeLeft());
			//System.out.println(actualGame.getTime());
		}
		application.repaint();
	}

	public static void updateGame() {
		long tmp = actualGame.advance();
		System.out.println("EVENT: "+ actualGame.getState() + " new timervalue: " + tmp);
		
		if(actualGame.last5Min())
			alarmDisplay.setRED();	
		else if (actualGame.breakTime())
			alarmDisplay.setWHITE();	
		else
			alarmDisplay.setNORMAL();	
		
		if(tmp >= 0)
		{			
			try
			{
				gameTimer.schedule(new myAlarm(), tmp*FACTOR);
				//gameTimer.schedule(new myAlarm(), actualGame.advance() * FACTOR);
			}
			catch (Exception e)
			{
				System.out.println("EXCECTOPN " + e);			
			}
		}
		else
		{
			System.out.println("GAME finished");
			if(gameSchedule.isEmpty())
			{
				System.out.println("Tournament finished");
				actualGame = null;
				alarmDisplay.setNORMAL();
				alarmDisplay.setTime(init);
			}
			else
			{
				actualGame = gameSchedule.remove(0);
				if(actualGame.gameRunning() == false)
				{
					alarmDisplay.setWHITE();
					gameTimer.schedule(new myAlarm(), actualGame.advance() * FACTOR);
				}
				else
					System.out.println("FIXME");
			}
			refreshTF();
		}		
	}
	public static void refreshTF()
	{
		String tmpStr = "Running  Game:\n";
		
		if(actualGame != null)
			tmpStr = tmpStr + "\t" + actualGame.getGameData() + "\nScheduled Games:\n";
		else
			tmpStr = tmpStr + "\nScheduled Games:\n";
			
		for (myGame gameTmp : gameSchedule)
		{
			tmpStr = tmpStr + "\t" + gameTmp.getGameData() + "\n";				
		}
		textAreaSched.setText(tmpStr);		
	}
	
	public void playJingle(int which) throws IOException
	{/*
		Mixer.Info[] mixers = AudioSystem.getMixerInfo();
	    System.out.println("There are " + mixers.length + " mixer info objects");
	    for(int i=0;i<mixers.length;i++)
	    {
	        Mixer.Info mixerInfo = mixers[i];
	        System.out.println("Mixer Name:"+mixerInfo.getName());
	        Mixer mixer = AudioSystem.getMixer(mixerInfo);
	        
	        Line.Info[] lineinfos = mixer.getTargetLineInfo();
	        for(Line.Info lineinfo : lineinfos)
	        {
	            System.out.println("line:" + lineinfo);
	            try
	            {
	                Line line = mixer.getLine(lineinfo);
	                line.open();
	                if(line.isControlSupported(FloatControl.Type.VOLUME))
	                {
	                    FloatControl control = (FloatControl) line.getControl(FloatControl.Type.VOLUME);
	                    System.out.println("Volume:"+control.getValue());   
	                   
	             
	                }
	            }
	            catch(LineUnavailableException e) {
	                e.printStackTrace();
	            	
	            }
	        }
	    }
	    */
	    
		/*
		 * 
		 
		 void open(AudioInputStream stream)
		 
		 * 
		 * */
		
		try
		{
			 AudioInputStream sound = AudioSystem.getAudioInputStream(jingles[which]);
			 
			 DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
			 Clip clip = (Clip) AudioSystem.getLine(info);
			 clip.open(sound);
			 

		 // play the sound clip
			 clip.start();
		// open jingle as stream
		//	InputStream j = new FileInputStream(myJingles[jingle]);
			
		//	AudioStream audioStream = new AudioStream(j);
		//	AudioPlayer.player.start(audioStream);
			
			 
		//	 Runtime.getRuntime().exec("/usr/bin/mpc play");

		}
		catch (Exception e)
		{
			System.out.println("Jingle Exception");			
		}
	}
	
}
