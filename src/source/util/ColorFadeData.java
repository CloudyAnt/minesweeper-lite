package source.util;

import java.awt.*;

/**
 * Compute and preserve the data that ColorFader can operate
 */
public class ColorFadeData {
    private int[] rgb0;
    private final Color[] gradients;
    private int gradientsIndex = 0;
    private final int partFrames;
    private final int totalFrames;
    private final int intervals;

    public ColorFadeData(int time, int intervals, Color start, Color... colors) {
        this.intervals = intervals;
        partFrames = time / intervals;

        // The array size is always needs to +1
        // Except for the case that every rgb tolerances are integers
        totalFrames = colors.length * (partFrames + 1);
        gradients = new Color[totalFrames];

        rgb0 = getRGB(start);
        setGradients(colors);
    }

    private void setGradients(Color... colors) {
        for (Color color : colors) {
            int[] rgb = getRGB(color);
            addGradientsTo(rgb);
            rgb0 = rgb;
        }
    }

    // rgb0 = present color, rgb1 = target color, rgb = intermediate color
    private void addGradientsTo(int[] rgb1) {
        float rTolerance = (float) (rgb1[0] - rgb0[0]) / partFrames;
        float gTolerance = (float) (rgb1[1] - rgb0[1]) / partFrames;
        float bTolerance = (float) (rgb1[2] - rgb0[2]) / partFrames;

        int frameAmount = this.partFrames;
        final float[] rgb = {rgb0[0], rgb0[1], rgb0[2]};
        while (frameAmount > 0) {
            rgb[0] += rTolerance;
            rgb[1] += gTolerance;
            rgb[2] += bTolerance;
            Color color = new Color(intercept(rgb[0]), intercept(rgb[1]), intercept(rgb[2]));
            gradients[gradientsIndex++] = color;
            frameAmount--;
        }
        // Make sure it reach the target
        // This may produce duplicate, if all tolerances are actually integer
        gradients[gradientsIndex++] = new Color(rgb1[0], rgb1[1], rgb1[2]);
    }

    public Color[] getGradients() {
        return gradients;
    }

    public int getInterval() {
        return intervals;
    }

    public int getTotalFrames() {
        return totalFrames;
    }

    private static int intercept(float f) {
        if (f < 0) {
            return 0;
        }
        return f > 255 ? 255 : (int) f;
    }

    public static int[] getRGB(Color c) {
        return new int[]{c.getRed(), c.getGreen(), c.getBlue()};
    }
}
