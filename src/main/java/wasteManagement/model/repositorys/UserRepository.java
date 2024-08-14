package wasteManagement.model.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wasteManagement.model.entities.UserInfo;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, String> {

    @Query("SELECT u FROM User u JOIN Authority a ON a.user.username = u.username WHERE a.authority = 'ROLE_WORKER'")
    List<UserInfo> findWorkers();

    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.username = :username")
    void deleteUser(@Param("username") String username);
}
