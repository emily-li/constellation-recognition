package com.liemily.neuralnetwork.training;

import com.liemily.neuralnetwork.layers.NeuronLayer;

/**
 * Simple wrapper for the desired input and output of a valid recognition from the neural network
 * @author Emily Li
 */
public class TrainingPair {
	private Enum name;
	private final NeuronLayer input;
	private final NeuronLayer output;

	public TrainingPair(NeuronLayer input, NeuronLayer output) {
		this.input = input;
		this.output = output;
	}

	public TrainingPair(Enum name, NeuronLayer input, NeuronLayer output) {
		this.name = name;
		this.input = input;
		this.output = output;
	}

	public Enum getName() {
		return name;
	}

	public NeuronLayer getInput() {
		return input;
	}

	public NeuronLayer getOutput() {
		return output;
	}
}
