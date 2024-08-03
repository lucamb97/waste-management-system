package wasteManagement.model.entities.observer;

import wasteManagement.model.entities.Issue;

public interface Observer {
    void update(Issue issue);
}
