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

package org.neuroph.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingElement;
import org.neuroph.core.learning.TrainingSet;


/**
 * Handles training set imports
 *
 * @author Zoran Sevarac
 * @author Ivan Nedeljkovic
 * @author Kokanovic Rados
 */

// TODO: importFromDatabase(sql, ...) and importFromUrl(url, ...)
public class TrainingSetImport
{
  
  public static TrainingSet importFromFile(String filePath, int inputsCount, int outputsCount, String separator)
    throws IOException, FileNotFoundException, NumberFormatException
  {

    FileReader fileReader = null;

    try {
     TrainingSet trainingSet = new TrainingSet();
     fileReader = new FileReader(new File(filePath));
     BufferedReader reader = new BufferedReader(fileReader);

     String line = "";
      
      while((line = reader.readLine())!=null) {
        Vector<Double> inputs = new Vector<Double>();
        Vector<Double> outputs = new Vector<Double>();
        String[] values = line.split(separator);

        if (values[0].equals("")) continue; // skip if line was empty

        for (int i = 0; i < inputsCount; i++)
          inputs.add(Double.valueOf(Double.parseDouble(values[i])));

        for (int i = inputsCount; i < inputsCount + outputsCount; i++)
          outputs.add(Double.valueOf(Double.parseDouble(values[i])));

        if (outputsCount>0) {
              trainingSet.addElement(new SupervisedTrainingElement(inputs, outputs));
        } else {
              trainingSet.addElement(new TrainingElement(inputs));
        }
      }

      return trainingSet;
      
    } catch (FileNotFoundException ex) {
       ex.printStackTrace();
       throw ex;
    } catch(IOException ex) {
    	if(fileReader != null) {
    		fileReader.close();
    	}
    	ex.printStackTrace();
    	throw ex;
    } catch (NumberFormatException ex) {
       fileReader.close();
       ex.printStackTrace();
       throw ex;
    }
  }

}