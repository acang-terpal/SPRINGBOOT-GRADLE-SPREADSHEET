package repository;

import entity.EntitySessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SpreadSheetSessionRepo extends JpaRepository<EntitySessions, String> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO" +
            " sessions(" +
            "id, " + //1
            "data, " + //2
            "created, " +
            "expires)" + //3
            " VALUES ( :id , :data , datetime('now'), :expires ) ;",
            nativeQuery = true)
    void setCookie(@Param("id") String id, @Param("data") String data, @Param("expires") String expires);

    @Modifying
    @Transactional
    @Query(value = "UPDATE \"main\".\"sessions\" SET " +
            "\"data\" = ?1 , " +
            "\"created\" = datetime('now'), " +
            "\"expires\" = ?2 , " +
            "\"modified\" = NULL " +
            "WHERE \"id\" = ?3 ;",
            nativeQuery = true)
    void updateCookie(String data, String expires, String id);

}
