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

import java.io.File;

/**
 * The utility class that is used to create training set
 *
 * @author Damir Kocic
 */
public class SaveImage {

    /**
     * Private constructor to provent the creation of this class.
     * This class sholud be used in the static way
     */
    private SaveImage(){ }

    /**
     * Finds out the number of allready created letters for the traing set
     *
     * @param letter String that represens the wanted letter
     *
     * @return the number of allready existing letters
     */
    public static int numberOfFiles(String letter) {
        File f = new File("Letters/Training Set/");
        File[] files = f.listFiles();
        int numberOfFiles = 0;
        for (int i = 0; i < files.length; i++) {
            if(files[i].getName().startsWith(letter)) {
                numberOfFiles++;
            }
        }
        return numberOfFiles;
    }

    /**
     * This method creates the folder that will conatin all of the letters
     * designated for the training set
     */
    public static void createTrainingSetFolder() {
        File f = new File("Letters");
        f.mkdir();
        File f1 = new File("Letters/Training Set");
        f1.mkdir();
    }
}
