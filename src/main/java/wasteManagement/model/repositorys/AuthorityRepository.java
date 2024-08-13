package wasteManagement.model.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wasteManagement.model.entities.Authority;

import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Authority a WHERE a.user.username = :username")
    void deleteUser(@Param("username") String username);

    @Query("SELECT authority FROM Authority a WHERE a.user.username = :username")
    List<String> findRolesByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query("DELETE FROM Authority a WHERE a.user.username = :username AND a.authority = :role")
    void deleteUserRole(@Param("username") String username, @Param("role")String role);
}
