/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.neuroph.nnet.comp;

import org.neuroph.core.Neuron;

/**
 *
 * @author zoran
 */
public class InputNeuron extends Neuron {

    public InputNeuron() {
      //  this.setTran
    }



    public void calculate() {
        this.output = this.netInput;
    }

}
