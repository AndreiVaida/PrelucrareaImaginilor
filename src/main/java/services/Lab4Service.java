package services;

import converters.ImageConverter;
import domain.GreyscaleImage;
import domain.Point2D;
import domain.StructuralElementGreyscale;
import domain.StructuralElementWB;
import domain.WhiteBlackImage;

import static app.Main.L;

public class Lab4Service {

    public WhiteBlackImage erosionWB(final WhiteBlackImage whiteBlackImage) {
        final StructuralElementWB structuralElement = createDefaultStructuralElementBW_demoErosion();
        return erosionWB(whiteBlackImage, structuralElement);
    }

    public WhiteBlackImage dilationWB(final WhiteBlackImage whiteBlackImage) {
        final StructuralElementWB structuralElement = createDefaultStructuralElementBW_demoDilation();
        return dilationWB(whiteBlackImage, structuralElement);
    }

    public GreyscaleImage erosionGreyscale(final GreyscaleImage greyscaleImage) {
        final StructuralElementGreyscale structuralElement = createDefaultStructuralElementGreyscale_demoErosion();
        return erosionGreyscale(greyscaleImage, structuralElement);
    }

    public GreyscaleImage dilationGreyscale(final GreyscaleImage greyscaleImage) {
        final StructuralElementGreyscale structuralElement = createDefaultStructuralElementGreyscale_demoDilation();
        return dilationGreyscale(greyscaleImage, structuralElement);
    }

    private WhiteBlackImage erosionWB(final WhiteBlackImage image, final StructuralElementWB structuralElement) {
        final int height = image.getHeight();
        final int width = image.getWidth();
        final WhiteBlackImage newImage = new WhiteBlackImage(height, width);

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

    private WhiteBlackImage dilationWB(final WhiteBlackImage image, final StructuralElementWB structuralElement) {
        final int height = image.getHeight();
        final int width = image.getWidth();
        final WhiteBlackImage newImage = new WhiteBlackImage(height, width);

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
    private boolean appliesPerfectly(final WhiteBlackImage image, final StructuralElementWB structuralElement, final int y, final int x) {
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
    private boolean appliesPartialy(final WhiteBlackImage image, final StructuralElementWB structuralElement, final int y, final int x) {
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

    public WhiteBlackImage determineContour(final WhiteBlackImage whiteBlackImage) {
        final StructuralElementWB structuralElement = createDefaultStructuralElementBW_contourDetermination();
        final WhiteBlackImage erodedImage = erosionWB(whiteBlackImage, structuralElement);
        return substractImage(whiteBlackImage, erodedImage);
    }

    public GreyscaleImage texturalSegmentation(final GreyscaleImage greyscaleImage) {
        final StructuralElementGreyscale structuralElement_Erosion = createDefaultStructuralElementGreyscale_demoErosion();
        final StructuralElementGreyscale structuralElement_Dilation = createDefaultStructuralElementGreyscale_demoDilation();
        final GreyscaleImage erodedImage = erosionGreyscale(greyscaleImage, structuralElement_Erosion);
        final GreyscaleImage dilatedImage = dilationGreyscale(erodedImage, structuralElement_Dilation);
        return drawOutline(dilatedImage);
    }

    /**
     * Draws a line between light and dark zones.
     */
    private GreyscaleImage drawOutline(final GreyscaleImage image) {
        final GreyscaleImage erodedImage = erosionGreyscale(image, createDefaultStructuralElementGreyscale_demoErosion());
//        return substractImage(image, erodedImage);
        final GreyscaleImage newImage = new GreyscaleImage(image);
        final int minDifference = 50;
        for (int i = 0; i < erodedImage.getHeight(); i++) {
            for (int j = 0; j < erodedImage.getWidth(); j++) {
                final Integer[][] neighboors = Lab2Service.getNeighborPixels(image.getMatrix(), 5, i, j);
                final int pixel = image.getMatrix()[i][j];
                if (max(neighboors) - pixel > minDifference || pixel - min(neighboors) > minDifference) {
                    newImage.setPixel(i, j, L);
                }
            }
        }
        return newImage;
    }

    private int max(final Integer[][] matrix) {
        int max = matrix[0][0];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] > max) {
                    max = matrix[i][j];
                }
            }
        }
        return max;
    }

