package domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlackWhiteImage {
    private int height;
    private int width;
    private boolean[][] matrix;

    public BlackWhiteImage(final int height, final int width) {
        this.height = height;
        this.width = width;
        matrix = new boolean[height][width];
    }

    public BlackWhiteImage(boolean[][] matrix) {
        this.matrix = matrix;
        width = matrix.length;
        height = matrix[0].length;
    }

    public void setPixel(final int line, final int column, final boolean value) {
        matrix[line][column] = value;
    }
}
