import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class myGame {
	
	public enum gameState
	{
		SCHEDULED,
		PREBREAK,
		PLAYTIME,
		LAST5MIN,
		POSTBREAK,
		FINISHED
	}
	
	private String myName;
	private int myDuration;	// how long is this match?
	private int myDelay;		// delay BEFORE match
	private int myPause;		// break AFTER match
	
	protected static final int START = 0;
	protected static final int STOP = 1;
	protected static final int PREPARE = 2;
	protected static final int FIVELEFT = 3;
	protected static File[] myJingles = new File[5];
	
	protected static String logfile = "/tmp/alarm.log";
	
	/*
	private static File startSoonJingle   = "/home/hglanzer/GIT/tTimer/sounds/start.wav";
	private static File startJingle       = "/home/hglanzer/GIT/tTimer/sounds/start.wav";
	private static File fiveminJingle     = "/home/hglanzer/GIT/tTimer/sounds/5minleft.wav";
	private static File endJingle         = "/home/hglanzer/GIT/tTimer/sounds/smb_gameover.wav";
	 * */
	
	private boolean isRunning;
	private int endTime;
	private gameState myState;
	
	private FileWriter fw;
	
	public myGame(String name, int delay, int duration, int pause, File[] jingles)
	{
		this.myName = name;
		this.myDuration = duration;
		this.myDelay = delay;
		this.myPause = pause;
		this.myState = gameState.SCHEDULED;
		this.isRunning = false;
		
		myJingles = jingles;
		
		for(int i = 0; i < 4; i++)
			System.out.println("JINGLE: " + myJingles[i]);
		
		logEvent("NEW GAME:" + name + ": " + delay + "/" + duration + "/" + pause);
	}
	
	public String getGameData()
	{
		return this.myName + ": " + this.myDelay + "/" + this.myDuration + "/" + this.myPause;		
	}
	
	public boolean gameRunning()
	{
		return this.isRunning;
	}
	
	public void pause() {
		this.isRunning = false;	
	}

	public void unpause() {
		this.isRunning = true;	
	}
	
	public int getTime()
	{
		Calendar cal = Calendar.getInstance();
		return (int) (this.endTime - cal.getTimeInMillis()/1000);
	}
	
	public boolean last5Min()
	{
		if(this.myState == gameState.LAST5MIN)
			return true;
		else
			return false;
	}
	
	public boolean breakTime()
	{
		if(this.myState == gameState.POSTBREAK || this.myState == gameState.PREBREAK)
			return true;
		else
			return false;
	}
	
	public int[] getTimeLeft()
	{
		long tmp;
		int sec = 0;
		int min = 0;
		int hour = 0;
		int[] tmpStr = new int[6];

		Calendar cal = Calendar.getInstance();
		tmp = this.endTime - (cal.getTimeInMillis() / 1000);
		sec = (int)tmp % 60;
		min = (((int)tmp - sec) / 60) % 60;
		hour = ((int)tmp - sec - min) / 3600;
		
		tmpStr[0] = sec % 10;
		tmpStr[1] = (sec-tmpStr[0]) / 10;
		
		tmpStr[2] = min % 10;
		tmpStr[3] = (min - tmpStr[2]) / 10;
		tmpStr[4] = hour;

    	//System.out.printf("tick: %d%d:%d%d:%d%d\n", tmpStr[5], tmpStr[4], tmpStr[3], tmpStr[2], tmpStr[1], tmpStr[00]);	
		return tmpStr;
	}

	// returns next timerinterval in seconds
	public long advance()
	{
		Calendar cal = Calendar.getInstance();
		int tmp = (int) (cal.getTimeInMillis() / 1000);
		
		switch(myState)
		{
		case SCHEDULED:
			myState = gameState.PREBREAK;
			this.endTime = (int)(cal.getTimeInMillis() / 1000) + (this.myDelay * 60);
			System.out.println("State: " + this.myState + " ends in " + (this.endTime - tmp));
			logEvent(this.myName + " / " + this.myState + " ends in " + (this.endTime - tmp));
			
			if(this.myDelay > 0)
			{
				try {
					playJingle(PREPARE);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return this.myDelay * 60;
			
		case PREBREAK:
			myState = gameState.PLAYTIME;
			this.endTime = (int)(cal.getTimeInMillis() / 1000) + (this.myDuration * 60);
			System.out.println("State: " + this.myState + " ends in " + (this.endTime - tmp));
			logEvent(this.myName + " / " + "State: " + this.myState + " ends in " + (this.endTime - tmp));
			try {
				playJingle(START);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(this.myDuration < 5)
				return (this.myDuration*60);
			else
				return ((this.myDuration - 5)*60);

			
		case PLAYTIME:
			if(this.myDuration < 5)
			{
				myState = gameState.POSTBREAK;
				this.endTime = (int)(cal.getTimeInMillis() / 1000) + (this.myPause * 60);
				return (this.myPause * 60);
			}
			else
			{
				myState = gameState.LAST5MIN;
				try {
					playJingle(FIVELEFT);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				logEvent(this.myName + " / " +  "last 5 minutes started");
				return 5*60;  
			}
		case LAST5MIN:
			myState = gameState.POSTBREAK;
			this.endTime = (int)(cal.getTimeInMillis() / 1000) + (this.myPause * 60);
			System.out.println("State: " + this.myState + " ends in " + (this.endTime - tmp));
			logEvent(this.myName + " / " + "GAME over");
			try {
				playJingle(STOP);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return this.myPause * 60;
			
		case POSTBREAK:
			myState = gameState.FINISHED;
			this.endTime = 0;
			logEvent(this.myName + " / " + "POSTbreak finished");
			return -1;
			
		case FINISHED:
			//System.out.println("NOT ADVCANCING FROM FINISHED");
			this.endTime = 0;
			return -1;
			
		default:
			return -1;
		}
	}

	public void playJingle(int jingle) throws IOException
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
			 AudioInputStream sound = AudioSystem.getAudioInputStream(myJingles[jingle]);
			 
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
	
	public String getState() {
		
		switch(myState)
		{
		case SCHEDULED:
			return "SCHEDULED";
			
		case PREBREAK:
			return "PREBREAK";
			
		case PLAYTIME:
			return "PLAYTIME";  
			
		case LAST5MIN:
			return "LAST5MIN";
			
		case POSTBREAK:
			return "POSTBREAK";
			
		case FINISHED:
			return "FINISHED";
			
		default:
			return "DEFAULT";
		}
	}
	
	public void logEvent(String log)
	{
		try
		{	
			Calendar cal = Calendar.getInstance();
			cal.get(Calendar.HOUR_OF_DAY);
			FileWriter fw = new FileWriter(logfile,true);	
			fw.write(cal.get(Calendar.HOUR_OF_DAY)+":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND)+ ": " + log + "\n");
			fw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
