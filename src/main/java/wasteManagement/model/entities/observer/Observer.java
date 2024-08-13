package wasteManagement.model.entities.observer;

import wasteManagement.model.entities.issues.Issue;

public interface Observer {
    void update(Issue issue);
}
