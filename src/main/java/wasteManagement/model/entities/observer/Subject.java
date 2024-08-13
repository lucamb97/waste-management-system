package wasteManagement.model.entities.observer;

import wasteManagement.model.entities.issues.Issue;

public interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(Issue issue);
}
