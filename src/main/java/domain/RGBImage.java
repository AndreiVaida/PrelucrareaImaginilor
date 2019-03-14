package domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RGBImage {
    private int height;
    private int width;
    private int[][] redMatrix;
    private int[][] greenMatrix;
    private int[][] blueMatrix;

    /**
     * Creates an empty RGB image with the given sizes.
     */
    public RGBImage(final int height, final int width) {
        this.height = height;
        this.width = width;
        redMatrix = new int[height][width];
        greenMatrix = new int[height][width];
        blueMatrix = new int[height][width];
    }

    /**
     * Creates the image with the given non-empty matrices.
     * The width and the height are set according to the redMatrix dimensions.
     */
    public RGBImage(int[][] redMatrix, int[][] greenMatrix, int[][] blueMatrix) {
        this.redMatrix = redMatrix;
        this.greenMatrix = greenMatrix;
        this.blueMatrix = blueMatrix;
        width = redMatrix.length;
        height = redMatrix[0].length;
    }

    public void setPixel(final int line, final int column, final int redComponent, final int greenComponent, final int blueComponent) {
        redMatrix[line][column] = redComponent;
        greenMatrix[line][column] = greenComponent;
        blueMatrix[line][column] = blueComponent;
    }
}
