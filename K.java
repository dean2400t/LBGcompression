/*
 * K class to store partition average for each axes
 */
public class K {

	private int numOfDimensions;
	private int[] axiesValues;
	public K(int numOfDimensions)
	{
		this.numOfDimensions=numOfDimensions;
		axiesValues=new int[numOfDimensions];
	}
	public void setAxiesValues(int[] axiesValues)
	{
		for (int i=0;i<numOfDimensions; i++)
			this.axiesValues[i]=axiesValues[i];
	}
	public int[] getAxesValues()
	{
		return axiesValues;
	}
	public int getAxesValue(int index)
	{
		return axiesValues[index];
	}
}
