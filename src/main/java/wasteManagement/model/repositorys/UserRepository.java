package wasteManagement.model.repositorys;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wasteManagement.model.entitys.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}