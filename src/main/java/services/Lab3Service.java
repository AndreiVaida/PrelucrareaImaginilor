package services;

import domain.BlackWhiteImage;
import domain.Outline;

public class Lab3Service {

    public Outline identifyOutline(final BlackWhiteImage blackWhiteImage) {
        final int height = blackWhiteImage.getHeight();
        final int width = blackWhiteImage.getWidth();
        final Outline outline = new Outline(height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final boolean[][] neighborPixels = getNeighborPixels(blackWhiteImage.getMatrix(), 3, i, j);
                outline.setPixel(i, j, pixelIsOnOutline(neighborPixels));
            }
        }
        return outline;
    }

    /**
     * @param neighborPixels - 3x3 matrix
     * @return true if the centered pixel is white and any its neighbors (only Left, Right, Up, Down) is black
     */
    private boolean pixelIsOnOutline(final boolean[][] neighborPixels) {
        if (!neighborPixels[1][1]) {
            return false;
        }
        return !neighborPixels[1][0] || !neighborPixels[1][2] || !neighborPixels[0][1] || !neighborPixels[2][1];
    }

    /**
     * @return the matrix[matrixSize][matrixSize] around the pixel from [line,column] from the given image (the pixel is in the middle of the returned matrix).
     * If the pixel is on the edge of the image, the non-existent pixels are set to 0 (false) - border.
     */
    private boolean[][] getNeighborPixels(final boolean[][] image, final int matrixSize, final int line, final int column) {
        final boolean[][] neighborMatrix = new boolean[matrixSize][matrixSize];

        // build the neighbor matrix
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                final int imgI = line + i - (matrixSize-1)/2;
                final int imgJ = column + j - (matrixSize-1)/2;

                if (imgI < 0 || imgJ < 0 || imgI >= image.length || imgJ >= image[0].length) {
                    neighborMatrix[i][j] = false;
                    continue;
                }
                neighborMatrix[i][j] = image[imgI][imgJ];
            }
        }

        return neighborMatrix;
    }
}
