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

    public Outline(final Outline outline) {
        matrix = new boolean[outline.height][outline.width];
        height = matrix.length;
        width = matrix[0].length;
        for (int i = 0; i < height; i++) {
            System.arraycopy(outline.matrix[i], 0, matrix[i], 0, width);
        }
    }

    public void setPixel(final int line, final int column, final boolean value) {
        matrix[line][column] = value;
    }
}
