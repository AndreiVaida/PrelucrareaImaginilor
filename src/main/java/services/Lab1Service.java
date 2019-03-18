package services;

import converters.ImageConverter;
import domain.GreyscaleImage;
import domain.RGBImage;

public class Lab1Service {
    private static final int L = 255;

    public GreyscaleImage convertToGrayscale(final RGBImage rgbImage) {
        final int height = rgbImage.getHeight();
        final int width = rgbImage.getWidth();
        final GreyscaleImage greyscaleImage = new GreyscaleImage(height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int red = rgbImage.getRedMatrix()[i][j];
                int green = rgbImage.getGreenMatrix()[i][j];
                int blue = rgbImage.getBlueMatrix()[i][j];
                final int value = (red + green + blue) / 3;
                greyscaleImage.setPixel(i, j, value);
            }
        }
        return greyscaleImage;
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

    public GreyscaleImage bitExtraction(final RGBImage rgbImage, final int k) {
        final GreyscaleImage greyscaleImage = convertToGrayscale(rgbImage);
        final int height = greyscaleImage.getHeight();
        final int width = greyscaleImage.getWidth();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final int pixel = greyscaleImage.getMatrix()[i][j];
                final String binaryValue = intToBinary(pixel);
                int bitK = Character.getNumericValue(binaryValue.charAt(k));
                if (bitK == 1) {
                    bitK = 255;
                }
                greyscaleImage.setPixel(i, j, bitK);
            }
        }
        return greyscaleImage;
    }

    private String intToBinary(final int pixel) {
        String binaryValue = Integer.toBinaryString(pixel);
        while (binaryValue.length() < 8) {
            binaryValue = '0' + binaryValue;
        }
        return binaryValue;
    }

}
