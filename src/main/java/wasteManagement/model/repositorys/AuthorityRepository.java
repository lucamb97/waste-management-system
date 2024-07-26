package wasteManagement.model.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wasteManagement.model.entitys.Authority;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
