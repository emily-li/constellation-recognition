package com.liemily.imagerecognition;

import com.liemily.neuralnetwork.layers.NeuronLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Wrapper for an OutputLayer where the output is a classification.
 * For instance, each node of the output layer could represent
 * an alphanumeric character in a handwriting recognition network
 *
 * @author Emily Li
 */
public class RecognitionOutputs {
    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private final Map<Enum, NeuronLayer> classificationLayers = new HashMap<>();

    public RecognitionOutputs(Enum[] classifications) {
        final double classificationNode = 1;
        final double otherNode = 0;

        double[] baseNodes = new double[classifications.length];
        Arrays.fill(baseNodes, otherNode);

        for (int i = 0; i < classifications.length; i++) {
            double[] layerNodes = Arrays.copyOf(baseNodes, baseNodes.length);
            layerNodes[i] = classificationNode;

            NeuronLayer classificationLayer = new NeuronLayer(layerNodes.length);
            classificationLayer.setNodes(layerNodes);
            classificationLayers.put(classifications[i], classificationLayer);
        }
    }

    /**
     * Returns a classification matching that of the neuron layer.
     * If it is possible for the neuron layer to match multiple classifications,
     * the layer with the least error is returned
     * @param neuronLayer NeuronLayer expected to be provided by the ImageNetworkAccessor
     * @return Returns an Enum existing in the classifications that were passed to the instance at instantiation
     *          if a valid classification is found. Otherwise returns null.
     */
    public Enum getClassification(NeuronLayer neuronLayer) {
        NeuronLayer processedNeuronLayer = getClassificationWithLeastError(neuronLayer);

        for (Map.Entry<Enum, NeuronLayer> classification : classificationLayers.entrySet()) {
            if (classification.getValue().equals(processedNeuronLayer)) {
                return classification.getKey();
            }
        }
        return null;
    }

    /**
     * Where there are multiple possible classifications for a neuron layer,
     * this method goes through all classifications and calculates the error that the neuron layer has with that classification
     * and returns the layer from the classification map with the smallest error.
     *
     * If there are two layers with the same error, the last calculated classification will be returned
     * @param neuronLayer NeuronLayer which may have multiple possible classifications
     * @return NeuronLayer from the classification map that best matches the NeuronLayer passed in by the user
     */
    protected NeuronLayer getClassificationWithLeastError(NeuronLayer neuronLayer) {
        TreeMap<Double, NeuronLayer> possibleClassifications = new TreeMap<>();
        NeuronLayer processedLayer = preProcess(neuronLayer);

        double[] baseValues = new double[classificationLayers.size()];
        Arrays.fill(baseValues, 0);
        for (int i = 0; i < processedLayer.size(); i++) {
            if (processedLayer.getNodes()[i] == 1) {
                logger.debug("Found value " + neuronLayer.getNodes()[i] + " at index " + i);
                NeuronLayer possibleClassification = new NeuronLayer(classificationLayers.size());
                double[] layerValues = baseValues;
                layerValues[i] = 1;
                possibleClassification.setNodes(layerValues);
                possibleClassifications.put(1 - neuronLayer.getNodes()[i], possibleClassification);
            }
        }
        return possibleClassifications.isEmpty() ? null : possibleClassifications.firstEntry().getValue();
    }

    /**
     * Performs any necessary normalisation to the NeuronLayer.
     * In this case, double values are converted to their nearest int value, expected to be either 0 or 1
     * though other values are allowed.
     *
     * The values themselves depend on the way in which the neural network has been trained.
     *
     * @param neuronLayer NeuronLayer provided by the user, representing a valid output
     * @return Returns a copy of the NeuronLayer with any necessary processing having been applied, leaving the original intact
     */
    private NeuronLayer preProcess(NeuronLayer neuronLayer) {
        NeuronLayer processedNeuronLayer = new NeuronLayer(neuronLayer.size());

        double[] neuronNodes = neuronLayer.getNodes();
        double[] newNodes = new double[neuronNodes.length];
        IntStream.range(0, neuronNodes.length).forEach(i -> newNodes[i] = Math.round(neuronNodes[i]));

        processedNeuronLayer.setNodes(newNodes);
        return processedNeuronLayer;
    }

    public NeuronLayer getNeuronLayer(Enum classification) {
        return classificationLayers.get(classification);
    }

    public Collection<Enum> getClassifications() { return classificationLayers.keySet(); }

    public int size() {
        return classificationLayers.size();
    }
}
