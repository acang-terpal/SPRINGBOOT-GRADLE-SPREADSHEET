package controller;

import configuration.ControllerEncDec;
import configuration.ControllerLog;
import configuration.DependencyConfig;
import configuration.FreeMarkerConfig;
import entity.EntityConfig;
import entity.EntityHtb;
import freemarker.template.Template;
import helper.ConstantMessage;
import helper.CookieHelperDb;
import helper.Helper;
import helper.Utilities;
import implement.ImpleSpreadSheetHtb;
import interfc.InterfcSpreadSheetHtb;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import net.sf.ehcache.Cache;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.util.WebUtils;
import service.SpreadSheetHtbService;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component("ControllerSpreadSheetHtb")
@Scope("prototype")
public class ControllerSpreadSheetHtb {
    private final EntityConfig entityConfig;
    private final InterfcSpreadSheetHtb service;
    private final SpreadSheetHtbService serviceJpa;
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
    private final String uuid;

    @Autowired
    public ControllerSpreadSheetHtb(DependencyConfig dependencyConfig, FreeMarkerConfig freeMarkerCfg, Utilities utilities, Helper hlp, ImpleSpreadSheetHtb impleSpreadSheetHtb, CookieHelperDb cookieHelperDb, ControllerEncDec controllerEncDec, SpreadSheetHtbService SpreadSheetHtbService) {
        this.controllerLog = dependencyConfig.getControllerLog();
        this.entityConfig = dependencyConfig.getEntityConfig();
        this.freeMarkerCfg = freeMarkerCfg;
        this.cacheCookies = dependencyConfig.getCacheCookies();
        this.cacheMaster = dependencyConfig.getCacheMaster();
        this.utilities = utilities;
        this.hlp = hlp;
        this.cookieHelper = cookieHelperDb;
        this.encDec = controllerEncDec;
        this.service = impleSpreadSheetHtb;
        this.serviceJpa = SpreadSheetHtbService;
        this.uuid = UUID.randomUUID().toString(); // Assign a unique ID on creation
        //System.out.println("PrototypeBean instance created with UUID: " + uuid);
    }

