package service.sqlite;

import entity.sqlite.EntitySessions;
import helper.Utilities;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.sqlite.SpreadSheetSessionRepo;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class SpreadSheetSessionService {

    private final Utilities utilities;
    private final SpreadSheetSessionRepo SpreadSheetSessionRepo;
    @PersistenceContext
    EntityManager em;

    @Autowired
    public SpreadSheetSessionService(SpreadSheetSessionRepo SpreadSheetSessionRepo, Utilities utilities) {
        this.SpreadSheetSessionRepo = SpreadSheetSessionRepo;
        this.utilities = utilities;
    }

    public HashMap<String, Object> getCookie(HashMap<String, Object> hashObj){
        //select (actual native query inside EntitySessions.class)
        Query query = em.createNamedQuery("getCookie");
        query.setParameter("sessionId", hashObj.get("cookie").toString());
        List<EntitySessions> sessionEntity = query.getResultList();
        HashMap<String, Object> result = new HashMap<>();
        if(sessionEntity.size() > 0){
            result = (HashMap<String, Object>) this.utilities.convertObjectToMap(sessionEntity.get(0));
        }
        return result;
    }

    public HashMap<String, Object> setCookie(HashMap<String, Object> hashObj){
        HashMap<String, Object> result = new HashMap();
        try {
            //insert
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
            Date parsedDate = dateFormat.parse(hashObj.get("expireDate").toString());
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            SpreadSheetSessionRepo.setCookie(
                    hashObj.get("cookie").toString(),
                    hashObj.toString(),
                    timestamp.toString());
            //select
            Query query = em.createNamedQuery("getCookie");
            query.setParameter("sessionId", hashObj.get("cookie").toString());
            List<EntitySessions> sessionEntity = query.getResultList();

            if(sessionEntity.size() > 0) {
                result = (HashMap<String, Object>) this.utilities.convertObjectToMap(sessionEntity.get(0));
                //convert object to hashmap via jackson
                //ObjectMapper objectMapper = new ObjectMapper();
                //result = objectMapper.convertValue(sessionEntity.get(0), HashMap.class);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    public HashMap<String, Object> updateCookie(HashMap<String, Object> hashObj){
        HashMap<String, Object> result = new HashMap();
        try {
            //insert
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
            Date parsedDate = dateFormat.parse(hashObj.get("expireDate").toString());
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            SpreadSheetSessionRepo.updateCookie(
                    hashObj.toString(),
                    timestamp.toString(),
                    hashObj.get("cookie").toString());
            //select
            Query query = em.createNamedQuery("getCookie");
            query.setParameter("sessionId", hashObj.get("cookie").toString());
            List<EntitySessions> sessionEntity = query.getResultList();

            if(sessionEntity.size() > 0) {
                result = (HashMap<String, Object>) this.utilities.convertObjectToMap(sessionEntity.get(0));
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return result;
    }
}
