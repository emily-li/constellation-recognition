package com.liemily.neuralnetwork.learning;

import com.liemily.neuralnetwork.networks.NeuralNetwork;
import com.liemily.neuralnetwork.layers.NeuronLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * Learning rule enabling back propagation through the network to correct error
 * @author Emily Li
 */
public class BackPropagationLearningRule implements LearningRule {
    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private double learningRate;

    public BackPropagationLearningRule(double learningRate) {
        this.learningRate = learningRate;
    }

    /**
     * Goes through the neural network's layers from the back,
     * i.e. output layer, hidden layers in reverse order, then input layer
     * correcting the values according to the specified delta function
     * and weights according to the change and learning rate
     * @param neuralNetwork NeuralNetwork to be trained. This is expected to be a feed forward network.
     * @param target Desired output layer representation
     * @return Returns the initial error between the neural network's output layer and the given target
     */
    public double teach(NeuralNetwork neuralNetwork, NeuronLayer target) {
        List<NeuronLayer> neuronLayers = neuralNetwork.getNeuronLayers();

        NeuronLayer outputLayer = neuralNetwork.getOutputLayer();

        double[] connectingDelta = new double[outputLayer.size()];
        for (int i = 0; i < outputLayer.size(); i++) {
            connectingDelta[i] = getDelta(outputLayer.getNodes()[i], target.getNodes()[i]);
        }

        NeuronLayer connectingLayer = outputLayer;
        for (int i = neuronLayers.size() - 2; i >= 0; i--) {
            NeuronLayer hiddenLayer = neuronLayers.get(i);
            double[] hiddenDelta = new double[hiddenLayer.size()];
            double[][] connectingWeights = neuralNetwork.getConnectionLayers().get(i).getWeights();

            for (int j = 0; j < hiddenLayer.size(); j++) {
                double error = 0.0;
                for (int k = 0; k < connectingLayer.size(); k++) {
                    error += connectingDelta[k] * connectingWeights[j][k];
                    double change = connectingDelta[k] * hiddenLayer.getNodes()[j];
                    connectingWeights[j][k] += learningRate * change;
                }
                hiddenDelta[j] = lambdaDerivative(hiddenLayer.getNodes()[j]) * error;
            }
            neuralNetwork.getConnectionLayers().get(i).setWeights(connectingWeights);
            connectingDelta = hiddenDelta;
            connectingLayer = hiddenLayer;
        }
        double error = getError(outputLayer, target);
        return error;
    }

    @Override
    public double getDelta(double value, double target) {
        double error = target - value;
        double delta = lambdaDerivative(value) * error;
        return delta;
    }

    @Override
    public double getError(NeuronLayer neuronLayer, NeuronLayer targetLayer) {
        double error = 0.0;
        for (int i = 0; i < neuronLayer.size(); i++) {
            double actualVal = neuronLayer.getNodes()[i];
            double targetVal = targetLayer.getNodes()[i];
            error += learningRate * Math.pow(targetVal - actualVal, 2);
        }
        return error;
    }

    @Override
    public double lambda(double val) {
        double lambda = 1 / (1 + Math.exp(val*-1));
        return lambda;
    }

    @Override
    public double lambdaDerivative(double val) {
        double deriv = 1 - lambda(val);
        return deriv;
    }
}
