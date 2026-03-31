/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import configuration.ControllerLog;
import configuration.ControllerSessionDb;
import configuration.DependencyConfig;
import entity.EntityConfig;
import jakarta.servlet.http.Cookie;
import net.sf.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author Hasan
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CookieHelperDb {
    private final EntityConfig entityConfig;
    private final Helper helper;
    private final Utilities utilities;
    private final ControllerLog controllerLog;
    private final Cache cacheMaster;
    private final Cache cacheCookies;

    @Autowired
    private ApplicationContext context;

    @Autowired
    public CookieHelperDb(DependencyConfig dependencyConfig, Utilities utilities, Helper helper) {
        this.entityConfig = dependencyConfig.getEntityConfig();
        this.cacheMaster = dependencyConfig.getCacheMaster();
        this.cacheCookies = dependencyConfig.getCacheCookies();
        this.controllerLog = dependencyConfig.getControllerLog();
        this.utilities = utilities;
        this.helper = helper;
    }

    public boolean checkExistCookie(String cookie) {
        HashMap<String, String> objReq = new HashMap<>();
        objReq.put("sessId", cookie);
        ControllerSessionDb controllerSessionDb = (ControllerSessionDb) context.getBean("ControllerSessionDb");
        HashMap jsonResp = controllerSessionDb.checkExistCookie(objReq);
        if(jsonResp.get("result").toString().equals("true")){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkExpireCookie(String cookie) {
        HashMap<String, String> objReq = new HashMap<>();
        objReq.put("sessId", cookie);
        ControllerSessionDb controllerSessionDb = (ControllerSessionDb) context.getBean("ControllerSessionDb");
        HashMap jSONObject = controllerSessionDb.checkExpireCookie(objReq);
        if (jSONObject.get("result").toString().equals("true")) {
            return true; //has expire
        } else {
            return false; //not expire
        }
    }

    public Cookie setCookie(String oldCookie) {
        HashMap<String, String> objReq = new HashMap<>();
        objReq.put("sessId", oldCookie);
        objReq.put("sessionTtl", this.entityConfig.getSessionTtl());
        ControllerSessionDb controllerSessionDb = (ControllerSessionDb) context.getBean("ControllerSessionDb");
        HashMap jObj = controllerSessionDb.setCookie(objReq);
        //CookieSetting(version, name, value);
        Cookie cS = new Cookie("SpreadSheetCookie", jObj.get("cookie").toString());
        cS.setMaxAge(Integer.parseInt(jObj.get("maxAge").toString()));
        cS.setPath("/");
        return cS;
    }

    public Cookie renewCookie(String oldCookie) {
        HashMap<String, String> objReq = new HashMap<>();
        objReq.put("sessId", oldCookie);
        objReq.put("sessionTtl", this.entityConfig.getSessionTtl());
        ControllerSessionDb controllerSessionDb = (ControllerSessionDb) context.getBean("ControllerSessionDb");
        HashMap jObj = controllerSessionDb.renewCookie(objReq);
        //CookieSetting(version, name, value);
        Cookie cS = new Cookie("SpreadSheetCookie", jObj.get("cookie").toString());
        cS.setMaxAge(Integer.parseInt(jObj.get("maxAge").toString()));
        cS.setPath("/");
        return cS;
    }

    public HashMap getDataCookie(String cookie) {
        HashMap<String, String> objReq = new HashMap<>();
        objReq.put("sessId", cookie);
        ControllerSessionDb controllerSessionDb = (ControllerSessionDb) context.getBean("ControllerSessionDb");
        HashMap hashObj = controllerSessionDb.getDataCookie(objReq);
        return hashObj;
    }

    public void addDataCookie(String cookie, String key, String value) {
        HashMap<String, String> objReq = new HashMap<>();
        objReq.put("sessId", cookie);
        objReq.put("key", key);
        objReq.put("value", value);
        ControllerSessionDb controllerSessionDb = (ControllerSessionDb) context.getBean("ControllerSessionDb");
        HashMap jObj = controllerSessionDb.addDataCookie(objReq);
    }

    public void removeCookie(final String cookie) {
        HashMap<String, String> objReq = new HashMap<>();
        objReq.put("sessId", cookie);
        ControllerSessionDb controllerSessionDb = (ControllerSessionDb) context.getBean("ControllerSessionDb");
        controllerSessionDb.removeCookie(objReq);
    }

}
