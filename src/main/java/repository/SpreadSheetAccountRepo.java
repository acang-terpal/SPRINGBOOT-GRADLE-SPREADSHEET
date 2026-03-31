package repository;

import entity.EntityAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpreadSheetAccountRepo extends JpaRepository<EntityAccount, Long> {

    @Query(value = "SELECT   " +
            " *    " +
            "FROM   " +
            " ms_account A " +
            "WHERE   " +
            " A.username = ?1  " +
            " AND A.password = ?2 ;",
            nativeQuery = true)
    List<EntityAccount> getAccount(String userName, String password);

}
