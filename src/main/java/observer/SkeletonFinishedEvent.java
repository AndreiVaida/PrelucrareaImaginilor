package observer;

import domain.Skeleton;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SkeletonFinishedEvent implements Event {
    final Skeleton skeleton;
}
