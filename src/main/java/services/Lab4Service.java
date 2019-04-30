package services;

import converters.ImageConverter;
import domain.BlackWhiteImage;
import domain.GreyscaleImage;
import domain.Point2D;
import domain.StructuralElementBW;
import domain.StructuralElementGreyscale;

import static app.Main.L;

public class Lab4Service {

    public BlackWhiteImage erosionBW(final BlackWhiteImage blackWhiteImage) {
        final StructuralElementBW structuralElement = createDefaultStructuralElementBW_demoErosion();
        return erosionBW(blackWhiteImage, structuralElement);
    }

    public BlackWhiteImage dilationBW(final BlackWhiteImage blackWhiteImage) {
        final StructuralElementBW structuralElement = createDefaultStructuralElementBW_demoDilation();
        return dilationBW(blackWhiteImage, structuralElement);
    }

    public GreyscaleImage erosionGreyscale(final GreyscaleImage greyscaleImage) {
        final StructuralElementGreyscale structuralElement = createDefaultStructuralElementGreyscale_demoErosion();
        return erosionGreyscale(greyscaleImage, structuralElement);
    }

    public GreyscaleImage dilationGreyscale(final GreyscaleImage greyscaleImage) {
        final StructuralElementGreyscale structuralElement = createDefaultStructuralElementGreyscale_demoDilation();
        return dilationGreyscale(greyscaleImage, structuralElement);
    }

    private BlackWhiteImage erosionBW(final BlackWhiteImage image, final StructuralElementBW structuralElement) {
        final int height = image.getHeight();
        final int width = image.getWidth();
        final BlackWhiteImage newImage = new BlackWhiteImage(height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final boolean pixel = appliesPerfectly(image, structuralElement, i, j);
                newImage.setPixel(i, j, pixel);
            }
        }

