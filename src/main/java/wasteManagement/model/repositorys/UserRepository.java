package wasteManagement.model.repositorys;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import wasteManagement.model.entities.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u JOIN Authority a ON a.user.username = u.username WHERE a.authority = 'ROLE_WORKER'")
    List<User> findWorkers();
}
