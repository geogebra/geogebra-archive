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

package org.neuroph.samples.stockmarket;

import java.util.Vector;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.util.TrainingSetImport;

/**
 * The part of simple stock market components, easy to use
 * stock market interface for neural network. Provides method to import stock training set froman array
 *
 * @author Valentin Steinhauer <valentin.steinhauer@t-online.de>
 * @author Zoran Sevarac <sevarac@gmail.com>
 */
public class StockTrainingSetImport extends TrainingSetImport {

    /**
     * Creates and returns training set for stock market prediction using the provided data from array
     * @param values an array containing stock data
     * @param inputsCount training element (neural net) inputs count
     * @param outputsCount training element (neural net) ouputs count
     * @return training set with stock data
     */
    public static TrainingSet importFromArray(double[] values, int inputsCount, int outputsCount) {
        TrainingSet trainingSet = new TrainingSet(inputsCount, outputsCount);
        for (int i = 0; i < values.length - inputsCount; i++) {
            Vector<Double> inputs = new Vector<Double>();
            for (int j = i; j < i + inputsCount; j++) {
                inputs.add(values[j]);
            }
            Vector<Double> outputs = new Vector<Double>();
            if (outputsCount > 0 && i + inputsCount + outputsCount <= values.length) {
                for (int j = i + inputsCount; j < i + inputsCount + outputsCount; j++) {
                    outputs.add(values[j]);
                }
                if (outputsCount > 0) {
                    trainingSet.addElement(new SupervisedTrainingElement(inputs, outputs));
                } else {
                    trainingSet.addElement(new TrainingElement(inputs));
                }
            }
        }
        return trainingSet;
    }
}
