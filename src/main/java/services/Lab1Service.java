package services;

import converters.ImageConverter;
import domain.RGBImage;

public class Lab1Service {
    private static final int L = 255;

    public void increaseLuminosity(final RGBImage rgbImage) {
        final int height = rgbImage.getHeight();
        final int width = rgbImage.getWidth();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int red = rgbImage.getRedMatrix()[i][j];
                int green = rgbImage.getGreenMatrix()[i][j];
                int blue = rgbImage.getBlueMatrix()[i][j];
                red  = ImageConverter.clamp(red + 50);
                green  = ImageConverter.clamp(green + 50);
                blue  = ImageConverter.clamp(blue + 50);
                rgbImage.setPixel(i, j, red, green, blue);
            }
        }
    }

    public void increaseContrast(final RGBImage rgbImage, final int a, final int b) {
        final int height = rgbImage.getHeight();
        final int width = rgbImage.getWidth();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int red = rgbImage.getRedMatrix()[i][j];
                int green = rgbImage.getGreenMatrix()[i][j];
                int blue = rgbImage.getBlueMatrix()[i][j];
                red  = increasePixelContrast(red, a, b);
                green  = increasePixelContrast(green, a, b);
                blue  = increasePixelContrast(blue, a, b);
                rgbImage.setPixel(i, j, red, green, blue);
            }
        }
    }

    private int increasePixelContrast(int pixel, final int a, final int b) {
//        pixel = (int) ((Math.sin(Math.PI*pixel/L - Math.PI/2) + 1) / 2 * L);
        if (pixel <= a) {
            pixel = pixel * pixel / a;
        }
        else if (pixel > b) {
            pixel = (pixel-b) / (L-b) * (L-b) + b;
        }
        else {
            pixel = (pixel-a) / (b-a) * (b-a) + a;
        }
        return ImageConverter.clamp(pixel);
    }
}
