package sample.model.PreProcessing;

import sample.util.Estimate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Oleh7 on 2/26/2016.
 */
public class StartImageParams {

    /**
     *
     * @param file
     * @throws IOException
     */
    public static void getStartValues(File file)throws IOException {
        BufferedImage bi = ImageIO.read(file);

        for (int i = 0; i < 256; i++) {}

        int x0 =0;
        int y0 = 0;
        int w = bi.getWidth();
        int h = bi.getHeight();

        int x1 = x0 + w;
        int y1 = y0 + h;
        long sumr = 0, sumg = 0, sumb = 0;
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                Color pixel = new Color(bi.getRGB(x, y));
                sumr += pixel.getRed();
                sumg += pixel.getGreen();
                sumb += pixel.getBlue();
            }
        }
        int num = w * h;
        Estimate.setRedAverage(sumr/num);
        Estimate.setGreenAverage(sumg/num);
        Estimate.setBlueAverage(sumb/num);

        System.out.println(Estimate.getRedAverage());
        System.out.println(Estimate.getGreenAverage());
        System.out.println(Estimate.getBlueAverage());

        float bright = (299 * sumr + 587 * sumg + 114 * sumb) / 10000;
        bright = bright/100000000;
        Estimate.setBrightVal(bright);

        System.out.println("Bright = " + Estimate.getBrightVal());
    }
}

