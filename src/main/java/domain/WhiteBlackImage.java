package domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WhiteBlackImage {
    private int height;
    private int width;
    private boolean[][] matrix;

    public WhiteBlackImage(final int height, final int width) {
        this.height = height;
        this.width = width;
        matrix = new boolean[height][width];
    }

    public WhiteBlackImage(final WhiteBlackImage whiteBlackImage) {
        height = whiteBlackImage.height;
        width = whiteBlackImage.width;
        matrix = new boolean[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                matrix[i][j] = whiteBlackImage.matrix[i][j];
            }
        }
    }

    public void setPixel(final int line, final int column, final boolean value) {
        matrix[line][column] = value;
    }
}
