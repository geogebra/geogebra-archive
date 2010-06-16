package org.neuroph.contrib.jHRT;

import java.awt.image.BufferedImage;

/**
 * This class is for cleaning the loaded BufferedImage
 * (due to the JPEG format imprecision)
 * @author Ivana Jovicic, Vladimir Kolarevic, Marko Ivanovic
 */
public class Cleaner {

    private Cleaner(){}

    public static void scanLetter(BufferedImage image) {
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                if (image.getRGB(i, j) != -1 && image.getRGB(i, j) != -16777216) {
                    System.out.println(image.getRGB(i, j));
                }
            }
        }
    }

    /**
     * This method cleans input image by replacing
     * all non black pixels with white pixels
     * @param image - input image that will be cleaned
     * @return - cleaned input image as BufferedImage
     */
    public static BufferedImage blackAndWhiteCleaning(BufferedImage image) {
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                if (image.getRGB(i, j) != -16777216) {
                    image.setRGB(i, j, -1);
                }
            }
        }
        return image;
    }

    /**
     * This method cleans input image by replacing all pixels with RGB values
     * from -4473925 (gray) to -1 (white) with white pixels and
     * from -4473925 (gray) to -16777216 (black) with black pixels
     * @param image - input image that will be cleaned
     * @return - cleaned input image as BufferedImage
     */
    public static BufferedImage blackAndGrayCleaning(BufferedImage image) {
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                if (image.getRGB(i, j) > -4473925) {
                    image.setRGB(i, j, -1);
                } else {
                    image.setRGB(i, j, -16777216);
                }
            }
        }
        return image;
    }

    /**
     * This method cleans input image by replacing all pixels with RGB values
     * from -3092272 (light gray) to -1 (white) with white pixels and
     * from -3092272 (light gray) to -16777216 (black) with black pixels
     * @param image - input image that will be cleaned
     * @return - cleaned input image as BufferedImage
     */
    public static BufferedImage blackAndLightGrayCleaning(BufferedImage image) {
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                if (image.getRGB(i, j) > -5) {
                    image.setRGB(i, j, -1);
                } else {
                    image.setRGB(i, j, -16777216);
                }
            }
        }
        return image;
    }

    /**
     * This method cleans input image by replacing all pixels with RGB values
     * from RGBcolor input (the input color) to -1 (white) with white pixels and
     * from RGBcolor input (the input color) to -16777216 (black) with black pixels
     * @param image - input image that will be cleaned
     * @param RGBcolor - input RGB value of wanted color as reference for celaning
     * @return - cleaned input image as BufferedImage
     */
    public static BufferedImage ColorCleaning(BufferedImage image, int RGBcolor) {
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                if (image.getRGB(i, j) == RGBcolor) {
                    image.setRGB(i, j, -16777216);
                } else {
                    image.setRGB(i, j, -1);
                }
            }
        }
        return image;
    }
}
