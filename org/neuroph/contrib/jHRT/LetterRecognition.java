/***
 * Neuroph  http://neuroph.sourceforge.net
 * Copyright by Neuroph Project (C) 2008
 *
 * This file is part of Neuroph framework.
 *
 * Neuroph is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Neuroph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Neuroph. If not, see <http://www.gnu.org/licenses/>.
 */

package org.neuroph.contrib.jHRT;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.contrib.imgrec.ImageRecognitionPlugin;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 *
 * This class loads the neural network and passes the image of a letter
 * to the netowork after which it process the resault of the recognition process
 *
 * @author Boris Horvat
 */
public class LetterRecognition {

    /** holds the path to the neural network */
    private static final String RESOURCE_PATH = "/org/neuroph/contrib/jHRT/resources/neuralNetwork.nnet";
    /** The NeuralNetwork that is in charge of the recognition process   */
    private NeuralNetwork nnet;

    /**
     * The public contructor that loads the network
     */
    public LetterRecognition() {
        // load trained neural network saved with easyNeurons (specify existing neural network file here)
        InputStream nnetStream = this.getClass().getResourceAsStream(RESOURCE_PATH);
        nnet = NeuralNetwork.load(nnetStream);
    }

    public LetterRecognition(String file){
        // load trained neural network saved with easyNeurons (specify existing neural network file here)
        nnet = NeuralNetwork.load(file);
    }

    /**
     * This method is used to recogize the letter from the image. It first passes the image 
     * to the trained neural network, and when it recives the resault of the recognition it passes 
     * the resault to the list model
     *
     * @param model represents the list model in which the resault of the regonition is put
     */
    public void recognize(DefaultListModel model) {

        // get the image recognition plugin from neural network
        ImageRecognitionPlugin imageRecognition =
                (ImageRecognitionPlugin) nnet.getPlugin(ImageRecognitionPlugin.IMG_REC_PLUGIN_NAME);

        try {
            // image recognition is done here (specify some existing image file)
            HashMap<String, Double> output = imageRecognition.recognizeImage(new File("letter.png"));
            String[] regonition = reogranize(sortHashMapByValuesD(output).toString());

            for (int i = 0; i < regonition.length; i++) {
                model.addElement(regonition[i]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Private method that is used to remove unwanted infromation form the resault,
     * it also splits the information into separate segments
     *
     * @param output the resault of the recognition process
     * 
     * @return the array of Strings that represents single letter
     */
    private String[] reogranize(String output) {
        return output.substring(1, output.length() - 1).split(", ");
    }

    /**
     * This private method sorts the result of the recogntion process, in order to 
     * see which letter has the highest probability
     *
     * @param passedMap the HashMap that holds the resault of the recognition process
     *
     * @return LinkedHashMap that represents the combination of letters with the
     *                       probability of the correct recognition
     */
    private LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
        List mapKeys = new ArrayList(passedMap.keySet());
        List mapValues = new ArrayList(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);
        Collections.reverse(mapValues);

        LinkedHashMap sortedMap =
                new LinkedHashMap();

        Iterator valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Object val = valueIt.next();
            Iterator keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                String comp1 = passedMap.get(key).toString();
                String comp2 = val.toString();

                if (comp1.equals(comp2)) {
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put((String) key, (Double) val);
                    break;
                }

            }

        }
        return sortedMap;
    }
}
