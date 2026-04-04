package repository.postgres;

import entity.postgres.EntityUser;
import entity.sqlite.EntityAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("postgresAccountRepo")
public interface SpreadSheetAccountRepo extends JpaRepository<EntityUser, Long> {

    @Query(value = "SELECT   " +
            " *    " +
            "FROM   " +
            " users A " +
            "WHERE   " +
            " A.email = ?1 ;",
            nativeQuery = true)
    List<EntityUser> getAccount(String userName, String password);

}
