
public class Container {

	Pixel[][] pixels;
	K[] ks;
	
	public Container(Pixel[][] pixels, K[] ks)
	{
		this.pixels=pixels;
		this.ks=ks;
	}
	
	public Pixel[][] getPixels()
	{
		return pixels;
	}
	public K[] getKs()
	{
		return ks;
	}
}
