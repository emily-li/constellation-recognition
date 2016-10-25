package com.liemily.neuralnetwork.networks;

import com.liemily.neuralnetwork.layers.NeuronLayer;
import com.liemily.neuralnetwork.learning.LearningRule;
import com.liemily.neuralnetwork.learning.SupervisedLearningInterface;
import com.liemily.neuralnetwork.training.TrainingPair;
import com.liemily.neuralnetwork.training.TrainingSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of a supervised feed forward network.
 * @author Emily Li
 */
public class SupervisedFeedForwardNetwork extends NeuralNetwork implements SupervisedLearningInterface {
    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    public SupervisedFeedForwardNetwork(LearningRule learningRule, int inputNodeSize, int hiddenNodeSize, int hiddenLayerCount, int outputNodeSize, double initWeightRange) {
        super(learningRule, inputNodeSize, hiddenNodeSize, hiddenLayerCount, outputNodeSize, initWeightRange);
    }

    /**
     * Activates the neural network in a feed forward fashion.
     * The activation starts from the input layer and alters the values moving forward,
     * utilising the learning rule's lambda function and weights provided by the ConnectionLayer's
     */
    @Override
    public void activate() {
        logger.trace("Activating network");
        List<NeuronLayer> neuronLayers = getNeuronLayers();
        for (int i = 1; i < neuronLayers.size(); i++) {
            logger.trace("Neuron layer values for layer " + i + " was " + Arrays.toString(getNeuronLayers().get(i).getNodes()));
            NeuronLayer neuronLayer = neuronLayers.get(i);
            NeuronLayer connectingLayer = neuronLayers.get(i-1);

            for (int j = 0; j < neuronLayer.size(); j++) {
                double sum = 0.0;
                for (int k = 0; k < connectingLayer.size(); k++) {
                    double weight = getConnectionLayers().get(i-1).getWeights()[k][j];
                    sum += connectingLayer.getNodes()[k] * (weight == 0 ? 1 : weight);
                }
                neuronLayer.getNodes()[j] = getLearningRule().lambda(sum);
            }
            logger.trace("Neuron layer values for layer " + i + " is now " + Arrays.toString(getNeuronLayers().get(i).getNodes()));
        }
        logger.trace("Activated");
    }

    /**
     * Trains the network with a training set by iterating over the full set multiple times
     * @param trainingSet TrainingSet, essentially of key-value pairs
     * @param iterations Number of times to use the same data
     * @return Returns the error produced from the last iteration
     */
    @Override
    public double train(TrainingSet trainingSet, int iterations) {
        double finalError = 0.0;
        for (int i = 0 ; i < iterations; i++) {
            logger.info("Training iteration " + (i+1) + "/" + iterations);
            double error = train(trainingSet);
            finalError = error;
        }
        logger.info("Final error of the total training set after training: " + finalError);
        return finalError;
    }

    /**
     * Trains the neural network on a training set, iterating over its TrainingPair's
     * @param trainingSet TrainingSet, essentially of key-value pairs
     * @return Returns the total error produced by training all TrainingPair's
     */
    @Override
    public double train(TrainingSet trainingSet) {
        double error = 0.0;
        for (TrainingPair trainingPair : trainingSet.getTrainingPairs()) {
            error += train(trainingPair);
        }
        logger.debug("Error for the training set was " + error);
        return error;
    }

    /**
     * Trains on a single TrainingPair by first setting the neural network's values with that of the TrainingPair's input,
     * activating the network using this input,
     * then using this first iteration as the start of the training performed by the learning rule.
     * @param trainingPair Key-value pair of input and desired output
     * @return Returns the error produced by teaching the network as per the LearningRule
     */
    @Override
    public double train(TrainingPair trainingPair) {
        logger.debug("Training with training pair '" + trainingPair.getName() + "'");
        setInputValues(trainingPair.getInput());
        activate();
        logger.debug("Output layer is " + getOutputLayer());
        logger.debug("Desired output is " + trainingPair.getOutput());
        double error = getLearningRule().teach(this, trainingPair.getOutput());
        logger.trace("Error for the training pair was " + error);
        return error;
    }
}
