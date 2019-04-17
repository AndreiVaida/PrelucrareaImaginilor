package observer;

public interface Observable {
    void notifyObservers(final ChangePixelEvent changePixelEvent);

    void addObserver(final Observer observer);
}
