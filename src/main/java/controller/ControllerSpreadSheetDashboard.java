package controller;

import configuration.ControllerEncDec;
import configuration.ControllerLog;
import configuration.DependencyConfig;
import configuration.FreeMarkerConfig;
import entity.EntityConfig;
import freemarker.template.Template;
import helper.CookieHelperDb;
import helper.Helper;
import helper.Utilities;
import implement.ImpleSpreadSheetHtb;
import interfc.InterfcSpreadSheetHtb;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import net.sf.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.util.WebUtils;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hasan
 */
@Component("ControllerSpreadSheetDashboard")
@Scope("prototype")
public class ControllerSpreadSheetDashboard {

    private final EntityConfig entityConfig;
    private final InterfcSpreadSheetHtb service;
    private final Helper hlp;
    private final Utilities utilities;
    private final ControllerLog controllerLog;
    private final ControllerEncDec encDec;
    private final CookieHelperDb cookieHelper;
    private final FreeMarkerConfig freeMarkerCfg;
    private final Cache cacheCookies;
    private final Cache cacheMaster;
    private ResponseEntity<StreamingResponseBody> response;
    private String fullUri, fullUrl, query, webroot, controller, methode, view, webRootUrl, baseUrl;
    private HashMap objParams, objRequestProperties, objCookie;
    private Cookie cookie;

    @Autowired
    public ControllerSpreadSheetDashboard(DependencyConfig dependencyConfig, FreeMarkerConfig freeMarkerCfg, Utilities utilities, Helper helper, CookieHelperDb cookieHelperDb, ImpleSpreadSheetHtb impleSpreadSheetHtb, ControllerEncDec controllerEncDec) {
        this.controllerLog = dependencyConfig.getControllerLog();
        this.entityConfig = dependencyConfig.getEntityConfig();
        this.freeMarkerCfg = freeMarkerCfg;
        this.cacheCookies = dependencyConfig.getCacheCookies();
        this.cacheMaster = dependencyConfig.getCacheMaster();
        this.utilities = utilities;
        this.hlp = helper;
        this.cookieHelper = cookieHelperDb;
        this.encDec = controllerEncDec;
        this.service = impleSpreadSheetHtb;
    }

    public void handleRequestProperties(HttpServletRequest request) {
        this.objRequestProperties = this.hlp.handleRequestProperties(request);
        this.response = (ResponseEntity<StreamingResponseBody>) objRequestProperties.get("response");
        this.fullUri = objRequestProperties.get("fullUri").toString();
        this.fullUrl = objRequestProperties.get("fullUrl").toString();
        this.query = objRequestProperties.get("query").toString();
        this.objParams = (HashMap) objRequestProperties.get("objParams");
        this.webroot = objRequestProperties.get("webroot").toString();
        this.controller = objRequestProperties.get("controller").toString();
        this.methode = objRequestProperties.get("methode").toString();
        this.view = objRequestProperties.get("view").toString();
        this.webRootUrl = objRequestProperties.get("webRootUrl").toString();
        this.baseUrl = objRequestProperties.get("baseUrl").toString();
        this.cookie = WebUtils.getCookie(request, "SpreadSheetCookie");
        //System.out.println(this.cookie.getValue());
        if (cookie != null) {
            if (this.cookieHelper.checkExistCookie(cookie.getValue())) {
                this.objCookie = this.cookieHelper.getDataCookie(this.cookie.getValue());
            } else {
                this.cookie = new Cookie("SpreadSheetCookie", "null");
            }
        } else {
            this.cookie = new Cookie("SpreadSheetCookie", "null");
        }
    }

    public ResponseEntity<StreamingResponseBody> getDashboard(HttpServletRequest request) {
        this.handleRequestProperties(request);
        //check cookie
        //System.out.println(this.cookie.getValue());
        if (this.cookieHelper.checkExistCookie(this.cookie.getValue())) {
            try {
                HashMap objAccount = this.hlp.hashStringToHashMap(this.objCookie.get("objAccount").toString());
                //renew cookie
                this.cookie = this.cookieHelper.renewCookie(this.cookie.getValue());
                HttpHeaders headers = new HttpHeaders();
                headers.add("Set-Cookie", " SpreadSheetCookie=" + this.cookie.getValue() + "; Max-Age=" + this.cookie.getMaxAge() + "; Path=/; " + this.cookie.getPath() + "; " + this.cookie.getSecure());

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.TEXT_HTML)
                        .body(outputStream -> {
                            // Writer langsung bungkus outputStream
                            try (Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("webRootUrl", this.webRootUrl);
                                map.put("baseUrl", this.baseUrl);

                                // Render Sidebar langsung ke writer
                                Template sideBar = this.freeMarkerCfg.getObjectMamen().getTemplate("component" + File.separator + "sidebar" + ".html");
                                StringWriter sideBarSW = new StringWriter();
                                //sidebar
                                map.put("dashBoardActive", "current_section");
                                map.put("boardAsOwnActive", ""); //act_item
                                map.put("boardAsMemberActive", "");
                                map.put("memberActive", "");
                                sideBar.process(map, sideBarSW);
                                map.put("sideBarObj", sideBarSW.toString());

                                // Render Content langsung ke writer
                                Template pageContent =this.freeMarkerCfg.getObjectMamen().getTemplate("component" + File.separator + view + "_page_content" + ".html");
                                StringWriter contentSW = new StringWriter();
                                pageContent.process(map, contentSW);
                                map.put("pageContentObj", contentSW.toString());

                                // Render Template Utama LANGSUNG ke Browser
                                Template template = this.freeMarkerCfg.getObjectMamen().getTemplate(view + ".html");
                                template.process(map, writer); // <--- KUNCI IRIT RAM: Tulis langsung ke selang (writer)
                                writer.flush();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }


        } else {
            response = this.hlp.redirectToLogin(request, objRequestProperties);
        }
        return this.response;
    }
}
