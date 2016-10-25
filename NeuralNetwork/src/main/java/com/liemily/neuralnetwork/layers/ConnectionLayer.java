package com.liemily.neuralnetwork.layers;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Wrapper class for weights between multiple NeuronLayers
 *
 * The weights are represented by a matrix
 * @author Emily Li
 */
public class ConnectionLayer {
    private double[][] weights;

    public ConnectionLayer(NeuronLayer incomingLayer, NeuronLayer outgoingLayer, double initWeightRange) {
        weights = new double[incomingLayer.size()][outgoingLayer.size()];
        populateWeights(weights, initWeightRange);
    }

    /**
     * Constructs random weights for NeuronLayer's when the ConnectionLayer is first initialised.
     * @param weights Matrix representing weights between NeuronLayer's
     * @param initWeightRange Permitted range around 0 for the weights
     */
    private void populateWeights(double[][] weights, double initWeightRange) {
        double randOrigin = 0.0 - initWeightRange / 2.0;
        double randBound = 0.0 + initWeightRange / 2.0;
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[0].length; j++) {
                double random = ThreadLocalRandom.current().nextDouble(randOrigin, randBound);
                weights[i][j] = random;
            }
        }
    }

    public double[][] getWeights() {
        return weights;
    }

    public void setWeights(double[][] weights) { this.weights = weights; }
}
