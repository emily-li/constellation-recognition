package com.liemily.neuralnetwork.networks;

import com.liemily.neuralnetwork.layers.NeuronLayer;
import com.liemily.neuralnetwork.learning.BackPropagationLearningRule;
import com.liemily.neuralnetwork.training.TrainingPair;
import com.liemily.neuralnetwork.training.TrainingSet;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test class for the 'Neural Network' component as described in the Functional Test Plan
 * @author Emily Li
 *
 */
public class SupervisedFeedForwardNetworkTest {
	private static int layerSize;
	private static SupervisedFeedForwardNetwork neuralNetwork;

	@BeforeClass
	public static void setupBeforeClass() {
		layerSize = 5;
		BackPropagationLearningRule learningRule = new BackPropagationLearningRule(0.5);
		neuralNetwork = spy(new SupervisedFeedForwardNetwork(learningRule, layerSize, layerSize, 1, layerSize, 0.5));
	}

	// 7. A network must be able to activate all its layers' nodes
	@Test
	public void testLayerCanActivateAllNodes() {
		List<NeuronLayer> layers = new ArrayList<>();
		neuralNetwork.getNeuronLayers().forEach(layer -> layers.add(spy(layer)));

		when(neuralNetwork.getNeuronLayers()).thenReturn(layers);
		when(neuralNetwork.getInputLayer()).thenReturn(layers.get(0));
		when(neuralNetwork.getOutputLayer()).thenReturn(layers.get(layers.size()-1));

		List<double[]> prevLayerValues = new ArrayList<>();
		List<double[]> newLayerValues = new ArrayList<>();
		layers.forEach(layer -> prevLayerValues.add(layer.getNodes().clone()));

		neuralNetwork.activate();
		neuralNetwork.getNeuronLayers().forEach(layer -> newLayerValues.add(layer.getNodes().clone()));

		assertThat(prevLayerValues.get(1), IsNot.not(IsEqual.equalTo(newLayerValues.get(1))));
	}

	// 11. The implemented network must be able to take a training set
	// 	   and use it with the learning rule to reduce its error over time
	@Test
	public void testErrorReducedThroughTraining() {
		NeuronLayer outputLayer = new NeuronLayer(layerSize);
		double[] outputValues = new double[layerSize];
		Arrays.fill(outputValues, 0.1);
		outputLayer.setNodes(outputValues);

		TrainingPair trainingPair = new TrainingPair(new NeuronLayer(layerSize), outputLayer);
		TrainingSet trainingSet = new TrainingSet(Collections.singletonList(trainingPair));

		neuralNetwork.setInputValues(trainingPair.getInput());
		neuralNetwork.activate();
		double initialError = neuralNetwork.getLearningRule().getError(neuralNetwork.getOutputLayer(), trainingPair.getOutput());
		neuralNetwork.train(trainingSet, 100);
		neuralNetwork.activate();
		double latterError = neuralNetwork.getLearningRule().getError(neuralNetwork.getOutputLayer(), trainingPair.getOutput());

		assertTrue(latterError < initialError);
	}
}
