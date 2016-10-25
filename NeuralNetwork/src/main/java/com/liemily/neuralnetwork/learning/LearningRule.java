package com.liemily.neuralnetwork.learning;

import com.liemily.neuralnetwork.layers.NeuronLayer;
import com.liemily.neuralnetwork.networks.NeuralNetwork;

/**
 * Interface for a learning rule to be used by a neural network, specifying how its values and weights should be altered
 * @author Emily Li
 */
public interface LearningRule {
	/**
	 * Teaches the neural network according to the desired target layer
	 * @param neuralNetwork Neural network to be trained
	 * @param target Target output layer
	 * @return Initial error before
	 */
	double teach(NeuralNetwork neuralNetwork, NeuronLayer target);

	/**
	 * Returns the difference between the node value and the target value
	 * @param value Value representing the activation value of a node
	 * @param target Target value of the target layer
	 * @return Returns the difference and applies any additional logic depending on the implementation
	 */
	double getDelta(double value, double target);

	/**
	 * Returns the total error between a neuron layer and the target layer
	 * @param neuronLayer NeuronLayer passed in by the user
	 * @param targetLayer Desired target layer
	 * @return Total error between the actual neuron layer and target layer
	 */
	double getError(NeuronLayer neuronLayer, NeuronLayer targetLayer);

	/**
	 * Normalisation function intended to be applied to node values
	 * @param input Node value
	 * @return Normalised value
	 */
	double lambda(double input);

	/**
	 * Derivative of the lambda function
	 * @param input Node value
	 * @return Returns the lambda derivative so that it can be used to produce delta
	 */
	double lambdaDerivative(double input);
}
