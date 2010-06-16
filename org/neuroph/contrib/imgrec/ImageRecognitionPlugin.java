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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.neuroph.core.Neuron;
import org.neuroph.core.exceptions.VectorSizeMismatchException;
import org.neuroph.util.plugins.LabelsPlugin;
import org.neuroph.util.plugins.PluginBase;

/**
 * Provides image recognition specific properties like sampling resolution, and easy to
 * use image recognition interface for neural network.
 *
 * @author Jon Tait
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class ImageRecognitionPlugin extends PluginBase implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String IMG_REC_PLUGIN_NAME = "Image Recognition Plugin";

	/**
	 * Image sampling resolution (image dimensions)
	 */
	private Dimension samplingResolution;

        /**
         * Color mode used for recognition (full color or black and white)
         */
        private ColorMode colorMode;

	/**
	 * Constructor
	 * 
	 * @param samplingResolution
	 *            image sampling resolution (dimensions)
	 */
	public ImageRecognitionPlugin(Dimension samplingResolution) {
		super(IMG_REC_PLUGIN_NAME);
		this.samplingResolution = samplingResolution;
                this.colorMode = ColorMode.FULL_COLOR;
	}

	/**
	 * Constructor
	 *
	 * @param samplingResolution
	 *            image sampling resolution (dimensions)
         * @param colorMode recognition color mode 
	 */
	public ImageRecognitionPlugin(Dimension samplingResolution, ColorMode colorMode) {
		super(IMG_REC_PLUGIN_NAME);
		this.samplingResolution = samplingResolution;
                this.colorMode = colorMode;
	}

	/**
	 * Returns image sampling resolution (dimensions)
	 * 
	 * @return image sampling resolution (dimensions)
	 */
	public Dimension getSamplingResolution() {
		return samplingResolution;
	}

        /**
         * Returns color mode used for image recognition
         * @return color mode used for image recognition
         */
        public ColorMode getColorMode() {
            return this.colorMode;
        }

	/**
	 * Sets network input (image to recognize) from the specified BufferedImage
	 * object
	 * 
	 * @param img
	 *            image to recognize
	 */
	public void setInput(BufferedImage img) throws ImageSizeMismatchException {
		FractionRgbData imgRgb = new FractionRgbData(ImageSampler
				.downSampleImage(samplingResolution, img));
		double input[];

		if (this.colorMode == ColorMode.FULL_COLOR)
			input = imgRgb.getFlattenedRgbValues();
		else if (this.colorMode == ColorMode.BLACK_AND_WHITE)
			input = FractionRgbData.convertRgbInputToBinaryBlackAndWhite(imgRgb
					.getFlattenedRgbValues());
		else
			throw new RuntimeException("Unknown color mode!");

                try {
                    this.getParentNetwork().setInput(input);
                } catch (VectorSizeMismatchException vsme) {
                    throw new ImageSizeMismatchException(vsme);
                }
	}

	/**
	 * Sets network input (image to recognize) from the specified File object
	 * 
	 * @param imgFile
	 *            file of the image to recognize
	 */
	public void setInput(File imgFile) throws IOException, ImageSizeMismatchException {
		BufferedImage img = ImageIO.read(imgFile);
		this.setInput(img);
	}

	/**
	 * Sets network input (image to recognize) from the specified URL object
	 * 
	 * @param imgURL
	 *            url of the image
	 */
	public void setInput(URL imgURL) throws IOException, ImageSizeMismatchException{
		BufferedImage img = ImageIO.read(imgURL);
		this.setInput(img);
	}

        public void processInput() {
                getParentNetwork().calculate();
        }

	/**
	 * Returns image recognition result as map with image labels as keys and
	 * recogition result as value
	 * 
	 * @return image recognition result
	 */
	public HashMap<String, Double> getOutput() {
		LabelsPlugin labelsPlugin = (LabelsPlugin) this.getParentNetwork()
				.getPlugin(LabelsPlugin.LABELS_PLUGIN_NAME);
		HashMap<String, Double> networkOutput = new HashMap<String, Double>();

		for (Neuron neuron : this.getParentNetwork().getOutputNeurons()) {
			String neuronLabel = labelsPlugin.getLabel(neuron);
			networkOutput.put(neuronLabel, neuron.getOutput());
		}

		return networkOutput;
	}


	/**
	 * This method performs the image recognition for specified image.
	 * Returns image recognition result as map with image labels as keys and
	 * recogition result as value
	 *
	 * @return image recognition result
	 */
        public HashMap<String, Double> recognizeImage(BufferedImage img) throws ImageSizeMismatchException {
		setInput(img);
		processInput();
                return getOutput();
        }

	/**
	 * This method performs the image recognition for specified image file.
	 * Returns image recognition result as map with image labels as keys and
	 * recogition result as value
	 *
	 * @return image recognition result
	 */
        public HashMap<String, Double> recognizeImage(File imgFile)  throws IOException, ImageSizeMismatchException {
		setInput(imgFile);
		processInput();
                return getOutput();
        }

	/**
	 * This method performs the image recognition for specified image URL.
	 * Returns image recognition result as map with image labels as keys and
	 * recogition result as value
	 *
	 * @return image recognition result
	 */
        public HashMap<String, Double> recognizeImage(URL imgURL)  throws IOException, ImageSizeMismatchException {
		setInput(imgURL);
		processInput();
                return getOutput();
        }

	/**
	 * Returns one or more image labels with the maximum output - recognized
	 * images
	 * 
	 * @return one or more image labels with the maximum output
	 */
	public HashMap<String, Neuron> getMaxOutput() {
		HashMap<String, Neuron> maxOutput = new HashMap<String, Neuron>();
		Neuron maxNeuron = this.getParentNetwork().getOutputNeurons()
				.elementAt(0);

		for (Neuron neuron : this.getParentNetwork().getOutputNeurons()) {
			if (neuron.getOutput() > maxNeuron.getOutput())
				maxNeuron = neuron;
		}

		LabelsPlugin labels = (LabelsPlugin) this.getParentNetwork().getPlugin(
				LabelsPlugin.LABELS_PLUGIN_NAME);

		maxOutput.put(labels.getLabel(maxNeuron), maxNeuron);

		for (Neuron neuron : this.getParentNetwork().getOutputNeurons()) {
			if (neuron.getOutput() == maxNeuron.getOutput()) {
				maxOutput.put(labels.getLabel(neuron), neuron);
			}
		}

		return maxOutput;
	}

}