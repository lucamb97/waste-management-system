package wasteManagement.model.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import wasteManagement.model.entitys.Bin;

import java.util.List;

@Repository
public interface BinsRepository extends JpaRepository<Bin, Long> {

    //This query returns all bins from a selected city
    @Query("SELECT b FROM Bin b WHERE b.city = :city")
    List<Bin> findByCity(@Param("city") String city);

    //This query returns all bins from a selected city
    //that need to be emptied and are not already on a workers list
    @Query("SELECT b FROM Bin b WHERE b.city = :city" +
            " AND b.needsEmptying = true" +
            " AND b.beingEmptied != true")
    List<Bin> findByNeedEmptying(@Param("city") String city);
}
