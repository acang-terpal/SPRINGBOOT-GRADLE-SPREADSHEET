/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import configuration.ControllerLog;
import entity.EntityConfig;
import jakarta.servlet.http.Cookie;
import net.sf.ehcache.Cache;
import org.json.simple.JSONObject;

/**
 * @author Hasan
 */
public class CookieHelperExternal {
    private final EntityConfig entityConfig;
    private final Helper helper;
    private final Utilities utilities;
    private final ControllerLog controllerLog;

    public CookieHelperExternal(EntityConfig entityConfig, Cache cacheMaster, Cache cacheCookies, Helper hlp, Utilities utilities, ControllerLog controllerLog) {
        this.entityConfig = entityConfig;
        this.utilities = utilities;
        this.controllerLog = controllerLog;
        this.helper = hlp;
    }

    public boolean checkExistCookie(String cookie) {
        JSONObject jsonReq = new JSONObject();
        jsonReq.put("sessId", cookie);
        JSONObject jsonResp = this.utilities.curlPost(this.entityConfig.getUrlSession() + "/checkExistCookie", jsonReq.toString(), true);
        if(jsonResp.get("result").toString().equals("true")){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkExpireCookie(String cookie) {
        JSONObject jsonReq = new JSONObject();
        jsonReq.put("sessId", cookie);

        JSONObject jSONObject = this.utilities.curlPost(this.entityConfig.getUrlSession() + "/checkExpireCookie", jsonReq.toString(), true);

        if (jSONObject.get("result").toString().equals("true")) {
            return true; //has expire
        } else {
            return false; //not expire
        }
    }

    public Cookie setCookie(String oldCookie) {
        JSONObject jsonReq = new JSONObject();
        jsonReq.put("sessId", oldCookie);
        jsonReq.put("sessionTtl", "86400");
        JSONObject jObj = this.utilities.curlPost(this.entityConfig.getUrlSession() + "/setSession", jsonReq.toString(), true);
        //CookieSetting(version, name, value);
        Cookie cS = new Cookie("mamenCookie", jObj.get("cookie").toString());
        cS.setMaxAge(Integer.parseInt(jObj.get("maxAge").toString()));
        cS.setPath("/");
        return cS;
    }

    public Cookie renewCookie(String oldCookie) {
        JSONObject jsonReq = new JSONObject();
        jsonReq.put("sessId", oldCookie);
        jsonReq.put("sessionTtl", "86400");
        JSONObject jObj = this.utilities.curlPost(this.entityConfig.getUrlSession() + "/renewSession", jsonReq.toString(), true);
        //CookieSetting(version, name, value);
        Cookie cS = new Cookie("mamenCookie", jObj.get("cookie").toString());
        cS.setMaxAge(Integer.parseInt(jObj.get("maxAge").toString()));
        cS.setPath("/");
        return cS;
    }

    public JSONObject getDataCookie(String cookie) {
        JSONObject jsonReq = new JSONObject();
        jsonReq.put("sessId", cookie);

        JSONObject jObj = this.utilities.curlPost(this.entityConfig.getUrlSession() + "/getSession", jsonReq.toString(), true);
        return jObj;
    }

    public void addDataCookie(String cookie, String key, String value) {
        JSONObject jsonReq = new JSONObject();
        jsonReq.put("sessId", cookie);
        jsonReq.put("key", key);
        jsonReq.put("value", value);

        this.utilities.curlPost(this.entityConfig.getUrlSession() + "/addDataSession", jsonReq.toString(), true);
    }

    public void removeCookie(final String cookie) {
        JSONObject jsonReq = new JSONObject();
        jsonReq.put("sessId", cookie);
        this.utilities.curlPost(this.entityConfig.getUrlSession() + "/delDataSession", jsonReq.toString(), true);
    }

}
