/*
 * Class 'Sphere' is used to calculate position for a new K based on the location of pixels in
 * the partition. The new K axes values will go towards the place where most pixels are 
 */

/*
 * General explanation:
 * A new sphere can be split into 3 values for each axes +1 0 or -1 to each axes.
 * This means, for the sphere array, that the array will be in base count three for each sphere:
 * In three dimensions:
 * [0]: -1,-1,-1
 * [1]: -1,-1,0
 * [2]: -1,-1,1
 * [3]: -1,0,-1
 * and so on.
 * 
 * if for example we find a pixel is -1 of axis 'a', then we will add 1 to all the spheres of which
 * the axis 'a' is -1
 */
public class Sphere {

	/*
	 * sphere array is used to calculate the number of pixels in each direction.  
	 * each sphere is a potential sphere to choose based on vector from the original sphere
	 */
	private long[] sphere;
	private int numOfDimensions;
	private int[] prevPow;
	private int[] jump;
	
	
	public Sphere(int numOfDimensions)
	{
		this.numOfDimensions=numOfDimensions;
		//number of spheres is three to the power of number of dimensions.
		int arraySize=(int) Math.pow(3, numOfDimensions);
		sphere=new long[arraySize];
		for (int i=0; i<arraySize; i++)
					sphere[i]=0;
		
		
		//in order to save calculation time, we save arrays of needed data in advance
		//the need for 'prevPow' and 'jump' will be explained later with more context
		prevPow=new int[numOfDimensions];
		jump=new int[numOfDimensions];
		for (int axies=0; axies<numOfDimensions; axies++)
		{
			prevPow[axies]=(int) Math.pow(3, axies);
			jump[axies]=prevPow[axies]*2;
		}
		
	}
	public void addPixelToSide(Pixel pixel, K k)
	{
		int arraySize=(int) Math.pow(3, numOfDimensions);
		int[] vectors=new int[numOfDimensions];
		
		//finding vector to pixel
		//vector 0 is -1, 1 is 0 and 2 is +1
		for (int ax=0; ax<numOfDimensions; ax++)
		{
			if (k.getAxesValue(ax)>pixel.getAxiesValue(ax))
				vectors[ax]=2;
			else if (k.getAxesValue(ax)<pixel.getAxiesValue(ax))
				vectors[ax]=0;
			else
				vectors[ax]=1;
		}
		
		int index; 
		//adding to spheres according to each vector axes
		for (int axies=0; axies<numOfDimensions; axies++)
		{
			index=prevPow[axies]*vectors[axies];
			while (index<arraySize)
			{
				for (int i=0; i<prevPow[axies]; i++)
				{
					sphere[index]++;
					index++;
				}
				//jumping to next set of spheres
				index+=jump[axies];
			}
		}
	}
	
	//maing function to get new K
	public void getNewK(Partition oldPartition, Partition newPartition)
	{
		K k=oldPartition.getK();
		for (Pixel pixel: oldPartition.getPixelsList())
			addPixelToSide(pixel, k);
		
		int[] vector=new int[numOfDimensions];
		
		
		int arraySize=(int) Math.pow(3, numOfDimensions);
		int zeroVector=0;
		for (int i=0; i<numOfDimensions; i++)
			zeroVector+=Math.pow(3, numOfDimensions);
		
		//finding the largest vector which is not the zero vector (original sphere)
		long maxPixels=0;
		int maxIndex=0;
		for (int index=0; index<arraySize; index++)
			if (sphere[index]>maxPixels && index!=zeroVector)
			{
				maxPixels=sphere[index];
				maxIndex=index;
			}
		
		//adding vector values to old partition K values and setting the new K to the new partition
		int moveValue;
		int power;
		for (int axies=numOfDimensions-1; axies>=0; axies--)
		{
			moveValue=-1;
			power=(int) Math.pow(3, axies);
			while (maxIndex-power>=0)
			{
				moveValue++;
				maxIndex-=power;
			}
			vector[axies]=moveValue+k.getAxesValue(axies);
		}
		
		K kToSet=new K(numOfDimensions);
		kToSet.setAxiesValues(vector);
		newPartition.setK(kToSet);
		
	}
	
}
