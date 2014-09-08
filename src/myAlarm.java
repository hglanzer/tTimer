import java.util.TimerTask;

public class myAlarm extends TimerTask{
	@Override
	public void run()
	{
		timerGUI.updateGame();
	}
}
