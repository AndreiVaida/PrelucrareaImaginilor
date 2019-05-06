package domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StructuralElementWB {
    private int height;
    private int width;
    private boolean[][] matrix;
    private Point2D center;

    public StructuralElementWB(final boolean[][] matrix, final Point2D center) {
        height = matrix.length;
        width = matrix[0].length;
        this.matrix = matrix;
        this.center = center;
    }
}
