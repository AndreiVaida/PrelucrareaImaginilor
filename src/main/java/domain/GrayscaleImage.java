package domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GrayscaleImage {
    private int height;
    private int width;
    private int[][] matrix;

    public GrayscaleImage(final int height, final int width) {
        this.height = height;
        this.width = width;
        matrix = new int[height][width];
    }

    public GrayscaleImage(int[][] matrix) {
        this.matrix = matrix;
        width = matrix.length;
        height = matrix[0].length;
    }

    public void setPixel(final int line, final int column, final int value) {
        matrix[line][column] = value;
    }
}
