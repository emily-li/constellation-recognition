package com.liemily.web.config;

import com.liemily.imagerecognition.BWImageConverter;
import com.liemily.imagerecognition.ImageConverter;
import com.liemily.imagerecognition.ImageNetworkAccessor;
import com.liemily.imagerecognition.RecognitionOutputs;
import com.liemily.neuralnetwork.learning.BackPropagationLearningRule;
import com.liemily.neuralnetwork.learning.LearningRule;
import com.liemily.neuralnetwork.networks.NeuralNetwork;
import com.liemily.neuralnetwork.networks.SupervisedFeedForwardNetwork;
import com.liemily.web.domain.Constellation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application specific config, generating the necessary beans for constellation recognition.
 * @author Emily Li
 */
@Configuration
public class ApplicationConfig {

    @Value("${imageNetworkAccessor.resizeImg}")
    private String resizeImg;

    @Value("${network.weights.initialRange}")
    private String initialWeightRange;

    @Value("${network.learning.learningRate}")
    private String learningRate;

    @Value("${network.layers.input.numNodes}")
    private String inputLayerNumNodes;

    @Value("${network.layers.hidden.count}")
    private String hiddenLayerCount;

    @Value("${network.layers.hidden.numNodes}")
    private String hiddenLayerNumNodes;

    @Bean
    public ImageNetworkAccessor imageNetworkAccessor() {
        return new ImageNetworkAccessor(imageConverter(), neuralNetwork(), recognitionOutputs(), Boolean.parseBoolean(resizeImg));
    }

    @Bean
    public ImageConverter imageConverter() {
        return new BWImageConverter();
    }

    @Bean
    public NeuralNetwork neuralNetwork() {
        NeuralNetwork neuralNetwork = new SupervisedFeedForwardNetwork(
                learningRule(),
                Integer.parseInt(inputLayerNumNodes),
                Integer.parseInt(hiddenLayerNumNodes),
                Integer.parseInt(hiddenLayerCount),
                recognitionOutputs().size(),
                Double.parseDouble(initialWeightRange)
        );
        return neuralNetwork;
    }

    @Bean
    public LearningRule learningRule() {
        return new BackPropagationLearningRule(Double.parseDouble(learningRate));
    }

    @Bean
    public RecognitionOutputs recognitionOutputs() {
        return new RecognitionOutputs(Constellation.values());
    }
}
