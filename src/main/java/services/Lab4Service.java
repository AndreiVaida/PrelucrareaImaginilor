package services;

import domain.BlackWhiteImage;
import domain.Point2D;
import domain.StructuralElement;

public class Lab4Service {

    public BlackWhiteImage erosionBW(final BlackWhiteImage blackWhiteImage) {
        final StructuralElement structuralElement = createDefaultStructuralElement1();
        return erosionBW(blackWhiteImage, structuralElement);
    }

    public BlackWhiteImage dilationBW(final BlackWhiteImage blackWhiteImage) {
        final StructuralElement structuralElement = createDefaultStructuralElement2();
        return dilationBW(blackWhiteImage, structuralElement);
    }

    private BlackWhiteImage erosionBW(final BlackWhiteImage image, final StructuralElement structuralElement) {
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

    private BlackWhiteImage dilationBW(final BlackWhiteImage image, final StructuralElement structuralElement) {
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
    private boolean appliesPerfectly(final BlackWhiteImage image, final StructuralElement structuralElement, final int y, final int x) {
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
     * @param y - line of the point from image
     * @param x - column of the point from image
     * @return <true> if the at least 1 white pixel of the structural element matches the image (considering it's center) and <false> otherwise
     */
    private boolean appliesPartialy(final BlackWhiteImage image, final StructuralElement structuralElement, final int y, final int x) {
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

    private StructuralElement createDefaultStructuralElement1() {
        final boolean[][] matrix = new boolean[2][2];
        matrix[0][0] = true;
        matrix[0][1] = true;
        matrix[1][0] = true;
        matrix[1][1] = false;
        return new StructuralElement(matrix, new Point2D(0, 0));
    }

    private StructuralElement createDefaultStructuralElement2() {
        final boolean[][] matrix = new boolean[3][3];
        matrix[0][0] = false;
        matrix[0][1] = false;
        matrix[0][2] = false;
        matrix[1][0] = false;
        matrix[1][1] = true;
        matrix[1][2] = true;
        matrix[2][0] = false;
        matrix[2][1] = true;
        matrix[2][2] = true;
        return new StructuralElement(matrix, new Point2D(1, 1));
    }
}
