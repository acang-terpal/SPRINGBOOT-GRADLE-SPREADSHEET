package repository;

import entity.EntityHtb;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpreadSheetHtbRepo extends JpaRepository<EntityHtb, String> {

    @Query(value = "select * from ms_htb where  parent_id is NULL ; #{#pageable}",
            countQuery = "SELECT count(*) FROM ms_htb ;",
            nativeQuery = true)
    Page<EntityHtb> findAllParentRootPaginated(Pageable pageable);

    @Query(value = "select * from ms_htb where  parent_id = :classId order by class_id asc limit :pageSize offset :pageNumber ;",
            countQuery = "select * from ms_htb where  parent_id = ?1 ;",
            nativeQuery = true)
    List<EntityHtb> findAllChildrenPaginated(@Param("classId") String classId,
                                             @Param("pageSize") int pageSize,
                                             @Param("pageNumber") int pageNumber);

    // Tarik semua ROOT (yang gak punya bapak) + langsung angkut semua ANAKNYA
    @Query("SELECT DISTINCT h FROM EntityHtb h " +
            "LEFT JOIN FETCH h.childrenHtbClass c " +       // Tarik Anak (Tier 1)
            "LEFT JOIN FETCH c.childrenHtbClass cc " +      // Tarik Cucu (Tier 2)
            "WHERE h.parentHtbClass IS NULL")               // Mulai dari Kakek (Root)
    List<EntityHtb> findHtbTree();

}
