
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


import javax.imageio.ImageIO;
import javax.swing.JTextArea;




public class Operation implements Runnable{
	
	private int numOfDimantions;
	private String path;
	private int method;
	private int numOfClusters;
	private double requiredDistortionImprovment;
	private StatusUpdate statusUp;
	private int colorSpace;
	private String getMethodName(int method)
	{
		if (method==1)
			return "Voronoi";
		else
			return "GreedySphere";
		
	}
	
	private static String getFileName(String path)
	{
		char[] chArray=path.toCharArray();
		int startIndex;
		if (chArray[chArray.length-4]=='.')
			startIndex=chArray.length-5;
		else
			startIndex=chArray.length-6;
		int endIndex=startIndex;
		String name="";
		while (startIndex>=0)
			if (chArray[startIndex]!='\\')
				startIndex--;
			else
				break;
		startIndex++;
		
		for (;startIndex<=endIndex;startIndex++)
			name+=chArray[startIndex];
		return name;
	}
	
	public Operation(String path, int method, int colorSpace, int numOfClusters, double requiredDistortionImprovment, StatusUpdate statuesUp)
	{
		this.path=path;
		this.method=method;
		this.numOfClusters=numOfClusters;
		this.requiredDistortionImprovment=requiredDistortionImprovment;
		this.statusUp=statuesUp;
		this.colorSpace=colorSpace;
	}
	
	public BufferedImage encodeRGB()
	{
		this.numOfDimantions=3;
		try {
		      // get the BufferedImage, using the ImageIO class
		      BufferedImage image = ImageIO.read(new File(path));
		      Pixel[][] pixels=getRGBpixels(image);
		      
		      Encoder encoder=new Encoder(numOfClusters, numOfDimantions, 256, pixels, requiredDistortionImprovment, statusUp);
		      
		      Pixel[][] encodedPixels;
		      if (method==1)
		    	  encodedPixels=encoder.encodeUsingVoroni(numOfClusters);
		      else
		    	  encodedPixels=encoder.encodeUsingGreedySphere(numOfClusters);
		      Decoder decoder=new Decoder();
		      statusUp.waitSetBF(decoder.rebuildRGB(encodedPixels), 2); 
		      
		      return null;
		      
		      
		    } catch (IOException e) {
		      System.err.println(e.getMessage());
		     
		    }
		return null;
	}
	
	private Pixel[][] getRGBpixels(BufferedImage image) {
		// TODO Auto-generated method stub
		int w = image.getWidth();
	    int h = image.getHeight();
	    PixelRGB[][] pixelsRGB=new PixelRGB[w][h];
	    Pixel[][] pixels=new Pixel[w][h];
	    int[] axiesValues=new int[numOfDimantions];
	    for (int i = 0; i < h; i++) 
	        for (int j = 0; j < w; j++) 
	        {
	        	pixelsRGB[j][i] = new PixelRGB(image.getRGB(j, i));
	        	axiesValues[0]=pixelsRGB[j][i].getRed();
	        	axiesValues[1]=pixelsRGB[j][i].getGreen();
	        	axiesValues[2]=pixelsRGB[j][i].getBlue();
	        	pixels[j][i]=new Pixel(numOfDimantions, axiesValues,i,j);	
	        }
	    return pixels;
	}

	public BufferedImage encodeRGBA()
	{
		this.numOfDimantions=4;
		try {
		      // get the BufferedImage, using the ImageIO class
		      BufferedImage image = ImageIO.read(new File(path));
		      Pixel[][] pixels=getRGBApixels(image);
		      
		      Encoder encoder=new Encoder(numOfClusters, numOfDimantions, 256, pixels, requiredDistortionImprovment, statusUp);

		      Pixel[][] encodedPixels;
		      if (method==1)
		    	  encodedPixels=encoder.encodeUsingVoroni(numOfClusters);
		      else
		    	  encodedPixels=encoder.encodeUsingGreedySphere(numOfClusters);
		      
		      Decoder decoder=new Decoder();
		      statusUp.waitSetBF(decoder.rebuildRGBA(encodedPixels), 2);
		      return null;
		      
		      
		      
		    } catch (IOException e) {
		      System.err.println(e.getMessage());
		    }
		return null;
	}
	
	private Pixel[][] getRGBApixels(BufferedImage image) {
		// TODO Auto-generated method stub
		int w = image.getWidth();
	    int h = image.getHeight();
	    PixelRGBA[][] pixelsRGBA=new PixelRGBA[w][h];
	    Pixel[][] pixels=new Pixel[w][h];
	    int[] axiesValues=new int[numOfDimantions];
	    for (int i = 0; i < h; i++) 
	        for (int j = 0; j < w; j++) 
	        {
	        	
	        	pixelsRGBA[j][i] = new PixelRGBA(image.getRGB(j, i));
	        	axiesValues[0]=pixelsRGBA[j][i].getAlpha();
	        	axiesValues[1]=pixelsRGBA[j][i].getRed();
	        	axiesValues[2]=pixelsRGBA[j][i].getGreen();
	        	axiesValues[3]=pixelsRGBA[j][i].getBlue();
	        	pixels[j][i]=new Pixel(numOfDimantions, axiesValues,i,j);	
	        }
	    return pixels;
	}

	@Override
	public void run() {
		if (colorSpace==1)
			encodeRGB();
		else
			encodeRGBA();
		
	}
	
	
	
}