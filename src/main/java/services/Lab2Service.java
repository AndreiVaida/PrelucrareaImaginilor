package services;

import domain.GreyscaleImage;
import domain.RGBImage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Lab2Service {
    private final Map<Integer,Integer[]> pseudocolorTable;

    public Lab2Service() {
        pseudocolorTable = new HashMap<>();
        try {
            loadPseudocolorTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
    static Integer[][] getNeighborPixels(final int[][] image, final int matrixSize, final int line, final int column) {
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

    public void invertContrast(final RGBImage rgbImage, final int matrixSize) {
        final int height = rgbImage.getHeight();
        final int width = rgbImage.getWidth();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final Integer[][] redMatrix = getNeighborPixels(rgbImage.getRedMatrix(), matrixSize, i, j);
                final Integer[][] greenMatrix = getNeighborPixels(rgbImage.getGreenMatrix(), matrixSize, i, j);
                final Integer[][] blueMatrix = getNeighborPixels(rgbImage.getBlueMatrix(), matrixSize, i, j);
                final int red = computeInvertContrast(redMatrix);
                final int green = computeInvertContrast(greenMatrix);
                final int blue = computeInvertContrast(blueMatrix);

                rgbImage.setPixel(i, j, red, green, blue);
            }
        }
    }

    private int computeInvertContrast(final Integer[][] matrix) {
        final int miu = computeMiu(matrix);
        final int sigma = computeSigma(matrix, miu);
        if (sigma == 0) {
            return miu;
        }
        return miu / sigma;
    }

    private int computeMiu(final Integer[][] matrix) {
        int sum = 0;
        for (Integer[] line : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                sum += line[j];
            }
        }
        final int nrElems = matrix.length * matrix[0].length;
        return sum / nrElems;
    }

    private int computeSigma(final Integer[][] matrix, final int miu) {
        int sum = 0;
        for (Integer[] line : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                sum += Math.pow(line[j] - miu, 2);
            }
        }
        final int nrElems = matrix.length * matrix[0].length;
        return (int) Math.sqrt(sum / (double)nrElems);
    }

    public RGBImage pseudocolorImage(final GreyscaleImage greyscaleImage) {


        final int height = greyscaleImage.getHeight();
        final int width = greyscaleImage.getWidth();
        final RGBImage rgbImage = new RGBImage(height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final int pixel = greyscaleImage.getMatrix()[i][j] / 4;
                final Integer[] rgbValues = pseudocolorTable.get(pixel);
                final int r = rgbValues[0] * 4;
                final int g = rgbValues[1] * 4;
                final int b = rgbValues[2] * 4;
                rgbImage.setPixel(i, j, r, g, b);
            }
        }
        return rgbImage;
    }

    private void loadPseudocolorTable() throws IOException {
        final File file = new File("./src/main/resources/docs/PseudocolorTable.txt");
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = bufferedReader.readLine()) != null && line.length() > 1) {
            final String[] splitLine = line.split(" ");
            final int grayPixel = Integer.parseInt(splitLine[0]);
            final String[] rgbValuesStr = splitLine[1].split(",");
            final Integer[] rgbValues = new Integer[3];
            rgbValues[0] = Integer.parseInt(rgbValuesStr[0]);
            rgbValues[1] = Integer.parseInt(rgbValuesStr[1]);
            rgbValues[2] = Integer.parseInt(rgbValuesStr[2]);
            pseudocolorTable.put(grayPixel, rgbValues);
        }
    }
}
