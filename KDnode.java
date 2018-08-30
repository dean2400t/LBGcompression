

public class KDnode 
	{
	    int curAxis;
	    int numOfDimantions;
	    Partition partition;
	    boolean checked;
	    
	 
	    KDnode Parent;
	    KDnode Left;
	    KDnode Right;
	 
	    
	    /*
	     * 'KDnode' constructor receive a partition with K in it, axis after the axis of it's parent or
	     * the first axis if came from the last, and number of dimensions 
	     */
	    public KDnode(Partition partition, int curAxis, int numOfDimantions)
	    {
	    	this.partition = partition;
	    	this.numOfDimantions=numOfDimantions;
	        this.curAxis = curAxis;
	        Left = Right = Parent = null;
	        checked = false;
	    }
	 
	    /*
	     * Function 'FindParent' searches based on the K axes values the
	     * tree to the parent where the new node will branch from (creating a new
	     * Boundary)
	     */
	    public KDnode FindParent(int[] point)
	    {
	        KDnode parent = null;
	        KDnode nextKDnode = this;
	        int curSearchAxis;
	        while (nextKDnode != null)
	        {
	        	curSearchAxis = nextKDnode.curAxis;
	            parent = nextKDnode;
	            if (point[curSearchAxis] > nextKDnode.partition.getK().getAxesValue(curSearchAxis))
	            	nextKDnode = nextKDnode.Right;
	            else
	            	nextKDnode = nextKDnode.Left;
	        }
	        return parent;
	    }
	 
	    public KDnode Insert(Partition partition)
	    {
	        KDnode parent = FindParent(partition.getK().getAxesValues());
	        
	        //Checking if K is the same as K of parent (not suppose to happen)
	        if (isSameKvalue(partition.getK().getAxesValues(), parent.getPartition().getK().getAxesValues()) == true)
	            return null;
	        
	        //creating new node with +1 base axis to it's parent or first axis if the parent is of the last axis  
	        KDnode newNode = new KDnode(partition, parent.curAxis + 1 < numOfDimantions ? parent.curAxis + 1: 0, numOfDimantions);
	        newNode.Parent = parent;
	 
	        if (partition.getK().getAxesValue(parent.curAxis) > parent.getPartition().getK().getAxesValue(parent.curAxis))
	            parent.Right = newNode;
	        else
	            parent.Left = newNode;
	        return newNode;
	    }
	 
	    boolean isSameKvalue(int[] axis1, int[] axis2)
	    {
	        for (int i = 0; i < numOfDimantions; i++)
	        {
	            if (axis1[i] != axis2[i])
	                return false;
	        }
	 
	        return true;
	    }
	 
	    int distance2(int[] axesValues1, int[] axesValues2)
	    {
	        int dis = 0;
	        for (int dimantion = 0; dimantion < numOfDimantions; dimantion++)
	            dis += (axesValues1[dimantion] - axesValues2[dimantion]) * (axesValues1[dimantion] - axesValues2[dimantion]);
	        return dis;
	    }
	    
	    public Partition getPartition()
	    {
	    	return partition;
	    }
	
}
