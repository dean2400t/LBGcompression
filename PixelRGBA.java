
public class PixelRGBA {
	private int alpha;
	private int red;
	private int green;
	private int blue;
	public PixelRGBA(int rgba)
	{
		setAlpha((rgba >> 24) & 0xff);
		setRed((rgba >> 16) & 0xff);
		setGreen((rgba >> 8) & 0xff);
		setBlue((rgba) & 0xff);
	}
	
	public int getAlpha() {
		return alpha;
	}
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	public int getRed() {
		return red;
	}
	public void setRed(int red) {
		this.red = red;
	}
	public int getGreen() {
		return green;
	}
	public void setGreen(int green) {
		this.green = green;
	}
	public int getBlue() {
		return blue;
	}
	public void setBlue(int blue) {
		this.blue = blue;
	}
}
