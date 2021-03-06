package domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GreyscaleImage {
    private int height;
    private int width;
    private int[][] matrix;

    public GreyscaleImage(final int height, final int width) {
        this.height = height;
        this.width = width;
        matrix = new int[height][width];
    }

    public GreyscaleImage(final GreyscaleImage greyscaleImage) {
        height = greyscaleImage.height;
        width = greyscaleImage.width;
        matrix = new int[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                matrix[i][j] = greyscaleImage.matrix[i][j];
            }
        }
    }

    public void setPixel(final int line, final int column, final int value) {
        matrix[line][column] = value;
    }
}
