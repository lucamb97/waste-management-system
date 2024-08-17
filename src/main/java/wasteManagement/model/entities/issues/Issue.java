package wasteManagement.model.entities.issues;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import wasteManagement.model.repositorys.BinsRepository;
import wasteManagement.model.repositorys.IssueRepository;

import java.time.LocalDateTime;

//This is the abstract class for the Factory design pattern
@Data
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "issue_type")
@Table(name = "issues")
public abstract class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "issue_description", nullable = false)
    private String issueDescription;

    @Column(name = "bin_id", nullable = false)
    private Long binId;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "assigned_worker")
    private String assignedWorker;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved", nullable = false)
    private boolean resolved = false;

    public String getIssueType() {
        //This method should return the discriminator value
        return this.getClass().getAnnotation(DiscriminatorValue.class).value();
    }

    // Abstract method that each concrete class will implement
    //to check if the issue was handled correctly
    public abstract void handle(BinsRepository binsRepository, IssueRepository issueRepository, IssueFactory issueFactory, Boolean fixed);
}