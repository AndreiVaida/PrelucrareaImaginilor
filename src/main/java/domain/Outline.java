package domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Outline {
    private int height;
    private int width;
    private boolean[][] matrix;

    public Outline(final int height, final int width) {
        this.height = height;
        this.width = width;
        matrix = new boolean[height][width];
    }

    public Outline(boolean[][] matrix) {
        this.matrix = matrix;
        width = matrix.length;
        height = matrix[0].length;
    }

    public Outline(final Outline outline) {
        matrix = new boolean[outline.height][outline.width];
        width = matrix.length;
        height = matrix[0].length;
        for (int i = 0; i < height; i++) {
            System.arraycopy(outline.matrix[i], 0, matrix[i], 0, width);
        }
    }

    public void setPixel(final int line, final int column, final boolean value) {
        matrix[line][column] = value;
    }
}
