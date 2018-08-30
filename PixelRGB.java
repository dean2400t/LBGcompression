
public class PixelRGB {
	private int red;
	private int green;
	private int blue;
	public PixelRGB(int rgba)
	{
		setRed((rgba >> 16) & 0xff);
		setGreen((rgba >> 8) & 0xff);
		setBlue((rgba) & 0xff);
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
