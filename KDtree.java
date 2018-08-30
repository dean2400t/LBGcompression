
 
/*
 * Class 'KDtree' is used to find the nearest partition's K to pixel in k dimensional space
 */
public class KDtree {

	//the root tree node
    KDnode Root;
 
    //minimum distance of a k to pixel found so far
    int d_min;
    
    //current closest k to pixel
    KDnode nearest_neighbour;
 
    //number of nodes(Ks) for list array
    int nList;
 
    //boolean array for not searching the same node twice
    KDnode CheckedNodes[];
    int checked_nodes;
    //Node's list
    KDnode List[];
 
    
    //minimum is closest distance from lower K axis to pixel axis and maximum is from upper K axis to pixel 
    int axis_min[], axis_max[];
    
    
    boolean max_boundary[], min_boundary[];
    
    //current axis in tree
    int curAxis;
    
    int n_boundry;
    
    int numOfDimensions;
 
    //KDtree builder receives number of KDnodes/partitions and number of dimensions
    public KDtree(int numOfKs, int numOfDimensions)
    {
    	this.numOfDimensions=numOfDimensions;
        Root = null;
        nList = 0;
        List = new KDnode[numOfKs];
        CheckedNodes = new KDnode[numOfKs];
        max_boundary = new boolean[numOfDimensions];
        min_boundary = new boolean[numOfDimensions];
        axis_min = new int[numOfDimensions];
        axis_max = new int[numOfDimensions];
        
    }
 
    //adding Knode with it's partition
    public boolean add(Partition partition)
    {
        if (Root == null)
        {
            Root = new KDnode(partition, 0, numOfDimensions);
            List[nList++] = Root;
        } else
        {
            KDnode pNode;
            if ((pNode = Root.Insert(partition)) != null)
                List[nList++] = pNode;
        }
 
        return true;
    }
 
    //Function 'find_nearest' is using constructed KDtree to find closest partition's K
    public KDnode find_nearest(Pixel pixel)
    {
        if (Root == null)
            return null;

        checked_nodes = 0;
        
        //finding the node's 'box' in which the pixel is found and setting it as current nearest neighbor
        KDnode parent = Root.FindParent(pixel.getAxiesValues());
        nearest_neighbour = parent;
        
        //setting the 'box' node's K distance from pixel as minimum found distance
        d_min = Root.distance2(pixel.getAxiesValues(), parent.getPartition().getK().getAxesValues());
        
        //if the parent's K value is the same as the pixel values then it is the closest to the pixel and returned
        if (parent.isSameKvalue(pixel.getAxiesValues(), parent.getPartition().getK().getAxesValues()) == true)
            return nearest_neighbour;
 
        //if not we go up the tree to find if there is a subtree with a closer K
        search_parent(parent, pixel);
        uncheck();
        
        return nearest_neighbour;
    }
 
    public void check_subtree(KDnode node, Pixel pixel)
    {
    	//checking if node was already visited
        if ((node == null) || node.checked)
            return;
 
        CheckedNodes[checked_nodes++] = node;
        node.checked = true;
        
        //setting cube which beyond it no K can be closer 
        set_bounding_cube(node, pixel.getAxiesValues());
 
        int dim = node.curAxis;
        int d = node.getPartition().getK().getAxesValue(dim) - pixel.getAxiesValue(dim);
 
        //current node's axis is farther than the minimum distance then we search only the subtree closer to the pixel (if there is one)  
        if (d * d > d_min)
        {
            if (node.getPartition().getK().getAxesValue(dim) > pixel.getAxiesValue(dim))
                check_subtree(node.Left, pixel);
            else
                check_subtree(node.Right, pixel);
        } 
        //both left and right trees might have a closer neighbor
        else
        {
            check_subtree(node.Left, pixel);
            check_subtree(node.Right, pixel);
        }
    }
 
    public void set_bounding_cube(KDnode node, int[] point)
    {
        if (node == null)
            return;
        int d = 0;
        int dx;
        
        //calculating distance of current node's axes by K value
        for (int axis = 0; axis < numOfDimensions; axis++)
        {
            dx = node.getPartition().getK().getAxesValue(axis) - point[axis];
            if (dx > 0)
            {
                dx *= dx;
                //checking if max boundary of this axis had already been reached
                if (!max_boundary[axis])
                {
                	//checking if this subtree's current axis distance is  bigger than the current distance found 
                    if (dx > axis_max[axis])
                    	axis_max[axis] = dx;
                    
                    //checking if the distance of the current axis boundary is on its bigger than the distance of the current closes neighbor 
                    if (axis_max[axis] > d_min)
                    {
                    	//checking the boundary as already far enough to stop search
                        max_boundary[axis] = true;
                        //when all boundaries are too far, the search stops
                        n_boundry++;
                    }
                }
            } else
            {
            	//same to minimum boundaries 
                dx *= dx;
                if (!min_boundary[axis])
                {
                    if (dx > axis_min[axis])
                    	axis_min[axis] = dx;
                    if (axis_min[axis] > d_min)
                    {
                        min_boundary[axis] = true;
                        n_boundry++;
                    }
                }
            }
            
            d += dx;
            //checking if overall distance so far is farther than minimum distance found, if so then return
            if (d > d_min)
                return;
 
        }
 
        //if node's distance is closer than current minimum distance than we find a closer neighbor
        if (d < d_min)
        {
            d_min = d;
            nearest_neighbour = node;
        }
    }
 
    public KDnode search_parent(KDnode parent, Pixel pixel)
    {
    	//reseting axis_min/max and min/max_boundry indicators for each search
        for (int axis = 0; axis < numOfDimensions; axis++)
        {
            axis_min[axis] = axis_max[axis] = 0;
            max_boundary[axis] = min_boundary[axis] = false; 
        }
        n_boundry = 0;
 
        KDnode search_root = parent;
        //going up the subTree to the root of tree, or until all boundaries have been eliminated from having closer neighbor 
        while (parent != null && (n_boundry != 2 * numOfDimensions))
        {
            check_subtree(parent, pixel);
            search_root = parent;
            parent = parent.Parent;
        }
 
        return search_root;
    }
 
    public void uncheck()
    {
        for (int n = 0; n < checked_nodes; n++)
            CheckedNodes[n].checked = false;
    }
 
}
 
