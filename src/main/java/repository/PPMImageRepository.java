package repository;

import domain.RGBImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PPMImageRepository {
    private PPMImageRepository() {
    }

    public static RGBImage loadImage(final File file) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        final RGBImage rgbImage;
        final int width;
        final int height;
        final int maxByteValue;
        // the format of the image (P3 or P6)
        final String format = bufferedReader.readLine();
        // comment line
        final String comment = bufferedReader.readLine();
        // resolution
        final String[] resolution = bufferedReader.readLine().split(" ");
        width = Integer.parseInt(resolution[0]);
        height = Integer.parseInt(resolution[1]);
        rgbImage = new RGBImage(width, height);
        // the maximum value of a byte component
        final String maxByteValueStr = bufferedReader.readLine();
        maxByteValue = Integer.parseInt(maxByteValueStr);
        // read the pixels
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final int redComponent = Integer.parseInt(bufferedReader.readLine());
                final int greenComponent = Integer.parseInt(bufferedReader.readLine());
                final int blueComponent = Integer.parseInt(bufferedReader.readLine());
                rgbImage.setPixel(i, j, redComponent, greenComponent, blueComponent);
            }
        }
        return rgbImage;
    }

    public static void saveImage(final RGBImage rgbImage, final File file) throws IOException {
        final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        final int width = rgbImage.getWidth();
        final int height = rgbImage.getHeight();
        final String widthStr = String.valueOf(width);
        final String heightStr = String.valueOf(height);
        final String maxByteValue = String.valueOf(255);
        final String format = "P3";
        final String comment = "# CREATOR: Andrei Lucian Vaida";
        // the format of the image (P3 or P6)
        bufferedWriter.write(format);
        bufferedWriter.newLine();
        // comment line
        bufferedWriter.write(comment);
        bufferedWriter.newLine();
        // resolution
        bufferedWriter.write(widthStr + " " + heightStr);
        bufferedWriter.newLine();
        // the maximum value of a byte component
        bufferedWriter.write(maxByteValue);
        bufferedWriter.newLine();
        // write the pixels
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final String redComponent = String.valueOf(rgbImage.getRedMatrix()[i][j]);
                bufferedWriter.write(redComponent);
                bufferedWriter.newLine();
                final String greenComponent = String.valueOf(rgbImage.getGreenMatrix()[i][j]);
                bufferedWriter.write(greenComponent);
                bufferedWriter.newLine();
                final String blueComponent = String.valueOf(rgbImage.getBlueMatrix()[i][j]);
                bufferedWriter.write(blueComponent);
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
        }
    }
}