        return newImage;
    }

    private GreyscaleImage erosionGreyscale(final GreyscaleImage image, final StructuralElementGreyscale structuralElement) {
        final int height = image.getHeight();
        final int width = image.getWidth();
        final GreyscaleImage newImage = new GreyscaleImage(height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final int pixel = computePixelErosion(image, structuralElement, i, j);
                newImage.setPixel(i, j, pixel);
            }
        }

        return newImage;
    }

    private GreyscaleImage dilationGreyscale(final GreyscaleImage image, final StructuralElementGreyscale structuralElement) {
        final int height = image.getHeight();
        final int width = image.getWidth();
        final GreyscaleImage newImage = new GreyscaleImage(height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final int pixel = computePixelDilation(image, structuralElement, i, j);
                newImage.setPixel(i, j, pixel);
            }
        }

        return newImage;
    }

    private BlackWhiteImage dilationBW(final BlackWhiteImage image, final StructuralElementBW structuralElement) {
        final int height = image.getHeight();
        final int width = image.getWidth();
        final BlackWhiteImage newImage = new BlackWhiteImage(height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final boolean pixel = appliesPartialy(image, structuralElement, i, j);
                newImage.setPixel(i, j, pixel);
            }
        }

        return newImage;
    }

    /**
     * @param y - line of the point from image
     * @param x - column of the point from image
     * @return <true> if the white pixels of the structural element matches perfectly the image (considering it's center) and <false> otherwise
     */
    private boolean appliesPerfectly(final BlackWhiteImage image, final StructuralElementBW structuralElement, final int y, final int x) {
        for (int i = 0; i < structuralElement.getHeight(); i++) {
            for (int j = 0; j < structuralElement.getWidth(); j++) {
                if (!structuralElement.getMatrix()[i][j]) {
                    continue;
                }

                final int iImage = y + i - structuralElement.getCenter().getY();
                final int jImage = x + j - structuralElement.getCenter().getX();
                if (iImage < 0 || jImage < 0 || iImage >= image.getHeight() || jImage >= image.getWidth()) {
                    return false;
                }
                if (image.getMatrix()[iImage][jImage] != structuralElement.getMatrix()[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @param s - line of the point from image
     * @param t - column of the point from image
     * @return Min{ image[s+x][y+t] - structuralElement[x][y] |  x ∈ structuralElement.lines, y ∈ structuralElement.columns }
     */
    private int computePixelErosion(final GreyscaleImage image, final StructuralElementGreyscale structuralElement, final int s, final int t) {
        int min = L;

        for (int x = 0; x < structuralElement.getHeight(); x++) {
            for (int y = 0; y < structuralElement.getWidth(); y++) {
                final int iImage = s + x;
                final int jImage = t + y;
                if (iImage < 0 || jImage < 0 || iImage >= image.getHeight() || jImage >= image.getWidth()) {
                    continue;
                }
                final int computeValue = ImageConverter.clamp(image.getMatrix()[iImage][jImage] - structuralElement.getMatrix()[x][y]);
                if (computeValue < min) {
                    min = computeValue;
                }
            }
        }
        return min;
    }

    /**
     * @param s - line of the point from image
     * @param t - column of the point from image
     * @return Max{ image[s-x][t-y] + structuralElement[x][y] |  x ∈ structuralElement.lines, y ∈ structuralElement.columns }
     */
    private int computePixelDilation(final GreyscaleImage image, final StructuralElementGreyscale structuralElement, final int s, final int t) {
        int max = 0;

        for (int x = 0; x < structuralElement.getHeight(); x++) {
            for (int y = 0; y < structuralElement.getWidth(); y++) {
                final int iImage = s - x;
                final int jImage = t - y;
                if (iImage < 0 || jImage < 0 || iImage >= image.getHeight() || jImage >= image.getWidth()) {
                    continue;
                }
                final int computeValue = ImageConverter.clamp(image.getMatrix()[iImage][jImage] + structuralElement.getMatrix()[x][y]);
                if (computeValue > max) {
                    max = computeValue;
                }
            }
        }
        return max;
    }

    /**
     * @param y - line of the point from image
     * @param x - column of the point from image
     * @return <true> if the at least 1 white pixel of the structural element matches the image (considering it's center) and <false> otherwise
     */
    private boolean appliesPartialy(final BlackWhiteImage image, final StructuralElementBW structuralElement, final int y, final int x) {
        for (int i = 0; i < structuralElement.getHeight(); i++) {
            for (int j = 0; j < structuralElement.getWidth(); j++) {
                if (!structuralElement.getMatrix()[i][j]) {
                    continue;
                }

                final int iImage = y + i - structuralElement.getCenter().getY();
                final int jImage = x + j - structuralElement.getCenter().getX();
                if (iImage < 0 || jImage < 0 || iImage >= image.getHeight() || jImage >= image.getWidth()) {
                    continue;
                }
                if (image.getMatrix()[iImage][jImage] == structuralElement.getMatrix()[i][j]) {
                    return true;
                }
            }
        }
        return false;
    }

    public BlackWhiteImage determineContour(final BlackWhiteImage blackWhiteImage) {
        final StructuralElementBW structuralElement = createDefaultStructuralElementBW_contourDetermination();
        final BlackWhiteImage erodedImage = erosionBW(blackWhiteImage, structuralElement);
        return substractImageAndDrawOutline(blackWhiteImage, erodedImage);
    }

    public GreyscaleImage texturalSegmentation(final GreyscaleImage greyscaleImage) {
        final StructuralElementGreyscale structuralElement_Erosion = createDefaultStructuralElementGreyscale_demoErosion();
        final StructuralElementGreyscale structuralElement_Dilation = createDefaultStructuralElementGreyscale_demoDilation();
        final GreyscaleImage erodedImage = erosionGreyscale(greyscaleImage, structuralElement_Erosion);
        final GreyscaleImage dilatedImage =  dilationGreyscale(erodedImage, structuralElement_Dilation);
        return dilatedImage;
//        return drawOutline(dilatedImage);
    }

    /**
     * Draws a line between light and dark zones.
     */
    private GreyscaleImage drawOutline(final GreyscaleImage image) {
        final GreyscaleImage erodedImage = erosionGreyscale(image, createDefaultStructuralElementGreyscale_demoErosion());
        return substractImageAndDrawOutline(image, erodedImage);
    }

    /**
     * @return imageMinuend - imageSubtractor
     */
    private BlackWhiteImage substractImageAndDrawOutline(final BlackWhiteImage imageMinuend, final BlackWhiteImage imageSubtractor) {
        final int height = imageMinuend.getHeight();
        final int width = imageMinuend.getWidth();
        final BlackWhiteImage imageDifference = new BlackWhiteImage(height, width);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final boolean pixel = imageMinuend.getMatrix()[i][j] != imageSubtractor.getMatrix()[i][j];
                imageDifference.setPixel(i, j, pixel);
            }
        }
        return imageDifference;
    }

    /**
     * @return imageMinuend - imageSubtractor
     */
    private GreyscaleImage substractImageAndDrawOutline(final GreyscaleImage imageMinuend, final GreyscaleImage imageSubtractor) {
        final int height = imageMinuend.getHeight();
        final int width = imageMinuend.getWidth();
        final GreyscaleImage imageDifference = new GreyscaleImage(height, width);
        final int delta = 20;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = ImageConverter.clamp(imageMinuend.getMatrix()[i][j] - imageSubtractor.getMatrix()[i][j]);
                if (pixel < delta || pixel > L-delta) {
                    //pixel = L;
                }
                imageDifference.setPixel(i, j, pixel);
            }
        }
        return imageDifference;
    }

    private StructuralElementBW createDefaultStructuralElementBW_demoErosion() {
        final boolean[][] matrix = new boolean[2][2];
        matrix[0][0] = true;
        matrix[0][1] = true;
        matrix[1][0] = true;
        matrix[1][1] = false;
        return new StructuralElementBW(matrix, new Point2D(0, 0));
    }

    private StructuralElementBW createDefaultStructuralElementBW_demoDilation() {
        final boolean[][] matrix = new boolean[3][3];
        matrix[0][0] = true;
        matrix[0][1] = true;
        matrix[0][2] = true;
        matrix[1][0] = true;
        matrix[1][1] = true;
        matrix[1][2] = false;
        matrix[2][0] = true;
        matrix[2][1] = false;
        matrix[2][2] = false;
        return new StructuralElementBW(matrix, new Point2D(1, 1));
    }

    private StructuralElementBW createDefaultStructuralElementBW_contourDetermination() {
        final boolean[][] matrix = new boolean[3][3];
        matrix[0][0] = true;
        matrix[0][1] = true;
        matrix[0][2] = true;
        matrix[1][0] = true;
        matrix[1][1] = true;
        matrix[1][2] = true;
        matrix[2][0] = true;
        matrix[2][1] = true;
        matrix[2][2] = true;
        return new StructuralElementBW(matrix, new Point2D(1, 1));
    }

    private StructuralElementGreyscale createDefaultStructuralElementGreyscale_demoErosion() {
        final int size = 3;
        final int[][] matrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = (i + j) * 10;
            }
        }
        return new StructuralElementGreyscale(matrix);
    }

    private StructuralElementGreyscale createDefaultStructuralElementGreyscale_demoDilation() {
        final int size = 6;
        final int[][] matrix = new int[size][size];
        // v1
        matrix[0][0] = 10;
        matrix[0][1] = 50;
        matrix[0][2] = 20;
        matrix[1][0] = 3;
        matrix[1][1] = 0;
        matrix[1][2] = 0;
        matrix[2][0] = 10;
        matrix[2][1] = 20;
        matrix[2][2] = 40;
        // v2
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = (i + j) * 10;
            }
        }
        return new StructuralElementGreyscale(matrix);
    }
}
