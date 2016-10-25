package com.liemily.neuralnetwork.learning;

import com.liemily.neuralnetwork.training.TrainingPair;
import com.liemily.neuralnetwork.training.TrainingSet;

/**
 * Interface for supervised neural networks, providing the facility to use training sets to reach a trained state
 * @author Emily Li
 */
public interface SupervisedLearningInterface {
	/**
	 * Takes a training set with which to train the network, utilising the desired number of iterations
	 * @param trainingSet TrainingSet, essentially of key-value pairs
	 * @param iterations Number of times to use the same data
	 * @return Returns the error of the last iteration
	 */
	double train(TrainingSet trainingSet, int iterations);

	/**
	 * Trains the neural network using the given training set
	 * @param trainingSet TrainingSet, essentially of key-value pairs
	 * @return Returns the final error
	 */
	double train(TrainingSet trainingSet);

	/**
	 * Trains the neural network on a single TrainingPair
	 * @param trainingPair Key-value pair of input and desired output
	 * @return Returns the error after training
	 */
	double train(TrainingPair trainingPair);
}
