package services;

import converters.ImageConverter;
import domain.GrayscaleImage;
import domain.RGBImage;

public class Lab1Service {
    private static final int L = 255;

    public GrayscaleImage convertToGrayscale(final RGBImage rgbImage) {
        final int height = rgbImage.getHeight();
        final int width = rgbImage.getWidth();
        final GrayscaleImage grayscaleImage = new GrayscaleImage(height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int red = rgbImage.getRedMatrix()[i][j];
                int green = rgbImage.getGreenMatrix()[i][j];
                int blue = rgbImage.getBlueMatrix()[i][j];
                final int value = (red + green + blue) / 3;
                grayscaleImage.setPixel(i, j, value);
            }
        }
        return grayscaleImage;
    }

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
        // accentuare netedă
        pixel = (int) ((Math.sin(Math.PI*pixel/L - Math.PI/2) + 1) / 2 * L);
        return ImageConverter.clamp(pixel);

        // normal contrast change
        /*final double factor = (259.0 * (contrastLevel + 255.0)) / (255.0 * (259.0 - contrastLevel));
        pixel = (int) (factor * (pixel - 128) + 128);*/
        // accentuare liniară
        /*final int va = a - 20;
        final int vb = b + 20;
        if (pixel <= a) {
            pixel = pixel * va / a;
        }
        else if (pixel > b) {
            pixel = (pixel-b) / (L-b) * (L-vb) + vb;
        }
        else {
            pixel = (pixel-a) / (b-a) * (vb-va) + va;
        }*/
    }

    public GrayscaleImage bitExtraction(final RGBImage rgbImage, final int k) {
        final GrayscaleImage grayscaleImage = convertToGrayscale(rgbImage);
        final int height = grayscaleImage.getHeight();
        final int width = grayscaleImage.getWidth();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (grayscaleImage.getMatrix()[i][j] % k == 0) {
                    grayscaleImage.setPixel(i, j, L);
                }
                else {
                    grayscaleImage.setPixel(i, j, 0);
                }
            }
        }
        return grayscaleImage;
    }

}
