package com.liemily.neuralnetwork.training;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Simple wrapper of TrainingPairs with which to train a neural network
 * @author Emily Li
 */
public class TrainingSet {
	private Collection<TrainingPair> trainingPairs;

	public TrainingSet() {
		trainingPairs = new ArrayList<>();
	}

	public TrainingSet(Collection<TrainingPair> trainingPairs) {
		this.trainingPairs = trainingPairs;
	}
	
	public Collection<TrainingPair> getTrainingPairs() {
		return trainingPairs;
	}

	public void addTrainingPair(TrainingPair trainingPair) { trainingPairs.add(trainingPair); }
}
