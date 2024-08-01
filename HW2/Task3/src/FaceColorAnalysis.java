import ij.*;
import ij.plugin.PlugIn;
import ij.process.*;
import java.util.*;

public class FaceColorAnalysis implements PlugIn {

    @Override
    public void run(String arg) {
//       declare all arrays
        int[] count = new int[256];
        int[] min = new int[256];
        int[] max = new int[256];
        double[] mean = new double[256];
        double[] mean2 = new double[256];
        Arrays.fill(min, Integer.MAX_VALUE);
        Arrays.fill(max, Integer.MIN_VALUE);

        TreeSet<Integer> uniqueColors = new TreeSet<>();
        collectUniqueColors(uniqueColors);

        Statistics(uniqueColors, count, min, max, mean, mean2);

        printToLog(count, min, max, mean, mean2);
    }

    private void collectUniqueColors(Set<Integer> uniqueColors) {
        int[] ids = WindowManager.getIDList();
        if (ids == null) {
            IJ.error("No open images.");
            return;
        }

        for (int id : ids) {
            ImagePlus imp = WindowManager.getImage(id);
            ImageProcessor ip = imp.getProcessor();
            for (int y = 0; y < ip.getHeight(); y++) {
                for (int x = 0; x < ip.getWidth(); x++) {
                    int pixel = ip.getPixel(x, y);

//  Get color components
                    int r = (pixel & 0xff0000) >> 16;
                    int g = (pixel & 0xff00) >> 8;
                    int b = pixel & 0xff;
//  Remove black colors
                    if (!(r == 0 && g == 0 && b == 0)) {
                        int color = (r << 16) | (g << 8) | b;
                        uniqueColors.add(color);
                    }
                }
            }
        }
    }

//    Compute statistics
    private void Statistics(Set<Integer> uniqueColors, int[] count, int[] min, int[] max, double[] mean, double[] mean2) {
        int[] sum = new int[256];
        int[] sumSq = new int[256];

        for (int color : uniqueColors) {
            int r = (color & 0xff0000) >> 16;
            int g = (color & 0xff00) >> 8;
            int b = color & 0xff;

            int rb2 = (r + b) / 2;
            count[g]++;
            min[g] = Math.min(min[g], rb2);
            max[g] = Math.max(max[g], rb2);
            sum[g] += rb2;
            sumSq[g] += rb2 * rb2;
        }

        for (int g = 0; g < 256; g++) {
            if (count[g] > 0) {
                mean[g] = (double) sum[g] / count[g];
                mean2[g] = (double) sumSq[g] / count[g];
            } else {
                min[g] = 0;
                max[g] = 0;
                mean[g] = 0.0;
                mean2[g] = 0.0;
            }
        }
    }

    private void printToLog(int[] count, int[] min, int[] max, double[] mean, double[] mean2) {
        IJ.log("G\tcount[G]\tmin[G]\tmax[G]\tmean[G]\tmean2[G]");
        for (int g = 0; g < 256; g++) {
            IJ.log(String.format("%d\t%d\t%d\t%d\t%.2f\t%.2f", g, count[g], min[g], max[g], mean[g], mean2[g]));
        }
    }

    public static void main(String[] args) {
        new ij.ImageJ();
        IJ.runPlugIn(FaceColorAnalysis.class.getName(), "");
    }
}
