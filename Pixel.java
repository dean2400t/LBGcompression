
public class Pixel {
	
	private int[] axiesValues;
	private int h;
	private int w;
	
	public Pixel(int numOfAxies, int[] axiesValues, int h, int w)
	{
		this.axiesValues=new int[numOfAxies];
		for (int i=0; i<numOfAxies; i++)
			this.axiesValues[i]=axiesValues[i];
		this.setH(h);
		this.setW(w);
	}
	
	public int getAxiesValue(int index)
	{
		return axiesValues[index];
	}
	
	public int[] getAxiesValues()
	{
		return axiesValues;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}
}
