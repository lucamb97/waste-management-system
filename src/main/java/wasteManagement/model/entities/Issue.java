package wasteManagement.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "issues")
@NoArgsConstructor
public class Issue {

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

}