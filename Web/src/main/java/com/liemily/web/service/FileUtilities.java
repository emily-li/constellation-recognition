package com.liemily.web.service;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for helper methods to facilitate the link between the web application and neural network via the image network accessor
 * @author Emily Li
 */
public class FileUtilities {
    public BufferedImage fileToImg(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            return ImageIO.read(is);
        }
    }
}
