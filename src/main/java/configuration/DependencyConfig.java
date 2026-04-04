package configuration;

import entity.EntityConfig;
import helper.CookieHelperExternal;
import main.Main;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.DiskStoreConfiguration;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import java.io.File;

@Configuration("dependencyConfig")
@Scope("singleton")
//@Scope("prototype") it will create new object every bean requested
@Order(1)
public class DependencyConfig {

    private EntityConfig entityConfig;
    private ControllerLog controllerLog;
    private Cache cache;
    private Cache cacheCookies;
    private Cache cacheMaster;

    @Autowired
    Environment env;

    public DependencyConfig() {
        //this is how to get cache
//        CacheManager cacheManager = CacheManager.getInstance();
//        this.cache = cacheManager.getCache(" SpreadSheetCacheApp");
//        Element config = this.cache.get("config");
//        this.entityConfig = (EntityConfig) config.getObjectValue();
//        this.cacheCookies = cacheManager.getCache(" SpreadSheetCacheAppCookies");
//        this.cacheMaster = cacheManager.getCache(" SpreadSheetCacheAppMaster");
        this.controllerLog = new ControllerLog();
        this.initCache();
    }

    public void initCache() {
        try {
            //--------------------------ehcache init--------------------------------------
            DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
            diskStoreConfiguration.setPath(Main.workDir + File.separator + "cache");
            // Already created a configuration object ...
            net.sf.ehcache.config.Configuration cacheConfiguration = new net.sf.ehcache.config.Configuration();
            cacheConfiguration.addDiskStore(diskStoreConfiguration);
            //Create a CacheManager using custom configuration
            CacheManager cacheManager = CacheManager.create(cacheConfiguration);
            //--------------------------ehcache save the config entity------------------------
            this.cache = new Cache(" SpreadSheetCacheApp", 1, false, true, 0, 0);
            cacheManager.addCache(this.cache);
            //--------------------------get cache and put new element-------------------------------------
            this.cache = cacheManager.getCache(" SpreadSheetCacheApp");
            this.cache.put(new Element("config", Main.conf));
            Element config = this.cache.get("config");
            this.entityConfig = (EntityConfig) config.getObjectValue();
            //-------------------------ehcache for cookies--------------------------
            //new Cache(workDir, maxElementsInMemory, overflow to disk, eternal, timeToLiveSeconds, timeToIdleSeconds)
            this.cacheCookies = new Cache(" SpreadSheetCacheAppCookies", 1000, true, false, 86400, 0);
            cacheManager.addCache(this.cacheCookies);

            //-------------------------ehcache for dataMaster--------------------------
            //new Cache(workDir, maxElementsInMemory, overflow to disk, eternal, timeToLiveSeconds, timeToIdleSeconds)
            this.cacheMaster = new Cache(" SpreadSheetCacheAppMaster", 1000, true, true, 0, 0);
            cacheManager.addCache(this.cacheMaster);
            //get all master data to save to cache
//        ControllerMasterDataCache controllerMasterDataCache = new ControllerMasterDataCache(main.Main.conf);
//        ArrayList<HashMap<String, Object>> rowMsParameter = controllerMasterDataCache.getMsParameter();
//        System.out.println("cache ms parameter lenght data : " + rowMsParameter.size());
            //--------------------------get cache of  SpreadSheetCacheAppMaster and put new element-------------------------
//        thisCache = cacheManager.getCache(" SpreadSheetCacheAppMaster");
//        thisCache.put(new Element("msParameter", rowMsParameter));
//        thisCache.put(new Element("msMitraCode", rowMsMitraCode));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean("cache")
    public Cache getCache() {
        return this.cache;
    }

    @Bean("entityConfig")
    public EntityConfig getEntityConfig() {
        return this.entityConfig;
    }

    @Bean("cacheCookies")
    public Cache getCacheCookies() {
        return this.cacheCookies;
    }

    @Bean("cacheMaster")
    public Cache getCacheMaster() {
        return this.cacheMaster;
    }

    @Bean("controllerLog")
    public ControllerLog getControllerLog() {
        return this.controllerLog;
    }

    @Bean("setCookie")
    @DependsOn({"cache","cacheCookies","cacheMaster","controllerLog","entityConfig"})
    @Lazy
    public synchronized boolean setCookie(CookieHelperExternal cookieHelper, String extRef) {
        cookieHelper.setCookie(extRef);
        return cookieHelper.checkExistCookie(extRef);
    }

    @Bean("checkExistAndSetCookie")
    @DependsOn({"cache","cacheCookies","cacheMaster","controllerLog","entityConfig"})
    @Lazy
    public synchronized boolean checkExistAndSetCookie(CookieHelperExternal cookieHelper, String extRef) {
        if (!cookieHelper.checkExistCookie(extRef)) {
            cookieHelper.setCookie(extRef);
            return false;
        } else {
            return true;
        }
    }

    @Bean("addDataCookie")
    @DependsOn({"cache","cacheCookies","cacheMaster","controllerLog","entityConfig"})
    @Lazy
    public synchronized boolean addDataCookie(CookieHelperExternal cookieHelper, String extRef, String modul) {
        if (cookieHelper.checkExistCookie(extRef)) {
            JSONObject jObjCookie = cookieHelper.getDataCookie(extRef);
            if (jObjCookie.containsKey(modul)) {
                if (jObjCookie.get(modul).equals("true")) {
                    return false;
                } else {
                    cookieHelper.addDataCookie(extRef, modul, "true");
                    return true;
                }
            } else {
                cookieHelper.addDataCookie(extRef, modul, "true");
                return true;
            }
        } else {
            return false;
        }
    }
}
