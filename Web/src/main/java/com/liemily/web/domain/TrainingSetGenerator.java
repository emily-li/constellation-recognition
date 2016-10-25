package com.liemily.web.domain;

import com.liemily.imagerecognition.ImageNetworkAccessor;
import com.liemily.imagerecognition.RecognitionOutputs;
import com.liemily.neuralnetwork.layers.NeuronLayer;
import com.liemily.neuralnetwork.training.TrainingPair;
import com.liemily.neuralnetwork.training.TrainingSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.*;

/**
 * Helper class to generate a training set that can be used by the neural network from a directory
 * @author Emily
 */
@Component
public class TrainingSetGenerator {
    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private ImageNetworkAccessor imageNetworkAccessor;
    private RecognitionOutputs recognitionOutputs;
    private boolean resizeImg;

    private Map<String, Enum> classificationMap;

    @Autowired
    public TrainingSetGenerator(ImageNetworkAccessor imageNetworkAccessor, RecognitionOutputs recognitionOutputs, @Value("${imageNetworkAccessor.resizeImg}") String resizeImg) {
        this.imageNetworkAccessor = imageNetworkAccessor;
        this.recognitionOutputs = recognitionOutputs;
        this.resizeImg = Boolean.parseBoolean(resizeImg);

        // Get list of valid subdirectory names, i.e. valid classifications
        classificationMap = new HashMap<>();
        recognitionOutputs.getClassifications().forEach(classification -> classificationMap.put(classification.name(), classification));
    }

    /**
     * Creates a training set given a directory location and recognition outputs
     * The method searches the directory for folders matching the recognition output names,
     * takes the images in the location, and creates the training set
     *
     * The subdirectories must match the Enum values from the RecognitionOutputs class
     *
     * @return TrainingSet produced from the images found within the training set directory
     */
    public TrainingSet getTrainingSet(String trainingSetDir) {
        List<TrainingPair> trainingPairs = new ArrayList<>();

        // Search the training set directory for images matching the classifications
        File trainingDir = new File(trainingSetDir);
        logger.info("Using location '" + trainingDir.getAbsolutePath() + "' to get images with which to train the neural network");

        if (trainingDir.exists()) {
            File[] subDirectories = trainingDir.listFiles();

            for (File subDir : subDirectories) {
                if (subDir.isDirectory()) {
                    String classification = subDir.getName().toUpperCase();
                    Enum classificationEnum = classificationMap.get(classification);

                    if (classificationEnum != null) {
                        File[] files = subDir.listFiles();
                        Collection<TrainingPair> classificationTrainingPairs = getTrainingPairs(classificationMap.get(classification), files);
                        trainingPairs.addAll(classificationTrainingPairs);
                    }
                }
            }
        }

        logger.info("Returning training set of size " + trainingPairs.size());
        Collections.shuffle(trainingPairs);
        return new TrainingSet(trainingPairs);
    }

    /**
     * Retrieves a collection of TrainingPairs from a list of files, given a classification
     * @param classification Classification determining the output of the TrainingPair
     * @param files Array of files determining the inputs of the TrainingPairs
     * @return Returns a collection of training pairs in which the inputs are processed images,
     *          and the outputs are specified by the classification
     */
    private Collection<TrainingPair> getTrainingPairs(Enum classification, File[] files) {
        Collection<TrainingPair> trainingPairs = new ArrayList<>();

        for (File file : files) {
            try {
                TrainingPair trainingPair = getTrainingPair(classification, file);
                if (trainingPair != null) {
                    trainingPairs.add(trainingPair);
                }
            } catch (IOException e) {
                logger.warn("Failed to read file at " + file.getAbsolutePath(), e);
            }
        }
        logger.info("Found " + trainingPairs.size() + " training pairs for classification " + classification);
        return trainingPairs;
    }

    /**
     * Creates a TrainingPair, given a file.
     * It creates the input by parsing the image, and the output from the parent directory name.
     * @param classification Classification determing the output of the TrainingPair
     * @param file Image matching a valid classification type
     * @return Returns a TrainingPair from information provided by the file. If the file is invalid, it returns null
     * @throws IOException
     */
    private TrainingPair getTrainingPair(Enum classification, File file) throws IOException {
        BufferedImage img = ImageIO.read(file);

        if (img != null) {
            NeuronLayer input = imageNetworkAccessor.getInput(img, resizeImg);
            NeuronLayer output = recognitionOutputs.getNeuronLayer(classificationMap.get(classification.name()));
            return new TrainingPair(classification, input, output);
        }
        return null;
    }
}