    private int min(final Integer[][] matrix) {
        int min = matrix[0][0];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] < min) {
                    min = matrix[i][j];
                }
            }
        }
        return min;
    }

    /**
     * @return imageMinuend - imageSubtractor
     */
    private WhiteBlackImage substractImage(final WhiteBlackImage imageMinuend, final WhiteBlackImage imageSubtractor) {
        final int height = imageMinuend.getHeight();
        final int width = imageMinuend.getWidth();
        final WhiteBlackImage imageDifference = new WhiteBlackImage(height, width);

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
    private GreyscaleImage substractImage(final GreyscaleImage imageMinuend, final GreyscaleImage imageSubtractor) {
        final int height = imageMinuend.getHeight();
        final int width = imageMinuend.getWidth();
        final GreyscaleImage imageDifference = new GreyscaleImage(height, width);
        final int delta = 20;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final int pixel = ImageConverter.clamp(imageMinuend.getMatrix()[i][j] - imageSubtractor.getMatrix()[i][j]);
                imageDifference.setPixel(i, j, pixel);
            }
        }
        return imageDifference;
    }

    private StructuralElementWB createDefaultStructuralElementBW_demoErosion() {
        final boolean[][] matrix = new boolean[2][2];
        matrix[0][0] = true;
        matrix[0][1] = true;
        matrix[1][0] = true;
        matrix[1][1] = false;
        return new StructuralElementWB(matrix, new Point2D(0, 0));
    }

    private StructuralElementWB createDefaultStructuralElementBW_demoDilation() {
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
        return new StructuralElementWB(matrix, new Point2D(1, 1));
    }

    private StructuralElementWB createDefaultStructuralElementBW_contourDetermination() {
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
        return new StructuralElementWB(matrix, new Point2D(1, 1));
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
        final int size = 4;
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

    /**
     * x ⊂ x2
     * Inclusion refers white pixels, not dimensions.
     */
    public void testInclusionProperty(final WhiteBlackImage x, final WhiteBlackImage x2) {
        final StructuralElementWB b = createDefaultStructuralElementBW_demoErosion();
        final StructuralElementWB b2 = createDefaultStructuralElementBW_demoDilation(); // b ⊂ b2

        // Dacă x ⊂ x2 atunci x-b ⊂ x2-b, unde „-” înseamnă „eroziune”
        final WhiteBlackImage x_b = erosionWB(x, b);
        final WhiteBlackImage x2_b = erosionWB(x2, b);
        final boolean x_bIsIncludedInX2_b = included(x_b, x2_b);
        System.out.println("Dacă x ⊂ x2 atunci x-b ⊂ x2-b (unde „-” înseamnă „eroziune”): " + x_bIsIncludedInX2_b);

        // Dacă x ⊂ x2 atunci x+b ⊂ x2+b2, unde „+” înseamnă „dilatare”
        final WhiteBlackImage xPb = dilationWB(x, b);
        final WhiteBlackImage x2Pb2 = dilationWB(x2, b2);
        final boolean xPbIsIncludedInX2Pb2 = included(xPb, x2Pb2);
        System.out.println("Dacă x ⊂ x2 atunci x+b ⊂ x2+b2 (unde „+” înseamnă „dilatare”): " + xPbIsIncludedInX2Pb2);

        // Dacă b ⊂ b2 atunci x-b ⊂ x-b2, unde „-” înseamnă „eroziune”
        final WhiteBlackImage x_b2 = erosionWB(x, b2);
        final boolean x_bIsIncludedInX_b2 = included(x_b, x_b2);
        System.out.println("Dacă b ⊂ b2 atunci x-b ⊂ x-b2 (unde „-” înseamnă „eroziune”): " + x_bIsIncludedInX_b2);
    }

    /**
     * @return <true> if image1 ⊂ image2 and <false> otherwise
     * Inclusion refers white pixels, not dimensions.
     */
    private boolean included(final WhiteBlackImage image1, final WhiteBlackImage image2) {
        final int height = image1.getHeight();
        final int width = image1.getWidth();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                final boolean pixel1 = image1.getMatrix()[i][j];
                if (!pixel1) {
                    continue;
                }
                final boolean pixel2 = image2.getMatrix()[i][j];
                if (!pixel2) {
                    return false;
                }
            }
        }
        return true;
    }
}
