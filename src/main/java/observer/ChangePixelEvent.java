package observer;

import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChangePixelEvent {
    private final int x;
    private final int y;
    private final Color color;;
}
