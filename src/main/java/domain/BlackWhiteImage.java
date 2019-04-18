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

    public BlackWhiteImage(final BlackWhiteImage blackWhiteImage) {
        height = blackWhiteImage.height;
        width = blackWhiteImage.width;
        matrix = new boolean[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                matrix[i][j] = blackWhiteImage.matrix[i][j];
            }
        }
    }

    public void setPixel(final int line, final int column, final boolean value) {
        matrix[line][column] = value;
    }
}
