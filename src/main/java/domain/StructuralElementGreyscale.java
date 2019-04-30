package domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StructuralElementGreyscale {
    private int height;
    private int width;
    private int[][] matrix;

    public StructuralElementGreyscale(final int[][] matrix) {
        height = matrix.length;
        width = matrix[0].length;
        this.matrix = matrix;
    }
}
