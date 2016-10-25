package com.liemily.web;

import com.liemily.imagerecognition.ImageConverter;
import com.liemily.imagerecognition.ImageNetworkAccessor;
import com.liemily.imagerecognition.RecognitionOutputs;
import com.liemily.neuralnetwork.layers.NeuronLayer;
import com.liemily.neuralnetwork.networks.NeuralNetwork;
import com.liemily.web.controller.ConstellationRecognitionController;
import com.liemily.web.controller.UploadController;
import com.liemily.web.domain.TrainingSetGenerator;
import com.liemily.web.domain.Upload;
import com.liemily.web.service.ConstellationRecognitionService;
import com.liemily.web.service.FileUtilities;
import com.liemily.web.validator.UploadValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Test class for the 'Web Client' component as described in the Functional Test Plan
 * @author Emily Li
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WebClientTest {

    @Autowired
    private UploadController uploadController;

    @Autowired
    private ImageConverter imageConverter;

    @Value("${imageNetworkAccessor.resizeImg}")
    private String resize;

    private ImageNetworkAccessor imageNetworkAccessor;
    private TrainingSetGenerator trainingSetGenerator;
    private ConstellationRecognitionService constellationRecognitionService;

    private BufferedImage bufferedImage;
    private MultipartFile multipartFile;
    private Upload upload;
    private BindingResult bindingResult;

    @Before
    public void setup() throws Exception {
        imageNetworkAccessor = spy(new ImageNetworkAccessor(imageConverter, mock(NeuralNetwork.class), mock(RecognitionOutputs.class), false));
        trainingSetGenerator = mock(TrainingSetGenerator.class);
        constellationRecognitionService = spy(new ConstellationRecognitionService(imageNetworkAccessor, trainingSetGenerator, "", "", ""));

        bufferedImage = mock(BufferedImage.class);
        try (InputStream imgInputStream = getClass().getClassLoader().getResourceAsStream("img1.bmp")) {
            multipartFile = new MockMultipartFile("file", imgInputStream);
        }

        bindingResult = mock(BindingResult.class);

        upload = mock(Upload.class);
        when(upload.getId()).thenReturn("1");
    }

    // 15. The web client must provide an error
    //     if the provided file from the user is invalid
    @Test
    public void testInvalidFile() {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getContentType()).thenReturn("text/plain");
        when(upload.getFile()).thenReturn(multipartFile);

        uploadController.upload(upload, bindingResult);

        verify(bindingResult, times(1)).rejectValue(UploadValidator.FILE_VALUE, UploadValidator.NOT_AN_IMG_ERROR);
    }

    @Test
    public void testNoFileOnUpload() {
        when(upload.getFile()).thenReturn(null);
        uploadController.upload(upload, bindingResult);

        verify(bindingResult, times(1)).rejectValue(UploadValidator.FILE_VALUE, UploadValidator.NULL_FILE_ERROR);
    }

    // 16. The web client must be able to submit an image to the neural network accessor
    @Test
    public void testImageSubmission() throws IOException {
        constellationRecognitionService.identify(multipartFile);
        BufferedImage bufferedImage = new FileUtilities().fileToImg(multipartFile);
        NeuronLayer imgLayer = imageConverter.convertToLayer(bufferedImage);
        verify(imageNetworkAccessor, atLeastOnce()).setInput(imgLayer);
    }

    // 17. The web client must be able to return the result
    //     from the network accessor to the user
    @Test
    public void testCanReturnResult() throws IOException {
        Enum expectedResult = mock(Enum.class);
        doReturn(expectedResult).when(imageNetworkAccessor).identify(bufferedImage);

        Enum result = constellationRecognitionService.identify(bufferedImage);
        verify(imageNetworkAccessor, times(1)).identify(bufferedImage);
        assertEquals(expectedResult, result);
    }

    // 18. The web client must return a failure result to the user
    //     if there is no positive result
    @Test
    public void testFailureReturnedWithNoRecognitionFound() throws IOException {
        ConstellationRecognitionController constellationRecognitionController = new ConstellationRecognitionController(constellationRecognitionService);
        Model model = mock(Model.class);

        Upload upload = new Upload();
        upload.setFile(multipartFile);

        doReturn(null).when(imageNetworkAccessor).identify(any());

        String result = constellationRecognitionController.identify(upload, model);
        assertEquals(result, "no-result");
    }

    // 19. Identification must take no longer than a minute
    @Test (timeout = 65 * 1000)
    public void testIdentificationTimeout() throws Exception {
        ConstellationRecognitionService mockService = new MockConstellationService(imageNetworkAccessor, trainingSetGenerator, "", "", "60000");
        mockService.identify(multipartFile);
    }

    private class MockConstellationService extends ConstellationRecognitionService {
        public MockConstellationService(ImageNetworkAccessor imageNetworkAccessor, TrainingSetGenerator trainingSetGenerator, String trainingSetDir, String trainingIterations, String identificationTimeoutMs) {
            super(imageNetworkAccessor, trainingSetGenerator, trainingSetDir, trainingIterations, identificationTimeoutMs);
        }

        @Override
        public Enum identify(BufferedImage img) throws IOException {
            try {
                Thread.sleep(getIdentificationTimeoutMs() + 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }
}
