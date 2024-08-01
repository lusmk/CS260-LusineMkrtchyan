import ij.IJ; 
import ij.ImagePlus; 
import ij.process.ImageProcessor;
import ij.plugin.filter.PlugInFilter;

import java.util.TreeSet;
import java.awt.Color;

public class HSV_Threshold implements PlugInFilter {

	public int setup(String args, ImagePlus im) {
		return DOES_RGB + DOES_STACKS;
	}
	
	public void run(ImageProcessor ip) {
		int height = ip.getHeight();
		int width = ip.getWidth();
		int white = 255 * (256 * 257 + 1);
		
		Color c;
		float[] hsb = new float[3];
		int hue, sat, val, sv, size;
		
		for (int col = 0; col < width; col++)
			for (int row =0; row < height; row++) {
				c = new Color(ip.getPixel(col, row));
				Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
				hue = (int) (hsb[0] * 256);
				sv = (int) (hsb[1] * hsb[2] * 256);
				if (hue >= 3 &&  hue <= 24 && sv >= 40 && sv <= 80)
					ip.putPixel(col, row, 0);
				else
					ip.putPixel(col, row, white);
			}
	}
}
