package converters;

import domain.BlackWhiteImage;
import domain.GreyscaleImage;
import domain.RGBImage;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import static app.Main.L;

public class ImageConverter {
    private ImageConverter() {}

    public static int clamp(int value) {
        if (value < 0) {
            return 0;
        }
        if (value > 255) {
            return 255;
        }
        return value;
    }

    public static RGBImage bufferedImageToRgbImage(final Image image) {
        final int height = (int) image.getHeight();
        final int width = (int) image.getWidth();
        final RGBImage rgbImage = new RGBImage(height, width);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                final int argb = image.getPixelReader().getArgb(i, j);
                final int red = (argb >> 16) & 0xff;
                final int green = (argb >> 8) & 0xff;
                final int blue = argb & 0xff;
                rgbImage.setPixel(j, i, red, green, blue);
            }
        }

        return rgbImage;
    }

    public static BlackWhiteImage bufferedImageToBlackWhiteImage(final Image image) {
        final int height = (int) image.getHeight();
        final int width = (int) image.getWidth();
        final BlackWhiteImage blackWhiteImage = new BlackWhiteImage(height, width);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                final int argb = image.getPixelReader().getArgb(i, j);
                final int red = (argb >> 16) & 0xff;
                final int green = (argb >> 8) & 0xff;
                final int blue = argb & 0xff;
                final boolean value = (red + green + blue) / 3 > L / 2;
                blackWhiteImage.setPixel(j, i, value);
            }
        }
        return blackWhiteImage;
    }

    public static Image rgbImageToImage(final RGBImage rgbImage) {
        final int height = rgbImage.getHeight();
        final int width = rgbImage.getWidth();
        final WritableImage writableImage = new WritableImage(width, height);
        final PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                final int r = rgbImage.getRedMatrix()[y][x];
                final int g = rgbImage.getGreenMatrix()[y][x];
                final int b = rgbImage.getBlueMatrix()[y][x];
                pixelWriter.setColor(x, y, Color.rgb(r, g, b));
            }
        }

        return writableImage;
    }

    public static Image grayscaleImageToImage(final GreyscaleImage greyscaleImage) {
        final int height = greyscaleImage.getHeight();
        final int width = greyscaleImage.getWidth();
        final WritableImage writableImage = new WritableImage(width, height);
        final PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                final int r = greyscaleImage.getMatrix()[y][x];
                final int g = greyscaleImage.getMatrix()[y][x];
                final int b = greyscaleImage.getMatrix()[y][x];
                pixelWriter.setColor(x, y, Color.rgb(r, g, b));
            }
        }

        return writableImage;
    }

    public static Image blackWhiteImageToImage(final BlackWhiteImage blackWhiteImage) {
        final int height = blackWhiteImage.getHeight();
        final int width = blackWhiteImage.getWidth();
        final WritableImage writableImage = new WritableImage(width, height);
        final PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                final boolean bwPixel = blackWhiteImage.getMatrix()[y][x];
                final int rgbPixel = bwPixel ? L : 0;
                pixelWriter.setColor(x, y, Color.rgb(rgbPixel, rgbPixel, rgbPixel));
            }
        }

        return writableImage;
    }

    public static Image duplicateImage(final Image image) {
        final int width = (int) image.getWidth();
        final int height = (int) image.getHeight();
        final WritableImage writableImage = new WritableImage(width, height);
        final PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                final int argb = image.getPixelReader().getArgb(x, y);
                final int red = (argb >> 16) & 0xff;
                final int green = (argb >> 8) & 0xff;
                final int blue = argb & 0xff;
                pixelWriter.setColor(x, y, Color.rgb(red, green, blue));
            }
        }

        return writableImage;
    }

    public static GreyscaleImage blackWhiteImageToGreyscaleImage(final BlackWhiteImage blackWhiteImage) {
        final int height = blackWhiteImage.getHeight();
        final int width = blackWhiteImage.getWidth();
        final GreyscaleImage greyscaleImage = new GreyscaleImage(height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                greyscaleImage.setPixel(i, j, blackWhiteImage.getMatrix()[i][j] ? 1 : 0);
            }
        }
        return greyscaleImage;
    }
}
