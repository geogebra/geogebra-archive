/**
 * Copyright 2010 Neuroph Project http://neuroph.sourceforge.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.neuroph.contrib.imgrec;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * The intention of this class is to allow you to pay up front (at construction) 
 * the compute cost of converting the RGB values in a BufferedImage into a derived form.
 * 
 * The major benefit of using this class is a single loop that grabs all 3 color channels
 * (red, green, and blue) from each (x,y) coordinate, as opposed to looping through each
 * color channel (red, green, or blue) when you need it.
 * 
 * If you only need a single color from the channels (red, green, or blue) then
 * using this class may be more expensive than a custom solution.
 * 
 * In the event that it needs to be parsed, the flattened rgb values array contains 
 * all the red first, followed by all the green, followed by all the blue values.  
 * The flattened array size should be divisible by 3. 
 * 
 * @author Jon Tait
 *
 */
public class FractionRgbData
{
        /**
         * Image width
         */
	private int width;

        /**
         * Image height
         */
	private int height;

        /**
         * Array which contains red componenet of the color for each image pixel
         */
	protected double[][] redValues;

        /**
         * Array which contains green componenet of the color for each image pixel
         */
	protected double[][] greenValues;

        /**
         * Array which contains blue componenet of the color for each image pixel
         */
	protected double[][] blueValues;

        /**
         * Single array with the red, green and blue componenets of the color for each image pixel
         */
	protected double[] flattenedRgbValues;

        /**
         * Creates rgb data for the specified image.
         * @param img image to cretae rgb data for
         */
	public FractionRgbData(BufferedImage img)
	{
		width = img.getWidth();
		height = img.getHeight();
		
		redValues = new double[height][width];
		greenValues = new double[height][width];
		blueValues = new double[height][width];
		flattenedRgbValues = new double[width * height * 3];
		
		populateRGBArrays(img);
	}

        /**
         * Fills the rgb arrays from image
         * @param img image to get rgb data from
         */
	protected void populateRGBArrays(BufferedImage img)
	{
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color color = new Color(img.getRGB(x, y));

				double red = ((double) color.getRed()) / 256d;
				redValues[y][x] = red;
				flattenedRgbValues[(y * width + x)] = red;

				double green = ((double) color.getGreen()) / 256d;
				greenValues[y][x] = green;
				flattenedRgbValues[(width * height + y * width + x)] = green;

				double blue = ((double) color.getBlue()) / 256d;
				blueValues[y][x] = blue;
				flattenedRgbValues[(2 * width * height + y * width + x)] = blue;
			}
		}
	}

        /**
         * Converts image rgb data to binary black and white data
         * @param inputRGB flatten rgb data
         * @return binary black and white representation of image
         */
        public static double[] convertRgbInputToBinaryBlackAndWhite(double[] inputRGB) {
            double inputBinary[]= new double[inputRGB.length/3];

            for(int i=0; i<inputRGB.length/3; i+=3) {
                if (inputRGB[i]>0) inputBinary[i] = 0;
                    else inputBinary[i] = 1;
            }

            return inputBinary;
        }

        /**
         * Get image width
         * @return image width
         */
	public int getWidth()
	{
		return width;
	}

        /**
         * Get image height
         * @return image height
         */
	public int getHeight()
	{
		return height;
	}

	/**
	 * Returns red color component for the entire image
	 * @return 2d array in the form: [row][column]
	 */
	public double[][] getRedValues()
	{
		return redValues;
	}

	/**
	 * Returns green color component for the entire image
	 * @return 2d array in the form: [row][column]
	 */
	public double[][] getGreenValues()
	{
		return greenValues;
	}

	/**
	 * Returns blue color component for the entire image
	 * @return 2d array in the form: [row][column]
	 */
	public double[][] getBlueValues()
	{
		return blueValues;
	}

	/**
	 * Returns rgb data in a form: all red rows, all green rows, all blue rows
	 * @return All the red rows, followed by all the green rows, 
	 * followed by all the blue rows.
	 */
	public double[] getFlattenedRgbValues()
	{
		return flattenedRgbValues;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null || !(obj instanceof FractionRgbData)) {
			return false;
		}
		FractionRgbData other = (FractionRgbData) obj;
		return Arrays.equals(flattenedRgbValues, other.getFlattenedRgbValues());
	}
	
	@Override
	public int hashCode()
	{
		return Arrays.hashCode(flattenedRgbValues);
	}
	
	@Override
	public String toString()
	{
		return Arrays.toString(flattenedRgbValues);
	}
}