package com.liemily.imagerecognition;

import com.liemily.neuralnetwork.layers.NeuronLayer;
import com.liemily.neuralnetwork.learning.SupervisedLearningInterface;
import com.liemily.neuralnetwork.networks.NeuralNetwork;
import com.liemily.neuralnetwork.training.TrainingSet;
import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

/**
 * API exposing a neural network
 *
 * The neural network is wrapped by the ImageNetworkAccessor so that it can be used for image recognition.
 * This is facilitated by a concrete implementation of the ImageConverter class and definition of results provided by RecognitionOutputs
 * @author Emily Li
 */
public class ImageNetworkAccessor {
    private ImageConverter imageConverter;
    private NeuralNetwork neuralNetwork;
    private RecognitionOutputs recognitionOutputs;
    private boolean resizeImg;

    public ImageNetworkAccessor(ImageConverter imageConverter, NeuralNetwork neuralNetwork, RecognitionOutputs recognitionOutputs, boolean resizeImg) {
        this.imageConverter = imageConverter;
        this.neuralNetwork = neuralNetwork;
        this.recognitionOutputs = recognitionOutputs;
        this.resizeImg = resizeImg;
    }

    /**
     * Exposes the neural network's functionality to the user
     * @param img BufferedImage that the ImageNetworkAccessor processes into a NeuronLayer representation
     * @return Returns an Enum, where a collection has been specified by the user to the RecognitionOutputs object
     * @throws IOException
     */
    public Enum identify(BufferedImage img) throws IOException {
        setInput(img, resizeImg);
        neuralNetwork.activate();
        NeuronLayer outputLayer = neuralNetwork.getOutputLayer();
        return getRecognitionOutputs().getClassification(outputLayer);
    }

    /**
     * Trains the network with a given training set if the network implements the SupervisedLearningInterface
     * @param trainingSet Collection of input-output pairs where the input is a representation of an image, and the output is the identifier
     * @param iterations Number of iterations to train using the same set
     * @return Returns error if the network has been trained, else -1
     */
    public double trainNetwork(TrainingSet trainingSet, int iterations) {
        if (neuralNetwork instanceof SupervisedLearningInterface) {
            return ((SupervisedLearningInterface) neuralNetwork).train(trainingSet, iterations);
        }
        return -1;
    }

    /**
     * See getInput(BufferedImage img, boolean resize)
     * @param img
     * @throws IOException
     */
    public NeuronLayer getInput(BufferedImage img) throws IOException {
        return getInput(img, resizeImg);
    }

    /**
     * Converts a BufferedImage to NeuronLayer representation
     * @param img BufferedImage provided by the user
     * @param resize boolean specifying whether the image should be resized to fit the neural network's existing input layer
     * @throws IOException
     */
    public NeuronLayer getInput(BufferedImage img, boolean resize) throws IOException {
        BufferedImage inputImg = resize ? resize(img) : img;
        NeuronLayer input = imageConverter.convertToLayer(inputImg);
        if (resize) {
            resize(input);
        }
        return input;
    }

    /**
     * Sets the input of the network after performing processing on the image
     * @param img BufferedImage provided by the user
     * @param resize boolean specifying whether the image should be resized to fit the neural network's existing input layer
     * @throws IOException
     */
    public void setInput(BufferedImage img, boolean resize) throws IOException {
        NeuronLayer input = getInput(img, resize);
        setInput(input);
    }

    /**
     * Applies the neuron layer specified to the neural network
     * by setting the existing input layer's values
     * @param neuronLayer NeuronLayer representing an input to the network
     */
    public void setInput(NeuronLayer neuronLayer) {
        neuralNetwork.setInputValues(neuronLayer);
    }

    /**
     * Resizes the BufferedImage to fit the neural network's input layer.
     *
     * This has been implemented so that it only resizes images that are too large to be applied.
     * If the image is smaller than that of the neural network, the neural network compensates for this
     *
     * @param img BufferedImage provided by the user
     * @return Returns a scaled version of the BufferedImage according to the neural network's existing input layer
     * @throws IOException
     */
    private BufferedImage resize(BufferedImage img) throws IOException {
        int size = (int) Math.sqrt(neuralNetwork.getInputLayer().size());
        return Scalr.resize(img, Scalr.Method.AUTOMATIC, size, size);
    }

    /**
     * Resizes a neuron layer so that it can fit the neural network
     * @param neuronLayer NeuronLayer representing an image to submit to the network
     */
    private void resize(NeuronLayer neuronLayer) {
        neuronLayer.setNodes(Arrays.copyOf(neuronLayer.getNodes(), neuralNetwork.getInputLayer().size()));
    }

    public RecognitionOutputs getRecognitionOutputs() {
        return recognitionOutputs;
    }
}
