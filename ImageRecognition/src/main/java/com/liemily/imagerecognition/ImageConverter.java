package com.liemily.imagerecognition;

import com.liemily.neuralnetwork.layers.NeuronLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class to be used by the ImageNetworkAccessor to convert images to NeuronLayer representations
 * @author Emily Li
 */
public abstract class ImageConverter {
    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Converts images to layers.
     *
     * First calls preProcess so that concrete implementations can apply any appropriate processing such as normalisation.
     * Then the method proceeds to use the RGB value made available by the BufferedImage class to create the layer
     * @param img BufferedImage passed in by the user
     * @return Represents the image as a single vector in the form of a NeuronLayer
     */
    public NeuronLayer convertToLayer(BufferedImage img) {
        BufferedImage preProcessedImg = preProcess(img);
        int whiteValues = 0;

        List<Double> nodes = new ArrayList<>();
        for (int x = 0; x < preProcessedImg.getWidth(); x++) {
            for (int y = 0; y < preProcessedImg.getHeight(); y++) {
                double nodeValue = getNodeValue(preProcessedImg.getRGB(x, y));
                if (nodeValue == 0) {
                    whiteValues++;
                }
                nodes.add(nodeValue);
            }
        }

        double[] values = nodes.stream().mapToDouble(d -> d).toArray();
        NeuronLayer neuronLayer = new NeuronLayer(nodes.size());
        neuronLayer.setNodes(values);
        logger.debug("Converted image to layer with " + whiteValues + " white values");
        return neuronLayer;
    }

    /**
     * Performs any necessary pre-processing to an image
     * @param img BufferedImage passed in by the user
     * @return New BufferedImage after processing
     */
    protected abstract BufferedImage preProcess(BufferedImage img);

    /**
     * Converts an RGB value provided by the BufferedImage class to a double to be used as a node value
     * @param rgb Expected to be provided by BufferedImage
     * @return double representing the node value
     */
    protected abstract double getNodeValue(int rgb);
}
