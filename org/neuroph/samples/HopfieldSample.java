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

package org.neuroph.samples;

import java.util.Vector;

import org.neuroph.core.learning.TrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.Hopfield;

/**
 * This sample shows how to create and train Hopfield neural network
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class HopfieldSample {

    /**
     * Runs this sample
     */
    public static void main(String args[]) {

        // create training set (H and T letter in 3x3 grid)
        TrainingSet trainingSet = new TrainingSet();
        trainingSet.addElement(new TrainingElement(new double[]{1, 0, 1, 
                                                                1, 1, 1,
                                                                1, 0, 1})); // H letter
        
        trainingSet.addElement(new TrainingElement(new double[]{1, 1, 1,
                                                                0, 1, 0,
                                                                0, 1, 0})); // T letter
  
        // create hopfield network
        Hopfield myHopfield = new Hopfield(9);
        // learn the training set
        myHopfield.learnInSameThread(trainingSet);

        // test hopfield network
        System.out.println("Testing network");

        // add one more 'incomplete' H pattern for testing - it will be recognized as H
        trainingSet.addElement(new TrainingElement(new double[]{1, 0, 0,
                                                                1, 0, 1,
                                                                1, 0, 1}));


        // print network output for the each element from the specified training set.
        for(TrainingElement trainingElement : trainingSet.trainingElements()) {
            myHopfield.setInput(trainingElement.getInput());
            myHopfield.calculate();
            myHopfield.calculate();   
            Vector<Double> networkOutput = myHopfield.getOutput();

            System.out.print("Input: " + trainingElement.getInput());
            System.out.println(" Output: " + networkOutput);
        }

    }

}