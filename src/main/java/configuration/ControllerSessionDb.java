package configuration;

import com.google.common.base.Throwables;
import entity.EntityConfig;
import helper.Helper;
import helper.Utilities;
import net.sf.ehcache.Cache;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import service.SpreadSheetSessionService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author hasan
 */
@Service("ControllerSessionDb")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ControllerSessionDb {

    private final EntityConfig entityConfig;
    private final SpreadSheetSessionService SpreadSheetSessionService;
    private final Cache cache;
    private final Helper helper;
    private final Utilities utilities;
    private final ControllerLog controllerLog;

    @Autowired
    public ControllerSessionDb(Cache cacheMaster, Cache cacheCookies, EntityConfig entityConfig, ControllerLog controllerLog, Utilities utilities, Helper helper, SpreadSheetSessionService SpreadSheetSessionService) {
        this.cache = cacheCookies;
        this.entityConfig = entityConfig;
        this.controllerLog = controllerLog;
        this.utilities = utilities;
        this.helper = helper;
        this.SpreadSheetSessionService = SpreadSheetSessionService;
    }

    public HashMap checkExistCookie(HashMap objReq) {
        HashMap<String, Object>  objResp = new HashMap<>();
        HashMap<String, Object>  hashObj = new HashMap<>();
        String sessionId = objReq.get("sessId").toString();
        hashObj.put("cookie", sessionId);
        HashMap objSession = this.SpreadSheetSessionService.getCookie(hashObj);
        if (objSession.containsKey("session_id")) {
            //check exipire
            String timeExpired = objSession.get("timestamp_expired").toString();
            objResp = this.calculateExpire(timeExpired);
            if (objResp.get("result").toString().equalsIgnoreCase("true")) {
                objResp.put("result", "false");
            } else {
                objResp.put("result", "true");
            }
            return objResp;
        } else {
            objResp.put("result", "false");
            return objResp;
        }
    }

    public HashMap checkExpireCookie(HashMap objReq) {
        HashMap<String, Object>  objResp = new HashMap<>();
        HashMap<String, Object>  hashObj = new HashMap<>();
        String sessionId = objReq.get("sessId").toString();
        hashObj.put("cookie", sessionId);
        HashMap objSession = this.SpreadSheetSessionService.getCookie(hashObj);
        if (objSession.containsKey("session_id")) {
            //check expire
            String timeExpired = objSession.get("timestamp_expired").toString();
            objSession = this.calculateExpire(timeExpired);
            return objSession;
        } else {
            objSession.put("result", "true");
            return objSession;
        }
    }

    public HashMap calculateExpire(String expireDate) {
        HashMap<String,String> objResp = new HashMap<>();
        Date d1 = null;
        Date d2 = null;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        long totalDiffSecond = 0;
        try {
            String dateStart = format.format(date);
            String dateStop = expireDate;

            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            long secondDay = diffDays * 1440 * 60;
            long secondHour = diffHours * 60 * 60;
            long secondMinute = diffMinutes * 60;
            long secondSec = diffSeconds;
            totalDiffSecond = secondDay + secondHour + secondMinute + secondSec;
        } catch (Exception e) {
            String s = Throwables.getStackTraceAsString(e);
            controllerLog.logErrorWriter(s);
        }
        if (totalDiffSecond <= 0) {
            //this.removeCookie(jsonReq);
            objResp.put("result", "true");
            return objResp;
        } else {
            objResp.put("result", "false");
            return objResp;
        }
    }

    public HashMap setCookie(HashMap objReq) {
        HashMap<String, Object> hashObj = new HashMap<>();
        try {
            String sessionId = objReq.get("sessId").toString();
            if(sessionId.equals("")){
                sessionId = this.helper.createID() + this.helper.createID();
            }

            //save cookie to cache
            Calendar calendar = Calendar.getInstance();
            int sessionTtl = Integer.parseInt(this.entityConfig.getSessionTtl());
            if (objReq.containsKey("sessionTtl")) {
                sessionTtl = Integer.parseInt(objReq.get("sessionTtl").toString());
            }
            calendar.add(Calendar.SECOND, sessionTtl);
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmmss");
            String formatted = format.format(calendar.getTime());

            hashObj.put("cookie", sessionId);
            hashObj.put("expireDate", formatted);
            hashObj.put("maxAge", String.valueOf(sessionTtl));

            HashMap jObjSession = this.SpreadSheetSessionService.getCookie(hashObj);
            if (jObjSession.containsKey("session_id")) {
                hashObj = this.SpreadSheetSessionService.updateCookie(hashObj);
            } else {
                hashObj = this.SpreadSheetSessionService.setCookie(hashObj);
                //System.out.println(Arrays.asList(hashObj));
            }

            hashObj = this.helper.hashStringToHashMap(hashObj.get("session_value").toString());
            hashObj.put("cookie", sessionId);
            hashObj.put("expireDate", formatted);
            hashObj.put("maxAge", String.valueOf(sessionTtl));
        } catch (Exception ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        }
        return hashObj;
    }

    public HashMap renewCookie(HashMap objReq) {
        HashMap<String, Object> hashObj = new HashMap<>();
        try {
            String sessionId = objReq.get("sessId").toString();
            //save cookie to cache
            Calendar calendar = Calendar.getInstance();
            int sessionTtl = Integer.parseInt(this.entityConfig.getSessionTtl());
            if (objReq.containsKey("sessionTtl")) {
                sessionTtl = Integer.parseInt(objReq.get("sessionTtl").toString());
            }
            calendar.add(Calendar.SECOND, sessionTtl);
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmmss");
            String formatted = format.format(calendar.getTime());
            hashObj.put("cookie", sessionId);

            //copy others data from old cookie
            HashMap hashObjOld = this.SpreadSheetSessionService.getCookie(hashObj);
            if (hashObjOld.containsKey("session_value")) {
                HashMap jObjSessionValue = this.helper.hashStringToHashMap(hashObjOld.get("session_value").toString());
                hashObj = new HashMap<>();
                for (Iterator iterator = jObjSessionValue.keySet().iterator(); iterator.hasNext(); ) {
                    String key = (String) iterator.next();
                    //System.out.println(this.jSONObject.get(key));
                    if (!key.equals("cookie") && !key.equals("expireDate") && !key.equals("maxAge")) {
                        hashObj.put(key, String.valueOf(jObjSessionValue.get(key)));
                    }
                }
                hashObj.put("cookie", sessionId);
                hashObj.put("expireDate", formatted);
                hashObj.put("maxAge", String.valueOf(sessionTtl));
                hashObj = this.SpreadSheetSessionService.updateCookie(hashObj);
                hashObj = this.helper.hashStringToHashMap(hashObj.get("session_value").toString());
            } else {
                hashObj = new JSONObject();
                hashObj.put("result", "false");
            }
        } catch (Exception ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        }
        return hashObj;
    }

    public HashMap getDataCookie(HashMap objReq) {
        HashMap<String, Object> hashObj = new HashMap<>();
        try {
            String sessionId = objReq.get("sessId").toString();
            hashObj.put("cookie", sessionId);
            hashObj = this.SpreadSheetSessionService.getCookie(hashObj);
            if (!hashObj.containsKey("session_id")) {
                hashObj = new HashMap<>();
                hashObj.put("result", "false");
            } else {
                String timeExpired = hashObj.get("timestamp_expired").toString();
                HashMap jObjExpire = this.calculateExpire(timeExpired);
                if (jObjExpire.get("result").toString().equalsIgnoreCase("true")) {
                    hashObj = new HashMap<>();
                    hashObj.put("result", "false");
                } else {
                    hashObj = this.helper.hashStringToHashMap(hashObj.get("session_value").toString());
                }
            }
        } catch (Exception ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        }
        return hashObj;
    }

    public HashMap addDataCookie(HashMap objReq) {
        HashMap<String, Object> hashObj = new HashMap<>();
        try {
            String sessionId = objReq.get("sessId").toString();
            hashObj.put("cookie", sessionId);
            HashMap hashObjOld = this.SpreadSheetSessionService.getCookie(hashObj);
            if (hashObjOld.containsKey("session_value")) {
                String timeExpired = hashObjOld.get("timestamp_expired").toString();
                HashMap objExpire = this.calculateExpire(timeExpired);
                if (objExpire.get("result").toString().equalsIgnoreCase("true")) {
                    hashObj = new HashMap<>();
                    hashObj.put("result", "false");
                    return hashObj;
                }
                HashMap objSessionValue = this.helper.hashStringToHashMap(hashObjOld.get("session_value").toString());
                objSessionValue.put(objReq.get("key").toString(), objReq.get("value").toString());
                hashObj = this.SpreadSheetSessionService.updateCookie(objSessionValue);
                if (!hashObj.containsKey("session_id")) {
                    hashObj = new HashMap<>();
                    hashObj.put("result", "false");
                } else {
                    hashObj = this.helper.hashStringToHashMap(hashObj.get("session_value").toString());
                }
            } else {
                hashObj = new HashMap<>();
                hashObj.put("result", "false");
            }
        } catch (Exception ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        }
        return hashObj;
    }

    public void removeCookie(final HashMap objReq) {
        HashMap<String, Object> hashObj =  new HashMap<>();
        hashObj.put("cookie", objReq.get("sessId").toString());
        hashObj.put("expireDate", "20000101 000000");
        hashObj.put("maxAge", String.valueOf(0));
        hashObj = this.SpreadSheetSessionService.updateCookie(hashObj);
    }
}
