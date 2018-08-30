import java.awt.image.BufferedImage;

public class StatusUpdate {

	private boolean isThereSomethingToUpdate;
	private Object lock=new Object();
	private String updateContant;
	private boolean encoderFinished;
	
	private BufferedImage bf;
	public StatusUpdate()
	{
		isThereSomethingToUpdate=false;
		encoderFinished=false;
		updateContant="";
		bf=null;
		
	}
	
	//waiting for the other thread to finish, and signal before starting to wait
	public synchronized void getAccess(int whoTries)
	{
		if (whoTries==1)
		{
			while (isThereSomethingToUpdate==true)
				try {
					notifyAll();
					if (updateContant.compareTo("Finish")!=0)
						wait();
					else
						return;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		else
			while (isThereSomethingToUpdate==false)
			{
				try {
					notifyAll();
					if (updateContant.compareTo("Finish")!=0)
						wait();
					else
						return;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	
	public void makeUpdate(String up)
	{
		isThereSomethingToUpdate=true;
		updateContant=up;
		getAccess(1);
	}
	
	public void finishedUpdate()
	{
		if (updateContant.compareTo("Finish")!=0)
			updateContant="";
		isThereSomethingToUpdate=false;
		getAccess(2);
	}
	
	public String getUpdate()
	{
		return updateContant;
	}
	
	
	//waiting in interface for buffered image from operations
	public synchronized void waitSetBF(BufferedImage bufferedImage, int action)
	{
		if (action==1)
			while (bf==null)
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		else
			bf=bufferedImage;
		notifyAll();
	}
	
	public BufferedImage getBF()
	{
		return bf;
	}
}
