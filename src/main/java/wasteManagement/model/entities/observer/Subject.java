package wasteManagement.model.entities.observer;

import wasteManagement.model.entities.issues.Issue;

import java.util.List;

public interface Subject {
    public List<Observer> observers = List.of();

    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(Issue issue);
}
