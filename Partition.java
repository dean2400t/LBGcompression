import java.util.ArrayList;

/*
 * Partition class is used to store pixels list in each partition,
 * K for average of partitions axes,
 * can the partition be split boolean indicator,
 * pointer to Voroni subTree,
 * place in array,
 * distortion calculation according to K and pixels list
 */
public class Partition {
	//used to store sum of all pixels to later be used for averaging K
	private long[] axesSum;
	private long numOfPixels;
	private ArrayList<Pixel> pixelsList;
	private boolean canBeSplit;
	private K k;
	private int[] partitionCenter;
	private long distortion;
	private int numOfDimantions;
	private VoroniTree vtPointer;
	private int placeInArray;
	public Partition(int numOfAxies)
	{
		numOfDimantions=numOfAxies;
		this.axesSum=new long[numOfAxies];
		for (int i=0; i<numOfAxies; i++)
			axesSum[i]=0;
		setNumOfPixels(0);
		pixelsList=new ArrayList<Pixel>();
		setK(new K(numOfAxies));
		setCanBeSplit(true);
		setDistortion(0);
	}
	public long getAxVal(int axies) {
		return axesSum[axies];
	}
	public int getAvgAxies(int axies) {
		return (int)(axesSum[axies]/numOfPixels);
	}
	public void setAxies(long sum, int axies) {
		this.axesSum[axies] = sum;
	}
	public void addToAxies(int num, int axies)
	{
		axesSum[axies]+=num;
	}
	public long getNumOfPixels() {
		return numOfPixels;
	}
	public void setNumOfPixels(int numOfPixels) {
		this.numOfPixels = numOfPixels;
	}
	public ArrayList<Pixel> getPixelsList() {
		return pixelsList;
	}
	//adding pixel's axes to correct sum and pixel to pixels list of the partition
	public void addPixelToList(Pixel pixel) {
		this.pixelsList.add(pixel);
		this.numOfPixels++;
		for (int dimantion=0; dimantion<axesSum.length; dimantion++)
			addToAxies(pixel.getAxiesValue(dimantion), dimantion);
	}
	public boolean canBeSplit() {
		return canBeSplit;
	}
	public void setCanBeSplit(boolean canBeSplit) {
		this.canBeSplit = canBeSplit;
	}
	public K getK() {
		return k;
	}
	public void setK(K k) {
		this.k = k;
	}
	
	//calc distortion O(n) where n is number of pixels in partition
	public void calcDistortion()
	{
		setDistortion(0);
		for (Pixel pixel: pixelsList)
			for (int axis=0; axis<axesSum.length; axis++)
				setDistortion(getDistortion() + (k.getAxesValue(axis)-pixel.getAxiesValue(axis))*(k.getAxesValue(axis)-pixel.getAxiesValue(axis)));
	}
	public long getDistortion() {
		return distortion;
	}
	public void setDistortion(long distortion) {
		this.distortion = distortion;
	}
	
	public void averageK()
	{
		int[] axes=new int[numOfDimantions];
		for (int dim=0; dim<numOfDimantions; dim++)
			axes[dim]=getAvgAxies(dim);
		k.setAxiesValues(axes);
	}
	public int[] getPartitionCenter() {
		return partitionCenter;
	}
	public void setPartitionCenter(int[] partitionCenter) {
		this.partitionCenter = partitionCenter;
	}
	public VoroniTree getVtPointer() {
		return vtPointer;
	}
	public void setVtPointer(VoroniTree vtPointer) {
		this.vtPointer = vtPointer;
	}
	public int getPlaceInArray() {
		return placeInArray;
	}
	public void setPlaceInArray(int placeInArray) {
		this.placeInArray = placeInArray;
	}

}
