package com.liemily.imagerecognition;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Simple image converter using java.awt.* to convert images to black and white
 * @author Emily Li
 */
public class BWImageConverter extends ImageConverter {
    private final int WHITE = -1;
    private final int BLACK = -16777216;

    protected BufferedImage preProcess(BufferedImage img) {
        BufferedImage bwImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(img, 0, 0, null);
        return bwImg;
    }

    @Override
    protected double getNodeValue(int rgb) {
        return rgb == WHITE ? 0 : 1;
    }
}
