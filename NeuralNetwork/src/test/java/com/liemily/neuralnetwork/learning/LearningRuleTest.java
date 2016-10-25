package com.liemily.neuralnetwork.learning;

import java.util.*;

import com.liemily.neuralnetwork.layers.NeuronLayer;
import com.liemily.neuralnetwork.networks.NeuralNetwork;
import com.liemily.neuralnetwork.networks.SupervisedFeedForwardNetwork;
import org.junit.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test class for the 'Neural Network Learning Rule' component as described in the Functional Test Plan
 * @author Emily Li
 *
 */
public class LearningRuleTest {
    private static LearningRule learningRule;
    private List<NeuronLayer> layers;
    private NeuralNetwork neuralNetwork;
    private NeuronLayer targetLayer;

    @BeforeClass
    public static void setupBeforeClass() {
        learningRule = spy(new BackPropagationLearningRule(0.5));
    }

    @Before
    public void setup() {
        neuralNetwork = spy(new SupervisedFeedForwardNetwork(learningRule, 5, 5, 1, 5, 0.5));

        layers = new ArrayList<>();
        neuralNetwork.getNeuronLayers().forEach(layer -> layers.add(spy(layer)));

        when(neuralNetwork.getNeuronLayers()).thenReturn(layers);
        when(neuralNetwork.getInputLayer()).thenReturn(layers.get(0));
        when(neuralNetwork.getOutputLayer()).thenReturn(layers.get(layers.size()-1));

        targetLayer = new NeuronLayer(5);
        double[] targetValues = new double[neuralNetwork.getOutputLayer().size()];
        Arrays.fill(targetValues, 0.3);
        targetLayer.setNodes(targetValues);
    }

	// 8. The realised learning rule must be able to traverse over a neural network
	@Test
	public void testLearningRuleCanTraverseNetwork() {
	    learningRule.teach(neuralNetwork, neuralNetwork.getOutputLayer());
        layers.forEach(hiddenLayer -> verify(hiddenLayer, atLeastOnce()).getNodes());
	}

	// 9. The realised learning rule must be able to alter the values of a neuron layer's weights
	@Test
	public void testLearningRuleCanAlterWeight() {
        double[][] previousWeights = neuralNetwork.getConnectionLayers().get(0).getWeights().clone();
        double[][] previousWeightsCopy = new double[previousWeights.length][previousWeights[0].length];
        for (int i = 0; i < previousWeights.length; i++) {
            for (int j = 0; j < previousWeights[i].length; j++) {
                previousWeightsCopy[i][j] = previousWeights[i][j];
            }
        }

        learningRule.teach(neuralNetwork, targetLayer);

        double[][] latterWeights = neuralNetwork.getConnectionLayers().get(0).getWeights();

        assertNotEquals(previousWeightsCopy, latterWeights);
	}

    // 10. The realised learning rule must be able to compare the values of neurons between different neuron layers
    @Test
    public void testLearningRuleCanAccessMultipleLayersNeurons() {
        learningRule.teach(neuralNetwork, targetLayer);
        NeuronLayer outputLayer = new NeuronLayer(targetLayer.size());

        for (int i = 0; i < neuralNetwork.getNeuronLayers().size()-1; i++) { // Exclude output layer
            NeuronLayer neuronLayer = neuralNetwork.getNeuronLayers().get(i);
            for (int j = 0; j < neuronLayer.getNodes().length; j++) {
                verify(learningRule, atLeastOnce()).getDelta(neuronLayer.getNodes()[j], targetLayer.getNodes()[j]);
            }
        }
    }
}
