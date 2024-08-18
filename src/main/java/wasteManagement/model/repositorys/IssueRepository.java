package wasteManagement.model.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wasteManagement.model.entities.Bin;
import wasteManagement.model.entities.issues.Issue;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE Issue i SET i.assignedWorker = :worker WHERE i.id = :issueId")
    void assignIssueToWorker(@Param("worker") String worker, @Param("issueId") Long issueId);

    @Query("SELECT i FROM Issue i WHERE i.createdBy = :username")
    List<Issue> findByUsername(@Param("username") String username);
}
