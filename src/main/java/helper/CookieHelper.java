/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import configuration.ControllerEncDec;
import configuration.ControllerLog;
import configuration.DependencyConfig;
import entity.EntityConfig;
import jakarta.servlet.http.Cookie;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hasan
 */
@Component("CookieHelper")
@Scope("prototype")
public class CookieHelper {

    private final Cache cacheCookies;
    private final ControllerLog controllerLog;
    private final EntityConfig entityConfig;
    private final Utilities utilities;
    private final Helper hlp;
    private final ControllerEncDec encDec;
    private JSONObject jSONObject;
    private Element elemCache;

    @Autowired
    public CookieHelper(DependencyConfig dependencyConfig, Utilities utilities, Helper helper, ControllerEncDec controllerEncDec) {
        this.controllerLog = dependencyConfig.getControllerLog();
        this.entityConfig = dependencyConfig.getEntityConfig();
        this.cacheCookies = dependencyConfig.getCacheCookies();
        this.utilities = utilities;
        this.hlp = helper;
        this.encDec = controllerEncDec;
    }

    public boolean checkExistCookie(String cookie) {
        if (this.cacheCookies.isKeyInCache(cookie)) {
            this.elemCache = this.cacheCookies.get(cookie);
            if (this.elemCache != null) {
                if (this.cacheCookies.isExpired(this.elemCache)) {
                    //comment this if request can be paralel from client, if client request just one at the time, uncoment
                    removeCookie(cookie);
                    return false;
                } else {
                    this.jSONObject = (JSONObject) elemCache.getObjectValue();
                    //System.out.println("private cookie : " + this.jSONObject.toString());
                    return true;
                }
            } else {
                //comment this if request can be paralel from client, if client request just one at the time, uncoment
                removeCookie(cookie);
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean checkExpireCookie(String cookie) {
        this.elemCache = this.cacheCookies.get(cookie);
        this.jSONObject = (JSONObject) elemCache.getObjectValue();
        String cookieStore = (String) this.jSONObject.get("cookie");
        String expireDate = (String) this.jSONObject.get("expireDate");

        //System.out.println("expireDate :" + expireDate);
        Date d1 = null;
        Date d2 = null;

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmmss");
        Date date = new Date();

        String dateStart = format.format(date);
        String dateStop = expireDate;
        long totalDiffSecond = 0;
        try {
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
            e.printStackTrace();
        }
        if (totalDiffSecond <= 0) {
            return true; //has expire
        } else {
            return false; //not expire
        }
    }

    public Cookie setCookie(String oldCookie) {
        //CookieSetting(version, name, value);
        Cookie cS = new Cookie(" SpreadSheetCookie", this.hlp.createID() + this.hlp.createID());
        cS.setMaxAge(Integer.parseInt(this.entityConfig.getSessionTtl()));
        cS.setSecure(true);
        cS.setHttpOnly(true);
        cS.setPath("/");
        //cookie.setDomain("example.com");
        //save cookie to cache
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, cS.getMaxAge());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmmss");
        String formatted = format.format(calendar.getTime());
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("cookie", cS.getValue());
        jSONObject.put("expireDate", formatted);
        //copy others data from old cookie
        if (!oldCookie.equals("")) {
            this.elemCache = this.cacheCookies.get(oldCookie);
            this.jSONObject = (JSONObject) this.elemCache.getObjectValue();
            for (Iterator iterator = this.jSONObject.keySet().iterator(); iterator.hasNext(); ) {
                String key = (String) iterator.next();
                //System.out.println(this.jSONObject.get(key));
                if (!key.equals("cookie") && !key.equals("expireDate")) {
                    jSONObject.put(key, this.jSONObject.get(key));
                }
            }
            //remove old cookies
            if (this.cacheCookies.isKeyInCache(oldCookie)) {
                //comment this if request can be paralel from client, if client request just one at the time, uncoment
                this.removeCookie(oldCookie);
            }
        }
        this.cacheCookies.put(new Element(cS.getValue(), jSONObject));
        return cS;
    }

    public Cookie renewCookie(String oldCookie) {
        //CookieSetting(version, name, value);
        Cookie cS = new Cookie(" SpreadSheetCookie", oldCookie);
        cS.setMaxAge(Integer.parseInt(this.entityConfig.getSessionTtl()));
        cS.setSecure(true);
        cS.setHttpOnly(true);
        cS.setPath("/");
        //save cookie to cache
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, cS.getMaxAge());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmmss");
        String formatted = format.format(calendar.getTime());
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("cookie", cS.getValue());
        jSONObject.put("expireDate", formatted);
        //copy others data from old cookie
        if (!oldCookie.equals("")) {
            this.elemCache = this.cacheCookies.get(oldCookie);
            this.jSONObject = (JSONObject) this.elemCache.getObjectValue();
            for (Iterator iterator = this.jSONObject.keySet().iterator(); iterator.hasNext(); ) {
                String key = (String) iterator.next();
                //System.out.println(this.jSONObject.get(key));
                if (!key.equals("cookie") && !key.equals("expireDate")) {
                    jSONObject.put(key, this.jSONObject.get(key));
                }
            }
        }
        this.cacheCookies.put(new Element(cS.getValue(), jSONObject));
        return cS;
    }

    public JSONObject getDataCookie(String cookie) {
        this.elemCache = this.cacheCookies.get(cookie);
        this.jSONObject = (JSONObject) this.elemCache.getObjectValue();
        return this.jSONObject;
    }

    public void addDataCookie(String cookie, String key, String value) {
        this.elemCache = this.cacheCookies.get(cookie);
        this.jSONObject = (JSONObject) this.elemCache.getObjectValue();
        this.jSONObject.put(key, value);
        this.cacheCookies.put(new Element(cookie, jSONObject));
    }

    public void removeCookie(final String cookie) {
        //avoid session expire when multithread http request trigger remove cookies same time, so we remove old cookie 2 minute after
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(Integer.parseInt(entityConfig.getTimeRemoveBeforeSessionDelete()));
                    cacheCookies.remove(cookie);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public String respExpire() {
        String resp = "";
        JSONObject jsonResp = new JSONObject();
        jsonResp.put("ACK", "NOK");
        jsonResp.put("pesan", "session expire");
        try {
            resp = jsonResp.toString();
        } catch (Exception ex) {
            Logger.getLogger(CookieHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resp;
    }
}
