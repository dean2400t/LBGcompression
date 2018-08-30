import java.util.ArrayList;

/*
 * class 'VoroniTree' is used to find the initial training set of Ks
 */
public class VoroniTree {
	private static int numOfDimantions;
	//starts and end for each tree
	private int[] starts;
	private int[] ends;
	
	/*
	 * Number of subtrees for a region is 2 to the power of the number of dimensions
	 * that is because each region is divided to 2 by the first axis, 2 by the second axis and
	 * so on. 
	 * So in a single array the will be base 2 numbers.
	 * for three dimensions:
	 * 000-0
	 * 001-1
	 * 010-2
	 * 011-3
	 * ...
	 */
	private VoroniTree[] trees;
	private static int numOfRegionsForSplit;
	
	/*
	 * The partitions idea is a fixed array of partitions which is split in two, from the end
	 * to a place in middle and from the start to another place in the middle.
	 * We search the array for the biggest partition and split it to the end of the other side.
	 * than we switch it with the end of it's own array and delete it. 
	 */
	private static Partition[] partitions;
	private static int upperIndex;
	private static int lowerIndex;
	private static int partitionsArraySize;
	private int placeInPartitionsArray;
	
	private static boolean continueFlag;
	
	//initiating the first Voroni tree (the root)
	public VoroniTree(int numOfDimantions, int numOfClusters, int[] starts, int[] ends, Pixel[][] pixels)
	{
		VoroniTree.numOfDimantions=numOfDimantions;
		//calculating and saving number of region for each split, 2 to the power of dimensions 
		numOfRegionsForSplit=(int) Math.pow(2, numOfDimantions);
		this.starts=starts;
		this.ends=ends;
		partitionsArraySize=numOfClusters+numOfRegionsForSplit;
		partitions=new Partition[partitionsArraySize];
		lowerIndex=0;
		upperIndex=partitionsArraySize;
		placeInPartitionsArray=0;
		VoroniTree.partitions[0]=new Partition(numOfDimantions);
		VoroniTree.partitions[0].setVtPointer(this);
		
		//creating the first partition
		trees=new VoroniTree[numOfRegionsForSplit];
		for (int i = 0; i < pixels[0].length; i++) 
	        for (int j = 0; j < pixels.length; j++)
	        	VoroniTree.partitions[0].addPixelToList(pixels[j][i]);
		continueFlag=true;
	}
	
	//creating sub Voroni tree
	private VoroniTree(int[] starts, int[] ends, int placeInArray)
	{
		trees=new VoroniTree[numOfRegionsForSplit];
		this.starts=starts;
		this.ends=ends;
		this.placeInPartitionsArray=placeInArray;
		partitions[placeInArray]=new Partition(numOfDimantions);
		
		//if starts equals to end then we cannot split the partition further 
		if (this.starts[0]==this.ends[0])
			partitions[placeInArray].setCanBeSplit(false);
		
	}
	
	//finding pixel region in current bigger region
	private int findPixelRegion(Pixel pixel)
	{
		int treeIndex=0;
		int axVal;
		for (int axis=0; axis<numOfDimantions; axis++)
		{
			if (pixel.getAxiesValue(axis)<(ends[axis]+starts[axis])/2)
				axVal=(int) Math.pow(2, axis);
			else
				axVal=0;
			treeIndex+=axVal;
		}
		return treeIndex;
	}
	
	/*
	 * Function 'findRegionsStartsEnds' is two int array and an index of a tree to indicate to 
	 * region the bigger region is being split, and base on tree index it sets the child start and end
	 * regions 
	 */
	
	private void findRegionsStartsEnds(int[] childStarts, int[] childEnds, int treeIndex)
	{
		int index=treeIndex;
		int axVal;
		for (int axies=numOfDimantions-1; axies>=0; axies--)
		{
			axVal=(int) Math.pow(2, axies);
			if (index>=axVal)
			{
				index-=axVal;
				childEnds[axies]=(starts[axies]+ends[axies])/2;
				childStarts[axies]=starts[axies];
			}
			else
			{
				if (ends[axies]-starts[axies]==1)
					childStarts[axies]=ends[axies];
				else
					childStarts[axies]=(starts[axies]+ends[axies])/2;
				childEnds[axies]=ends[axies];
			}
		}
	}
	
