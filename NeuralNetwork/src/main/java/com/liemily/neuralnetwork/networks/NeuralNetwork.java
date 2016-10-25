package com.liemily.neuralnetwork.networks;

import com.liemily.neuralnetwork.layers.NeuronLayer;
import com.liemily.neuralnetwork.layers.ConnectionLayer;
import com.liemily.neuralnetwork.learning.LearningRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Array based neural network that avoids memory intensive objects being created.
 * This approach was taken rather than a full OO approach as optimisation problems came up during training
 *
 * To keep the classes following OO principles, classes act as wrappers around arrays
 *
 * @author Emily Li
 */
public abstract class NeuralNetwork {
    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private LearningRule learningRule;
    private List<NeuronLayer> neuronLayers;
    private List<ConnectionLayer> connectionLayers;

    public NeuralNetwork(LearningRule learningRule, int inputNodeSize, int hiddenNodeSize, int hiddenLayerCount, int outputNodeSize, double initWeightRange) {
        logger.debug("Creating neural network");

        this.learningRule = learningRule;

        NeuronLayer inputLayer = new NeuronLayer(inputNodeSize);
        List<NeuronLayer> hiddenLayers = new ArrayList<>();
        IntStream.range(0, hiddenLayerCount).forEach(i -> hiddenLayers.add(new NeuronLayer(hiddenNodeSize)));
        NeuronLayer outputLayer = new NeuronLayer(outputNodeSize);

        neuronLayers = new ArrayList<>();
        neuronLayers.add(inputLayer);
        neuronLayers.addAll(hiddenLayers);
        neuronLayers.add(outputLayer);

        ConnectionLayer inputToHiddenLayer = new ConnectionLayer(inputLayer, hiddenLayers.get(0), initWeightRange);
        List<ConnectionLayer> hiddenConnectionLayers = new ArrayList<>();
        NeuronLayer connectingLayer = hiddenLayers.get(0);
        for (int i = 1; i < hiddenLayers.size(); i++) {
            ConnectionLayer connectionLayer = new ConnectionLayer(connectingLayer, hiddenLayers.get(i), initWeightRange);
            hiddenConnectionLayers.add(connectionLayer);
            connectingLayer = hiddenLayers.get(i);
        }
        ConnectionLayer hiddenToOutputLayer = new ConnectionLayer(connectingLayer, outputLayer, initWeightRange);

        connectionLayers = new ArrayList<>();
        connectionLayers.add(inputToHiddenLayer);
        connectionLayers.addAll(hiddenConnectionLayers);
        connectionLayers.add(hiddenToOutputLayer);

        logger.debug("Completed neural network creation");
    }

    /**
     * Activates the neural network, sending signals down the neuron layers and relevant connections
     */
    public abstract void activate();

    public NeuronLayer getInputLayer() {
        return neuronLayers.get(0);
    }

    public NeuronLayer getOutputLayer() { return neuronLayers.get(neuronLayers.size()-1); }

    public List<NeuronLayer> getNeuronLayers() {
        return neuronLayers;
    }

    public List<ConnectionLayer> getConnectionLayers() {
        return connectionLayers;
    }

    public LearningRule getLearningRule() {
        return learningRule;
    }

    public void setInputValues(NeuronLayer inputLayer) {
        IntStream.range(0, getInputLayer().size()).forEach(i -> getInputLayer().getNodes()[i] =
                i > inputLayer.size() - 1 ? getInputLayer().getNodes()[i] : inputLayer.getNodes()[i]);
    }
}
