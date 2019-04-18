package observer;

public interface Observable {
    void notifyObservers(final Event event);

    void addObserver(final Observer observer);
}
