package com.liemily.web.service;

import com.liemily.imagerecognition.ImageNetworkAccessor;
import com.liemily.neuralnetwork.training.TrainingSet;
import com.liemily.web.domain.TrainingSetGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.*;

/**
 * Constellation recognition service, bringing together the ImageNetworkAccessor and web application.
 *
 * As the neural network currently has no persisted state, the service trains the neural network at start up
 * @author Emily Li
 */
@Component
public class ConstellationRecognitionService {
    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private ImageNetworkAccessor imageNetworkAccessor;
    private long identificationTimeoutMs;

    @Autowired
    public ConstellationRecognitionService(ImageNetworkAccessor imageNetworkAccessor, TrainingSetGenerator trainingSetGenerator, @Value("${app.training.dir}") String trainingSetDir, @Value("${app.training.iterations}") String trainingIterations, @Value("${app.identification.timeoutMs}") String identificationTimeoutMs) {
        this.imageNetworkAccessor = imageNetworkAccessor;
        if (identificationTimeoutMs == null || identificationTimeoutMs.isEmpty()) {
            this.identificationTimeoutMs = -1;
        } else {
            this.identificationTimeoutMs = Long.parseLong(identificationTimeoutMs);
        }

        int iter = 0;
        String invalidNumIterationsError = "Invalid setting for app.training.iterations";
        try {
            iter = Integer.parseInt(trainingIterations);
        } catch (NumberFormatException e) {
            logger.info(invalidNumIterationsError, e);
        }
        if (iter < 0) {
            logger.info(invalidNumIterationsError);
            iter = 0;
        }

        if (iter > 0) {
            TrainingSet trainingSet = trainingSetGenerator.getTrainingSet(trainingSetDir);

            logger.info("Training...");
            double error = imageNetworkAccessor.trainNetwork(trainingSet, iter);
            logger.info("Completed training with final error: " + error);
        } else {
            logger.info("Skipping training");
        }
    }

    /**
     * Produces an identification result for the neural network, given a MultipartFile provided by the Spring Boot application.
     * This method converts the MultipartFile to a BufferedImage, compatible with the ImageNetworkAccessor
     * @param multipartFile Submission from the user
     * @return Enum matching the enums from the configured RecognitionOutputs
     * @throws IOException
     */
    public Enum identify(MultipartFile multipartFile) throws IOException {
        return identify(new FileUtilities().fileToImg(multipartFile), identificationTimeoutMs);
    }

    /**
     * Returns the identification for a given image, matching those specified by the configured RecognitionOutputs
     * @param img BufferedImage for the ImageNetworkAccessor to use for identification
     * @return Enum matching the enums from the configured RecognitionOutputs
     * @throws IOException
     */
    public Enum identify(BufferedImage img) throws IOException {
        return imageNetworkAccessor.identify(img);
    }

    /**
     * Allows for identification with a timeout
     * @param img BufferedImage to be passed to the ImageNetworkAccessor
     * @param timeout Timeout specified in milliseconds
     * @return Returns the identification of the image, matching enums from the configured RecognitionOutputs
     * @throws IOException
     */
    public Enum identify(BufferedImage img, long timeout) throws IOException {
        Enum result = null;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Enum> futureResult = executorService.submit(new IdentifyTask(img));

        if (timeout > 0) {
            try {
                result = futureResult.get(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException ie) {
                throw new IOException("Failed to run identification", ie);
            } catch (TimeoutException te) {
                logger.info("Identification timed out", te);
            }
        } else {
            result = identify(img);
        }
        return result;
    }

    public long getIdentificationTimeoutMs() {
        return identificationTimeoutMs;
    }

    /**
     * Simple identification task that can be used by an ExecutorService
     */
    private class IdentifyTask implements Callable<Enum> {
        private BufferedImage img;

        public IdentifyTask(BufferedImage img) {
            this.img = img;
        }

        @Override
        public Enum call() throws Exception {
            return identify(img);
        }
    }
}
