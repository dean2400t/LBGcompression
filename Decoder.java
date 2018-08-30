import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Decoder {

	public Decoder()
	{
		
	}
	
	public BufferedImage rebuildRGB(Pixel[][] pixels)
	{
		BufferedImage bf=new BufferedImage(pixels.length, pixels[0].length, BufferedImage.TYPE_INT_RGB);
		for (int h=0; h<pixels.length; h++)
			for (int w=0; w<pixels[0].length; w++)
			{
	            Color color = new Color(pixels[h][w].getAxiesValue(0),pixels[h][w].getAxiesValue(1),pixels[h][w].getAxiesValue(2));
	            int rgb = color.getRGB();
	            bf.setRGB(h, w, rgb);
	        }
		return bf;
		
	}
	public BufferedImage rebuildRGBA(Pixel[][] pixels)
	{
		BufferedImage bf=new BufferedImage(pixels.length, pixels[0].length, BufferedImage.TYPE_INT_ARGB);
		
		for (int h=0; h<pixels.length; h++)
			for (int w=0; w<pixels[0].length; w++)
			{
	            Color color = new Color(pixels[h][w].getAxiesValue(1),pixels[h][w].getAxiesValue(2),pixels[h][w].getAxiesValue(3),pixels[h][w].getAxiesValue(0));
	            int rgb = color.getRGB();       
	            bf.setRGB(h, w, rgb);
	        }
		return bf;
		
	}
}
