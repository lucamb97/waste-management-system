package wasteManagement.model.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserData, Long> {
    //This query returns all bins from a selected city
    @Query("SELECT u FROM UserData u WHERE u.username = :username")
    UserData findByUsername(@Param("username") String username);
}
