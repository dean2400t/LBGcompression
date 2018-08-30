import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Encoder {
	private Pixel[][] pixels;
	private Partition[] partitions;
	private int numOfDimantions;
	private boolean continueFlag;
	private StatusUpdate statusUp;
	
	private long curDistortion;
	private long prevDistortion;
	private BigDecimal requiredDistortion;
	private BigDecimal noDistortionIndicator;
	private int numOfpartitions;
	private int maxAxiesValue;
	private int iterationNum;
	private long startTime;
	/*
	 * Encoder builder function receives 'numOfClusters' for number of maximum partitions, 'numOfDimantions'
	 * for number of axes for data, 'maxAxiesValue' for maximum values for voroni tree, 'pixels' array for
	 * rgb/rgba pixel data, and 'requiredDistortionImprovment' for iterations to better results
	 */
	public Encoder(int numOfClusters, int numOfDimantions, int maxAxiesValue, Pixel[][] pixels, double requiredDistortionImprovment, StatusUpdate statusUp)
	{
		startTime=System.currentTimeMillis();
		this.statusUp=statusUp;
		//continue flag to indicate that no more partitions can be splist
		continueFlag=true;
		iterationNum=1;
		this.numOfDimantions=numOfDimantions;
		this.pixels=pixels;
		this.maxAxiesValue=maxAxiesValue;
		requiredDistortion=new BigDecimal(requiredDistortionImprovment);
		
		
		noDistortionIndicator=new BigDecimal(100);
		 
		//-1 for indication for first improvement
		prevDistortion=-1;
		curDistortion=-1;
		numOfpartitions=0;
	}
	
	
	//A: main encoding function
	
	/*
	 * Function 'encodeUsingGreedySphere' receives maximum allowed number of partitions and using greedy sphere splits the partitions.
	 * The function then vector to each pixel and returns pixel array containing the changed pixels 
	 */
	public Pixel[][] encodeUsingGreedySphere(int numOfClusters)
	{
		
		continueFlag=true;
		partitions=new Partition[numOfClusters];
		partitions[0]=new Partition(numOfDimantions);
		calcFirstK(partitions[0]);
		
		//calculate how many partitions are not within power of 2 for later splits
		int itirationTime=(int) (Math.log(numOfClusters)/Math.log(2));
		itirationTime=(int) Math.pow(2, itirationTime);
		
		//Iterating using power of 2
		int oldPartitionIndex;
		int newPartitionIndex;
		numOfpartitions=1;
		for (int i=2; i<=itirationTime && continueFlag==true; i*=2)
		{
			newPartitionIndex=i/2;
			oldPartitionIndex=0;
			for (;newPartitionIndex<i && continueFlag==true;newPartitionIndex++)
			{
				partitions[newPartitionIndex]=new Partition(numOfDimantions);
				setNewK(partitions[oldPartitionIndex], partitions[newPartitionIndex]);
				calcNewAvgsAndPartitions(newPartitionIndex, oldPartitionIndex);
				oldPartitionIndex++;
				//stop condition if no more partitions can be split
				if (continueFlag)
					numOfpartitions++;
			}
		}
		
		//Splitting the largest partitions until reaching maximum number of partitions or no more can be split 
		int extra=numOfClusters-itirationTime;
		
		for (int i=0; i<extra && continueFlag==true; i++)
		{
			newPartitionIndex=itirationTime+i;
			partitions[newPartitionIndex]=new Partition(numOfDimantions);
			oldPartitionIndex=findPartitionToSplit(newPartitionIndex);
			setNewK(partitions[oldPartitionIndex], partitions[newPartitionIndex]);
			calcNewAvgsAndPartitions(newPartitionIndex, oldPartitionIndex);
			if (continueFlag)
				numOfpartitions++;
		}
		/*
		 * If continueFlag is false, it means that we have an empty partition.
		 * In this section we find it and remove it
		 */
		if (continueFlag==false)
		{
			Partition[] tempPartitions=new Partition[numOfpartitions-1];
			int index=0;
			for (int par=0; par<numOfpartitions; par++)
				if (partitions[par].getNumOfPixels()!=0)
				{
					tempPartitions[index]=partitions[par];
					index++;
				}
			partitions=tempPartitions;
			numOfpartitions--;

		}
		Pixel[][] encodedPixels=buildVectors();
		return encodedPixels;
	
	}
	
	/*
	 * Function 'encodeUsingVoroni' receives maximum allowed number of partitions and using Voroni's regions splits the partitions.
	 * The function then vector to each pixel and returns pixel array containing the changed pixels 
	 */
	public Pixel[][] encodeUsingVoroni(int numOfClusters)
	{
		//Initiating the Voroni's regions min and max values
		int starts[]=new int[numOfDimantions];
		int ends[]=new int[numOfDimantions];
		for (int dim=0; dim<numOfDimantions; dim++)
		{
			starts[dim]=0;
			ends[dim]=maxAxiesValue;
		}
		
		//creating Voroni tree
		VoroniTree vTree=new VoroniTree(numOfDimantions, numOfClusters, starts, ends, pixels);
		
		//in order to not receive more partitions than needed we stop at the split before the one that might give us extra partitions
		int stopCluster=(int) (numOfClusters-Math.pow(2, numOfDimantions));
		
		//Splitting the regions and inserting Ks to each one
		while (stopCluster>=vTree.getNumOfPartitions() && vTree.getContinueFlag())
			vTree.splitBiggetsPartition();
		
		partitions=new Partition[numOfClusters];
		vTree.getPartitions(partitions);
		
		int numofPar=vTree.getNumOfPartitions();
		
		//averaging partitions
		for (int index=0; index<numofPar; index++)
			partitions[index].averageK();
		
		numOfpartitions=numofPar;
		//splitting the biggest partitions using Greedy sphere
		if (numofPar<numOfClusters)
		{
			int pToSplit=0;
			for (int newPartitionIndex=numofPar; newPartitionIndex<numOfClusters && pToSplit>=0; newPartitionIndex++)
			{
				pToSplit=findPartitionToSplit(numofPar);
				if (pToSplit!=-1)
				{
					partitions[newPartitionIndex]=new Partition(numOfDimantions);
					setNewK(partitions[pToSplit], partitions[newPartitionIndex]);
					calcNewAvgsAndPartitions(newPartitionIndex, pToSplit);
					numOfpartitions++;
				}
			}
			/*
			 * If continueFlag is false, it means that we have an empty partition.
			 * In this section we find it and remove it
			 */
			if (continueFlag==false)
			{
				Partition[] tempPartitions=new Partition[numOfpartitions-1];
				int index=0;
				for (int par=0; par<numOfpartitions; par++)
					if (partitions[par].getNumOfPixels()!=0)
					{
						tempPartitions[index]=partitions[par];
						index++;
					}
				partitions=tempPartitions;
				numOfpartitions--;
			}
		}
		
		//Getting encoded pixels
		Pixel[][] encodedPixels=buildVectors();
		return encodedPixels;
		
		
	}
	
	
	//B: Helper functions
	
	//Using Greedy sphere to get new partition's K axes values
	private void setNewK(Partition oldPartition, Partition newPartition)
	{
		Sphere sphere=new Sphere(numOfDimantions);
		sphere.getNewK(oldPartition, newPartition);
	}
	
	
	//Making the first partition which is built from all pixels 
	private void calcFirstK(Partition partition)
	{
		for (int i = 0; i < pixels[0].length; i++) 
	        for (int j = 0; j < pixels.length; j++)
	        	partition.addPixelToList(pixels[j][i]);
		partition.averageK();
	}
	
	//Function 'calcPointsDistance' to calculate distance from two points
	private long calcPointsDistance(int[] v, int[] t)
	{
		long distance=0;
		for (int dimantion=0; dimantion<numOfDimantions; dimantion++)
			distance+= (v[dimantion]-t[dimantion])*(v[dimantion]-t[dimantion]);
		return distance;
	}
	
	/*
	 * Function 'findPartitionToSplit' to find the largest splittable partition
	 * returns index of partition to split or -1 if no partition can be split.
	 */
	private int findPartitionToSplit(int curP)
	{
		int pToSplit=-1;
		long maxNumOfPixels=0;
		for (int p=0; p<numOfpartitions && partitions[p]!=null; p++)
			if (partitions[p].canBeSplit() && partitions[p].getNumOfPixels()>maxNumOfPixels && curP!=p)
			{
				pToSplit=p;
				maxNumOfPixels=partitions[p].getNumOfPixels();
			}
		return pToSplit;
	}
	
	/*
	 * Function 'calcNewAvgsAndPartitions' receives two partitions Indexes, an old partition
	 * with pixels list, and a new partition with no pixels and a different K.
	 * the function then split the points between the partitions based on proximity
	 * 
	 * if one of the partitions end up being empty, the function will indicate the current
	 * partition as not able to be split, and will split the empty partition with the next
	 * biggest partition. The recursion will continue until a partition is split successfully or
	 * no more partitions can be split.
	 */
	
	private void calcNewAvgsAndPartitions(int newPartitionIndex, int oldPartitionIndex)
	{
		//tempForOld to be used for the partition which being split
		Partition tempForOld=new Partition(numOfDimantions);
		long newCcalcDis;
		long oldCcalcDis;
		
		//getting Ks axes
		int[] oldKaxies=partitions[oldPartitionIndex].getK().getAxesValues();
		int[] newKaxies=partitions[newPartitionIndex].getK().getAxesValues();
		
		//in case the new partition is not empty
		if (partitions[newPartitionIndex]!=null)
		{
			K k=new K(numOfDimantions);
			k.setAxiesValues(newKaxies);
			partitions[newPartitionIndex]=new Partition(numOfDimantions);
			partitions[newPartitionIndex].setK(k);
		}
		
		//adding pixels to the closest partition based on K axes values
		for (Pixel pixel : partitions[oldPartitionIndex].getPixelsList())
		{
			newCcalcDis=calcPointsDistance(newKaxies, pixel.getAxiesValues());
			oldCcalcDis=calcPointsDistance(oldKaxies, pixel.getAxiesValues());
			
			if (oldCcalcDis<newCcalcDis)
				tempForOld.addPixelToList(pixel);
			else
				partitions[newPartitionIndex].addPixelToList(pixel);
		}
		int pToSplit;
		
		//Resplitting new partition in case it ended up empty
		if (partitions[newPartitionIndex].getNumOfPixels()==0)
		{
			partitions[oldPartitionIndex]=tempForOld;
			partitions[oldPartitionIndex].setCanBeSplit(false);
			
			partitions[oldPartitionIndex].averageK();
			
			pToSplit=findPartitionToSplit(newPartitionIndex);
			if (pToSplit>-1)
			{
				setNewK(partitions[pToSplit], partitions[newPartitionIndex]);
				calcNewAvgsAndPartitions(newPartitionIndex, pToSplit);
			}
			else
				continueFlag=false;
		}
		
		//Resplitting old partition in case it ended up empty
		else if (tempForOld.getNumOfPixels()==0)
		{
			partitions[newPartitionIndex].setCanBeSplit(false);
			partitions[newPartitionIndex].averageK();
			partitions[oldPartitionIndex]=tempForOld;
			
			pToSplit=findPartitionToSplit(oldPartitionIndex);
			if (pToSplit>-1)
			{
				setNewK(partitions[pToSplit], partitions[oldPartitionIndex]);
				calcNewAvgsAndPartitions(oldPartitionIndex, pToSplit);
			}
			else
				continueFlag=false;
		}
		//saving the splitted partitions and averaging Ks
		else
		{
			partitions[oldPartitionIndex]=tempForOld;
			partitions[oldPartitionIndex].averageK();
			partitions[newPartitionIndex].averageK();
		}
	}
	
	
	//C: Vector building functions
	
	
	/*
	 * Function 'optimize' is using KDtree to find the closest partition's K to each pixel
	 * and adding it to the correct partition. It then checks for the distortion improvement from
	 * the last iteration. if the improvement is small enough it will end.
	 */
	private void optimize()
	{

		//creating KDtree and empty partitions with the same Ks as the original partitions
		KDtree tree=new KDtree(numOfpartitions, numOfDimantions);
		Partition[] adjPartitions=new Partition[numOfpartitions];
		for (int index=0; index<numOfpartitions; index++)
		{
			tree.add(partitions[index]);
			partitions[index].setPlaceInArray(index);
			adjPartitions[index]=new Partition(numOfDimantions);
			adjPartitions[index].setK(partitions[index].getK());
		}
		Partition curPartition;
		//adding pixel to the closest partition based on it's K axes values
		for (int partition=0; partition<numOfpartitions; partition++)
	        for (Pixel pixel : partitions[partition].getPixelsList())
	        {
	        	curPartition=tree.find_nearest(pixel).getPartition();
	        	adjPartitions[curPartition.getPlaceInArray()].addPixelToList(pixel);	        	
	        }
		partitions=adjPartitions;
		
		
		//averaging new Ks based on pixels
		String up;
		for (int index=0; index<numOfpartitions; index++)
		{
			//checking for empty partitions, if one is found then splitting the biggest partition
			if (partitions[index].getNumOfPixels()==0)
			{
				int pToSplit=findPartitionToSplit(index);
				if (pToSplit!=-1)
				{
					up="zero: "+index+" to->"+pToSplit+"\n";
					System.out.print(up);
					statusUp.makeUpdate(up);
					setNewK(partitions[pToSplit], partitions[index]);
					calcNewAvgsAndPartitions(index, pToSplit);
					//resetting partitions 
					optimize();
					return;
				}
				//not suppose to happen, but if for some reason it does, an error is better than a crush.
				else
				{
					statusUp.makeUpdate("Error in optimization, can't split partition");
					return;
				}
			}
			else
				partitions[index].averageK();
		}
		
		//checking if iterations are needed
		if (requiredDistortion.compareTo(noDistortionIndicator)!=0)
			//if no previous distortion exist, it means we are in the first iteration
			if (prevDistortion==-1)
			{
				prevDistortion=0;
				for (int partition=0; partition<numOfpartitions; partition++)
				{
			        	partitions[partition].calcDistortion();
			        	prevDistortion+=partitions[partition].getDistortion();
			    }
				optimize();
				return;
			}
			else
			{
				//calculating improvement by percent and check if another iteration is needed
				curDistortion=0;
				for (int partition=0; partition<numOfpartitions; partition++)
			        {
			        	partitions[partition].calcDistortion();
			        	curDistortion+=partitions[partition].getDistortion();
			        }
				if (curDistortion!=0)
				{
					BigDecimal prev=new BigDecimal(prevDistortion);
					BigDecimal cur=new BigDecimal(curDistortion);
					BigDecimal prevMinusCur=prev.subtract(cur);
					@SuppressWarnings("deprecation")
					BigDecimal improvmentRatio=prevMinusCur.divide(cur,8,BigDecimal.ROUND_HALF_UP);
					up="Iteration "+iterationNum+": ";
					up+=improvmentRatio.toString()+"% improvment \n";
					System.out.print(up);
					statusUp.makeUpdate(up);
					if (improvmentRatio.compareTo(requiredDistortion)==1)
					{
						iterationNum++;
						prevDistortion=curDistortion;
						optimize();
					}
				}
			}
		
	}
	
	/*
	 * Funciton 'buildVectors' uses partitions array and optimize function to return
	 * compressed pixels array 
	 */
	@SuppressWarnings("deprecation")
	private Pixel[][] buildVectors()
	{
		Pixel[][] encodedPixels=new Pixel[pixels.length][pixels[0].length];
		int[] axes=new int[numOfDimantions];
		
		
		
		
		optimize();
		
		//encoding new pixels based on partitions
		for (int partition=0; partition<numOfpartitions; partition++)
	        for (Pixel pixel : partitions[partition].getPixelsList())
	        {
		    	axes=partitions[partition].getK().getAxesValues();
		        encodedPixels[pixel.getW()][pixel.getH()] = new Pixel(numOfDimantions,axes,pixel.getH(),pixel.getW());
	        }
	   
		
		long orgNumOfPix=pixels.length*pixels[0].length;
		long numOfPix=0;
		
		
		for (int partition=0; partition<numOfpartitions; partition++)
			numOfPix+=partitions[partition].getNumOfPixels();
		
		String up="original number of pixels: "+orgNumOfPix+". In partitions: "+numOfPix+"\n";
		System.out.print(up);
		statusUp.makeUpdate(up);
		
		BigDecimal overall=new BigDecimal(numOfPix);
		BigDecimal cur;
		BigDecimal prec;
		int par=0;
		up="Partition | Num of pixels | percentage | K \n";
		System.out.print(up);
		statusUp.makeUpdate(up);
		for (int partition=0; partition<numOfpartitions; partition++)
		{
			cur=new BigDecimal(partitions[partition].getNumOfPixels());
			prec=cur.divide(overall,4,BigDecimal.ROUND_HALF_UP);
			prec=prec.multiply(noDistortionIndicator);
			up=par+": "+partitions[partition].getNumOfPixels()+" -> "+prec+"%|| ";
			for (int axis=0; axis<numOfDimantions-1; axis++)
				up+=partitions[partition].getK().getAxesValue(axis)+",";
			up+=partitions[partition].getK().getAxesValue(numOfDimantions-1)+"\n";
			System.out.print(up);
			statusUp.makeUpdate(up);
			par++;
			for (int check=0; check<numOfpartitions; check++)
				if (calcPointsDistance(partitions[check].getK().getAxesValues(), partitions[partition].getK().getAxesValues())==0 && check!=partition)
					System.out.println("Error: "+check+"-"+partition);
		}
		
		 long finishTime=System.currentTimeMillis();
		 
		up="Time: "+(finishTime-startTime) +" \n";
		System.out.print(up);
		statusUp.makeUpdate(up);
		up="Finish";
		System.out.print(up);
		statusUp.makeUpdate(up);
		return encodedPixels;
	}
	
}