	/*
	 * Function 'addPixel' new pixel to a corresponding subTree's partition, and will
	 * create one if needed.
	 */
	public void addPixel(Pixel pixel)
	{
		int treeIndex=findPixelRegion(pixel);
		if (trees[treeIndex]==null)
		{
			int[] childStarts=new int[numOfDimantions];
			int[] childEnds=new int[numOfDimantions];
			findRegionsStartsEnds(childStarts, childEnds, treeIndex);
			if (placeInPartitionsArray<=lowerIndex)
			{
				upperIndex--;
				trees[treeIndex]=new VoroniTree(childStarts, childEnds, upperIndex);
			}
			else
			{
				lowerIndex++;
				trees[treeIndex]=new VoroniTree(childStarts, childEnds, lowerIndex);
			}
		}
		int placeInArray=trees[treeIndex].placeInPartitionsArray;
		VoroniTree.partitions[placeInArray].addPixelToList(pixel);
		VoroniTree.partitions[placeInArray].setVtPointer(trees[treeIndex]);
	}
	
	/*
	 * Function 'splitBiggetsPartition' finds the biggest partition and through it it's sub Voroni tree.
	 * it then splits the tree into sub trees based on the pixels in the partition
	 */
	public void splitBiggetsPartition()
	{
		int biggetsPartition=0;
		long maxNumOfPixels=0;
		
		//searching lower indexes
		for (int index=0; index<=lowerIndex; index++)
			if (maxNumOfPixels<VoroniTree.partitions[index].getNumOfPixels() && VoroniTree.partitions[index].canBeSplit()==true)
			{
				maxNumOfPixels=VoroniTree.partitions[index].getNumOfPixels();
				biggetsPartition=index;
			}
		//searching upper indexes
		for (int index=partitionsArraySize-1; index>=upperIndex; index--)
			if (maxNumOfPixels<VoroniTree.partitions[index].getNumOfPixels() && VoroniTree.partitions[index].canBeSplit()==true)
			{
				maxNumOfPixels=VoroniTree.partitions[index].getNumOfPixels();
				biggetsPartition=index;
			}
		
		//checking if a partition was found
		if (maxNumOfPixels>0)
			{
			VoroniTree partitionVT=VoroniTree.partitions[biggetsPartition].getVtPointer();
			for (Pixel pixel: VoroniTree.partitions[biggetsPartition].getPixelsList())
				partitionVT.addPixel(pixel);
			
			Partition temp;
			
			//creating new partition at the other side and removing the original partition
			if (biggetsPartition<=lowerIndex)
			{
				if (biggetsPartition<lowerIndex)
				{
					temp=VoroniTree.partitions[biggetsPartition];
					VoroniTree.partitions[biggetsPartition]=VoroniTree.partitions[lowerIndex];
					VoroniTree.partitions[lowerIndex]=temp;
				}
				VoroniTree.partitions[lowerIndex]=null;
				lowerIndex--;
			}
			if (biggetsPartition>=upperIndex)
			{
				if (biggetsPartition>upperIndex)
				{
					temp=VoroniTree.partitions[biggetsPartition];
					VoroniTree.partitions[biggetsPartition]=VoroniTree.partitions[upperIndex];
					VoroniTree.partitions[upperIndex]=temp;
				}
				VoroniTree.partitions[upperIndex]=null;
				upperIndex++;
			}
		}
		else
			continueFlag=false;
	}
	public int getNumOfPartitions()
	{
		return (lowerIndex + partitionsArraySize-upperIndex+1);
	}
	public void getPartitions(Partition[] partitionsToFill)
	{
		int index=0;
		for (int lowerIndex=0; lowerIndex<=VoroniTree.lowerIndex; lowerIndex++)
		{
			partitionsToFill[index]=partitions[lowerIndex];
			index++;
		}
		for (int upperIndex=partitionsArraySize-1; upperIndex>=VoroniTree.upperIndex; upperIndex--)
		{
			partitionsToFill[index]=partitions[upperIndex];
			index++;
		}
	}
	public boolean getContinueFlag()
	{
		return continueFlag;
	}
}
