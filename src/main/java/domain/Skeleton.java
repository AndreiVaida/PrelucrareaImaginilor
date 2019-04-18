package domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Skeleton {
    private int height;
    private int width;
    private int[][] matrix;
    private int maxHeight = 0;

    public Skeleton(final int height, final int width) {
        this.height = height;
        this.width = width;
        matrix = new int[height][width];
    }

    public Skeleton(final int[][] matrix) {
        this.height = matrix.length;
        this.width = matrix[0].length;
        this.matrix = new int[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                this.matrix[i][j] = matrix[i][j];
            }
        }
    }

    public void setPixel(final int line, final int column, final int value) {
        matrix[line][column] = value;
    }
}
