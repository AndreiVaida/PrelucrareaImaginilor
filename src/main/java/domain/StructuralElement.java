package domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StructuralElement {
    private int height;
    private int width;
    private boolean[][] matrix;
    private Point2D center;

    public StructuralElement(final boolean[][] matrix, final Point2D center) {
        height = matrix.length;
        width = matrix[0].length;
        this.matrix = matrix;
        this.center = center;
    }
}
