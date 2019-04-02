package services;

import domain.RGBImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Lab2Service {
    private static final int L = 255;

    public void medianFilter(final RGBImage rgbImage, final int matrixSize) {
        final int height = rgbImage.getHeight();
        final int width = rgbImage.getWidth();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final Integer[][] redMatrix = getNeighborPixels(rgbImage.getRedMatrix(), matrixSize, i, j);
                final Integer[][] greenMatrix = getNeighborPixels(rgbImage.getGreenMatrix(), matrixSize, i, j);
                final Integer[][] blueMatrix = getNeighborPixels(rgbImage.getBlueMatrix(), matrixSize, i, j);
                final int red = computeMedianValue(redMatrix);
                final int green = computeMedianValue(greenMatrix);
                final int blue = computeMedianValue(blueMatrix);

                rgbImage.setPixel(i, j, red, green, blue);
            }
        }
    }

    /**
     * @return the matrix[matrixSize][matrixSize] around the pixel from [line,column] from the given image (the pixel is in the middle of the returned matrix).
     * If the pixel is on the edge of the image, the non-existent pixels are set to the arithmetic mean of the other pixels.
     */
    private Integer[][] getNeighborPixels(final int[][] image, final int matrixSize, final int line, final int column) {
        final Integer[][] neighborMatrix = new Integer[matrixSize][matrixSize];
        int pixelSum = 0;
        int nrOfNonNullPixels = 0;

        // build the neighbor matrix
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                final int imgI = line + i - (matrixSize-1)/2;
                final int imgJ = column + j - (matrixSize-1)/2;

                if (imgI < 0 || imgJ < 0 || imgI >= image.length || imgJ >= image[0].length) {
                    neighborMatrix[i][j] = null;
                    continue;
                }
                neighborMatrix[i][j] = image[imgI][imgJ];
                nrOfNonNullPixels++;
                pixelSum += image[imgI][imgJ];
            }
        }
        // fill the non-existing pixels
        final int mean = pixelSum / nrOfNonNullPixels;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (neighborMatrix[i][j] == null) {
                    neighborMatrix[i][j] = mean;
                }
            }
        }

        return neighborMatrix;
    }

    /**
     * Sort the values from the given matrix in an array, divide the array in 3 sections and return the arithmetic mean of the elements from the mddle section.
     */
    private int computeMedianValue(final Integer[][] matrix) {
        List<Integer> array = new ArrayList<>();
        for (Integer[] line : matrix) {
            array.addAll(Arrays.asList(line));
        }
        array = array.stream().sorted(Integer::compareTo).collect(Collectors.toList());
        final int start = array.size() / 3;
        final int end = start + array.size() / 3;
        int sum = 0;
        for (int i = start; i < end; i++) {
            sum += array.get(i);
        }
        return sum / start;
    }
}
