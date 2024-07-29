package wasteManagement.model.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import wasteManagement.model.entities.Bin;


import java.util.List;

@Repository
public interface BinsRepository extends JpaRepository<Bin, Long> {

    //This query returns the selected bin from id
    @Query("SELECT b FROM Bin b WHERE b.id = :id")
    Bin findById(@Param("id") long id);

    //This query returns the selected bin from the associated user
    @Query("SELECT b FROM Bin b WHERE b.user = :user")
    List<Bin> findByUser(@Param("user") String user);

    //This query returns all bins from a selected city
    @Query("SELECT b FROM Bin b WHERE b.city = :city")
    List<Bin> findByCity(@Param("city") String city);

    //This query returns all bins from a selected city
    //that need to be emptied and are not already on a workers list
    @Query("SELECT b FROM Bin b WHERE b.city = :city" +
            " AND b.needsEmptying = true" +
            " AND b.beingEmptied != true")
    List<Bin> findByNeedEmptying(@Param("city") String city);

    //This query takes a list of bins as input and changes their state
    //to beingEmptied True
    @Modifying
    @Transactional
    @Query("UPDATE Bin b " +
            "SET b.beingEmptied = true " +
            "WHERE b.id IN :binIds")
    void updateBeingEmptied(@Param("binIds") List<Long> binIds);

    @Modifying
    @Transactional
    @Query("UPDATE Bin b " +
            "SET b.beingEmptied = false, b.needsEmptying = false " +
            "WHERE b.id = :id")
    void binEmptied(@Param("id") Long Id);
}
