package services;

import converters.ImageConverter;
import domain.RGBImage;

public class Lab1Service {
    public void increaseLuminosity(final RGBImage rgbImage) {
        final int height = rgbImage.getHeight();
        final int width = rgbImage.getWidth();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int r = rgbImage.getRedMatrix()[i][j];
                int g = rgbImage.getGreenMatrix()[i][j];
                int b = rgbImage.getBlueMatrix()[i][j];
                r  = ImageConverter.clamp(r + 50);
                g  = ImageConverter.clamp(g + 50);
                b  = ImageConverter.clamp(b + 50);
                rgbImage.setPixel(i, j, r, g, b);
            }
        }
    }
}