    public String getUniqueId() {
        return uuid;
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

    public ResponseEntity<StreamingResponseBody> getHtb(HttpServletRequest request) {
        this.handleRequestProperties(request);
        //check cookie
        if (this.cookieHelper.checkExistCookie(this.cookie.getValue())) {
            try {
                HashMap objAccount = this.hlp.hashStringToHashMap(this.objCookie.get("objAccount").toString());
                //renew cookie
                this.cookie = this.cookieHelper.renewCookie(this.cookie.getValue());
                HttpHeaders headers = new HttpHeaders();
                headers.add("Set-Cookie", " SpreadSheetCookie=" + this.cookie.getValue() + "; Max-Age=" + this.cookie.getMaxAge() + "; Path=/; " + this.cookie.getPath() + "; " + this.cookie.getSecure());
                StringWriter sideBarSW = new StringWriter();
                StringWriter contentSW = new StringWriter();
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.TEXT_HTML)
                        .body(outputStream -> {
                            try (Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("webRootUrl", this.webRootUrl);
                                map.put("baseUrl", this.baseUrl);
                                map.put("dashBoardActive", "current_section");
                                map.put("boardAsOwnActive", ""); //act_item
                                map.put("boardAsMemberActive", "");
                                map.put("memberActive", "");

                                // ---------------------Render Sidebar langsung ke writer----------------------
                                Template sideBar = this.freeMarkerCfg.getObjectMamen().getTemplate("component" + File.separator + "sidebar" + ".html");
                                //sidebar
                                sideBar.process(map, sideBarSW);
                                map.put("sideBarObj", sideBarSW.toString());

                                //--------------------------------content----------------------------------
                                List<EntityHtb> htbTree = this.serviceJpa.findHtbTree(); // testing get parent and all children
                                htbTree.forEach(root -> {
                                    System.out.println("Kakek: " + root.getName());

                                    // Cek Anak (Tier 1)
                                    if (root.getChildrenHtbClass() != null) {
                                        root.getChildrenHtbClass().forEach(child -> {
                                            System.out.println("  |-- Anak: " + child.getName());

                                            // Cek Cucu (Tier 2)
                                            if (child.getChildrenHtbClass() != null) {
                                                child.getChildrenHtbClass().forEach(cucu -> {
                                                    System.out.println("      |-- Cucu: " + cucu.getName());
                                                });
                                            }
                                        });
                                    }
                                });
                                List<EntityHtb> listEntityRoot = this.serviceJpa.findAllParentRootPaginated(1);
                                EntityHtb objEntityRoot = listEntityRoot.get(0);
                                HashMap<String, Object> objRoot = (HashMap<String, Object>) this.utilities.convertObjectToMap(objEntityRoot);
                                objRoot.put("tabSize", "0px");

                                HashMap objDataRate = this.service.getRootRateDownload(objRoot);
                                objRoot.put("rate_download", objDataRate.get("rate_download").toString());
                                objDataRate = this.service.getRootRateUpload(objRoot);
                                objRoot.put("rate_upload", objDataRate.get("rate_upload").toString());

                                objDataRate = this.service.getRootTotalDownload(objRoot);
                                objRoot.put("total_download", objDataRate.get("total_download").toString());
                                objDataRate = this.service.getRootTotalUpload(objRoot);
                                objRoot.put("total_upload", objDataRate.get("total_upload").toString());

                                //init root data
                                List listData = new ArrayList();
                                listData.add(objRoot);

                                List<EntityHtb> listEntityChildren1 = this.serviceJpa.findAllChildrenPaginated(objRoot, 1);
                                for (int i = 0; i < listEntityChildren1.size(); i++) {
                                    //find all chilldren first tier
                                    EntityHtb objEntity1 = listEntityChildren1.get(i);
                                    HashMap<String, Object> objDataTier1 = (HashMap<String, Object>) this.utilities.convertObjectToMap(objEntity1);
                                    objDataTier1.put("tabSize", "20px");

                                    objDataRate = this.service.getChildrenRateDownload(objDataTier1);
                                    objDataTier1.put("rate_download", objDataRate.get("rate_download").toString());
                                    objDataRate = this.service.getChildrenRateUpload(objDataTier1);
                                    objDataTier1.put("rate_upload", objDataRate.get("rate_upload").toString());

                                    objDataRate = this.service.getChildrenTotalDownload(objDataTier1);
                                    objDataTier1.put("total_download", objDataRate.get("total_download").toString());
                                    objDataRate = this.service.getChildrenTotalUpload(objDataTier1);
                                    objDataTier1.put("total_upload", objDataRate.get("total_upload").toString());

                                    listData.add(objDataTier1);

                                    //find all chilldren second tier
                                    //List listDataTier2 = this.service.getChildren(objDataTier1);
                                    List<EntityHtb> listEntityChildren2 = this.serviceJpa.findAllChildrenPaginated(objDataTier1, 1);
                                    for (int j = 0; j < listEntityChildren2.size(); j++) {
                                        EntityHtb objEntity2 = listEntityChildren2.get(j);
                                        HashMap<String, Object> objDataTier2 = (HashMap<String, Object>) this.utilities.convertObjectToMap(objEntity2);
                                        objDataTier2.put("tabSize", "40px");

                                        objDataRate = this.service.getChildrenRateDownload(objDataTier2);
                                        objDataTier2.put("rate_download", objDataRate.get("rate_download").toString());
                                        objDataRate = this.service.getChildrenRateUpload(objDataTier2);
                                        objDataTier2.put("rate_upload", objDataRate.get("rate_upload").toString());

                                        objDataRate = this.service.getChildrenTotalDownload(objDataTier2);
                                        objDataTier2.put("total_download", objDataRate.get("total_download").toString());
                                        objDataRate = this.service.getChildrenTotalUpload(objDataTier2);
                                        objDataTier2.put("total_upload", objDataRate.get("total_upload").toString());

                                        listData.add(objDataTier2);

                                        //find all chilldren third tier
                                        //List listDataTier3 = this.service.getChildren(objDataTier2);
                                        List<EntityHtb> listEntityChildren3 = this.serviceJpa.findAllChildrenPaginated(objDataTier2, 1);
                                        for (int k = 0; k < listEntityChildren3.size(); k++) {
                                            EntityHtb objEntity3 = listEntityChildren3.get(k);
                                            HashMap<String, Object> objDataTier3 = (HashMap<String, Object>) this.utilities.convertObjectToMap(objEntity3);
                                            //HashMap objDataTier3 = (HashMap) listDataTier3.get(k);
                                            objDataTier3.put("tabSize", "60px");

                                            objDataRate = this.service.getChildrenRateDownload(objDataTier3);
                                            objDataTier3.put("rate_download", objDataRate.get("rate_download").toString());
                                            objDataRate = this.service.getChildrenRateUpload(objDataTier3);
                                            objDataTier3.put("rate_upload", objDataRate.get("rate_upload").toString());

                                            objDataRate = this.service.getChildrenTotalDownload(objDataTier3);
                                            objDataTier3.put("total_download", objDataRate.get("total_download").toString());
                                            objDataRate = this.service.getChildrenTotalUpload(objDataTier3);
                                            objDataTier3.put("total_upload", objDataRate.get("total_upload").toString());

                                            listData.add(objDataTier3);
                                        }
                                    }
                                }
                                //System.out.println(jArrData);
                                map.put("listData", listData);
                                Template pageContent = this.freeMarkerCfg.getObjectMamen().getTemplate("component" + File.separator + view + "_page_content" + ".html");
                                pageContent.process(map, contentSW);
                                map.put("pageContentObj", contentSW.toString());

                                // ------------------------------Render Template Utama LANGSUNG ke Browser--------------------------------
                                Template template = this.freeMarkerCfg.getObjectMamen().getTemplate(view + ".html");
                                template.process(map, writer); // <--- KUNCI IRIT RAM: Tulis langsung ke selang (writer)

                                writer.flush();
                                writer.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    sideBarSW.flush();
                                    sideBarSW.close();
                                    contentSW.flush();
                                    contentSW.close();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            response = this.hlp.redirectToLogin(request, objRequestProperties);
        }
        return this.response;
    }

    public ResponseEntity<StreamingResponseBody> getRate(HttpServletRequest request) {
        this.handleRequestProperties(request);
        //check cookie
        if (this.cookieHelper.checkExistCookie(this.cookie.getValue())) {
            try {

                //content
                List<EntityHtb> listEntityRoot = this.serviceJpa.findAllParentRootPaginated(1);
                EntityHtb objEntityRoot = listEntityRoot.get(0);
                HashMap<String, Object> objRoot = (HashMap<String, Object>) this.utilities.convertObjectToMap(objEntityRoot);
                objRoot.put("tabSize", "0px");

                HashMap objDataRate = this.service.getRootRateDownload(objRoot);
                objRoot.put("rate_download", objDataRate.get("rate_download").toString());
                objDataRate = this.service.getRootRateUpload(objRoot);
                objRoot.put("rate_upload", objDataRate.get("rate_upload").toString());

                objDataRate = this.service.getRootTotalDownload(objRoot);
                objRoot.put("total_download", objDataRate.get("total_download").toString());
                objDataRate = this.service.getRootTotalUpload(objRoot);
                objRoot.put("total_upload", objDataRate.get("total_upload").toString());

                //init root data
                List listData = new ArrayList();
                listData.add(objRoot);

                List<EntityHtb> listEntityChildren1 = this.serviceJpa.findAllChildrenPaginated(objRoot, 1);
                for (int i = 0; i < listEntityChildren1.size(); i++) {
                    //find all chilldren first tier
                    EntityHtb objEntity1 = listEntityChildren1.get(i);
                    HashMap<String, Object> objDataTier1 = (HashMap<String, Object>) this.utilities.convertObjectToMap(objEntity1);
                    objDataTier1.put("tabSize", "20px");

                    objDataRate = this.service.getChildrenRateDownload(objDataTier1);
                    objDataTier1.put("rate_download", objDataRate.get("rate_download").toString());
                    objDataRate = this.service.getChildrenRateUpload(objDataTier1);
                    objDataTier1.put("rate_upload", objDataRate.get("rate_upload").toString());

                    objDataRate = this.service.getChildrenTotalDownload(objDataTier1);
                    objDataTier1.put("total_download", objDataRate.get("total_download").toString());
                    objDataRate = this.service.getChildrenTotalUpload(objDataTier1);
                    objDataTier1.put("total_upload", objDataRate.get("total_upload").toString());

                    listData.add(objDataTier1);

                    //find all chilldren second tier
                    //List listDataTier2 = this.service.getChildren(objDataTier1);
                    List<EntityHtb> listEntityChildren2 = this.serviceJpa.findAllChildrenPaginated(objDataTier1, 1);
                    for (int j = 0; j < listEntityChildren2.size(); j++) {
                        EntityHtb objEntity2 = listEntityChildren2.get(j);
                        HashMap<String, Object> objDataTier2 = (HashMap<String, Object>) this.utilities.convertObjectToMap(objEntity2);
                        objDataTier2.put("tabSize", "40px");

                        objDataRate = this.service.getChildrenRateDownload(objDataTier2);
                        objDataTier2.put("rate_download", objDataRate.get("rate_download").toString());
                        objDataRate = this.service.getChildrenRateUpload(objDataTier2);
                        objDataTier2.put("rate_upload", objDataRate.get("rate_upload").toString());

                        objDataRate = this.service.getChildrenTotalDownload(objDataTier2);
                        objDataTier2.put("total_download", objDataRate.get("total_download").toString());
                        objDataRate = this.service.getChildrenTotalUpload(objDataTier2);
                        objDataTier2.put("total_upload", objDataRate.get("total_upload").toString());

                        listData.add(objDataTier2);

                        //find all chilldren third tier
//                        List listDataTier3 = this.service.getChildren(objDataTier2);
                        List<EntityHtb> listEntityChildren3 = this.serviceJpa.findAllChildrenPaginated(objDataTier2, 1);
                        for (int k = 0; k < listEntityChildren3.size(); k++) {
                            EntityHtb objEntity3 = listEntityChildren3.get(k);
                            HashMap<String, Object> objDataTier3 = (HashMap<String, Object>) this.utilities.convertObjectToMap(objEntity3);
//                            HashMap objDataTier3 = (HashMap) listDataTier3.get(k);
                            objDataTier3.put("tabSize", "60px");

                            objDataRate = this.service.getChildrenRateDownload(objDataTier3);
                            objDataTier3.put("rate_download", objDataRate.get("rate_download").toString());
                            objDataRate = this.service.getChildrenRateUpload(objDataTier3);
                            objDataTier3.put("rate_upload", objDataRate.get("rate_upload").toString());

                            objDataRate = this.service.getChildrenTotalDownload(objDataTier3);
                            objDataTier3.put("total_download", objDataRate.get("total_download").toString());
                            objDataRate = this.service.getChildrenTotalUpload(objDataTier3);
                            objDataTier3.put("total_upload", objDataRate.get("total_upload").toString());

                            listData.add(objDataTier3);
                        }
                    }
                }
                JSONObject jObjResponse = ConstantMessage.Pesan.getSuccess.pesan();
                JSONArray jArrData = new JSONArray(listData);
                jObjResponse.put("data", jArrData);

                //renew cookie
                this.cookie = this.cookieHelper.renewCookie(this.cookie.getValue());
                HttpHeaders headers = new HttpHeaders();
                headers.add("Set-Cookie", " SpreadSheetCookie=" + this.cookie.getValue() + "; Max-Age=" + this.cookie.getMaxAge() + "; Path=/; " + this.cookie.getPath() + "; " + this.cookie.getSecure());
                StreamingResponseBody responseBody = outputStream -> {
                    String data = jObjResponse.toString();
                    outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                };
                this.response = ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.TEXT_HTML)
                        .body(responseBody);
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
//        System.out.println(Thread.currentThread().toString()); // check thread is virtual thread or os thread
        return this.response;
    }
}
