package com.liemily.imagerecognition;

import com.liemily.neuralnetwork.learning.BackPropagationLearningRule;
import com.liemily.neuralnetwork.networks.SupervisedFeedForwardNetwork;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test class for the 'Neural Network Accessor' component as described in the Functional Test Plan
 * @author Emily Li
 */
public class ImageNetworkAccessorTest {
    private SupervisedFeedForwardNetwork network;
    private ImageConverter imageConverter;
    private RecognitionOutputs recognitionOutputs;
    private ImageNetworkAccessor accessor;

    private InputStream img1Stream;
    private InputStream img2Stream;
    private BufferedImage img1;
    private BufferedImage img2;

    private int layerSize;

    @Before
    public void setup() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        img1Stream = classLoader.getResourceAsStream("img1.bmp");
        img2Stream = classLoader.getResourceAsStream("img2.bmp");
        img1 = ImageIO.read(img1Stream);
        img2 = ImageIO.read(img2Stream);

        layerSize = img1.getWidth() * img1.getHeight();

        network = spy(new SupervisedFeedForwardNetwork(new BackPropagationLearningRule(0.5), layerSize, layerSize, 1, layerSize, 0.5));
        imageConverter = spy(new BWImageConverter());
        recognitionOutputs = mock(RecognitionOutputs.class);
        accessor = new ImageNetworkAccessor(imageConverter, network, recognitionOutputs, true);
    }

    @After
    public void tearDown() throws IOException {
        img1Stream.close();
        img2Stream.close();
    }

    // 12. The implemented network accessor must be able to
    //     set the input layer of the neural network
    @Test
    public void testSettingInputLayer() throws IOException {
        double[] oldInput = network.getInputLayer().getNodes().clone();
        accessor.setInput(img1, true);
        double[] newInput = network.getInputLayer().getNodes().clone();

        assertThat(newInput, IsNot.not(IsEqual.equalTo(oldInput)));
    }

    // 13. The implemented network accessor must be able to
    //     convert an image to Node objects
    @Test
    public void testImageRepresentation() throws IOException {
        accessor.setInput(img1, true);
        double[] layer1A = network.getInputLayer().getNodes().clone();
        accessor.setInput(img1, true);
        double[] layer1B = network.getInputLayer().getNodes().clone();
        accessor.setInput(img2, true);
        double[] layer2 = network.getInputLayer().getNodes().clone();

        verify(imageConverter, atLeast(2)).convertToLayer(any());
        assertArrayEquals(layer1A, layer1B, Double.MIN_VALUE);
        assertThat(layer1A, IsNot.not(IsEqual.equalTo(layer2)));
    }

    // 14. The implemented network accessor must be able to
    //     get a result from an image input
    @Test
    public void testInputGivesResult() throws IOException {
        accessor.identify(img1);
        verify(network, times(1)).activate();
        verify(recognitionOutputs, times(1)).getClassification(any());
    }
}
