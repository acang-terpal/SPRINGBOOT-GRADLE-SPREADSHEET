package controller;

import configuration.ControllerEncDec;
import configuration.ControllerLog;
import configuration.DependencyConfig;
import configuration.FreeMarkerConfig;
import entity.EntityConfig;
import freemarker.template.Template;
import helper.*;
import implement.ImpleSpreadSheetLogin;
import interfc.InterfcSpreadSheetLogin;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import net.sf.ehcache.Cache;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.util.WebUtils;
import service.postgres.SpreadSheetAccountService;

import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hasan
 */

@Component("ControllerSpreadSheetLogin")
@Scope("prototype")
public class ControllerSpreadSheetLogin {

    private final EntityConfig entityConfig;
    private final InterfcSpreadSheetLogin service;
    private final SpreadSheetAccountService SpreadSheetAccountService;
    private final Helper hlp;
    private final Utilities utilities;
    private final ControllerLog controllerLog;
    private final ControllerEncDec encDec;
    private final CookieHelperDb cookieHelper;
    private final FreeMarkerConfig freeMarkerCfg;
    private final Cache cacheCookies;
    private final Cache cacheMaster;
    private final PasswordEncoder passwordEncoder;
    private ResponseEntity<StreamingResponseBody> response;
    private String fullUri, fullUrl, query, webroot, controller, methode, view, webRootUrl, baseUrl, hostPort;
    private HashMap objParams;
    private Cookie cookie;
    private HashMap objRequestProperties;

    @Autowired
    public ControllerSpreadSheetLogin(DependencyConfig dependencyConfig, FreeMarkerConfig freeMarkerCfg, Utilities utilities, Helper hlp, ImpleSpreadSheetLogin impleSpreadSheetLogin, CookieHelperDb cookieHelperDb, ControllerEncDec controllerEncDec, SpreadSheetAccountService SpreadSheetAccountService, PasswordEncoder passwordEncoder) {
        this.controllerLog = dependencyConfig.getControllerLog();
        this.entityConfig = dependencyConfig.getEntityConfig();
        this.freeMarkerCfg = freeMarkerCfg;
        this.cacheCookies = dependencyConfig.getCacheCookies();
        this.cacheMaster = dependencyConfig.getCacheMaster();
        this.utilities = utilities;
        this.hlp = hlp;
        this.cookieHelper = cookieHelperDb;
        this.encDec = controllerEncDec;
        this.service = impleSpreadSheetLogin;
        this.SpreadSheetAccountService = SpreadSheetAccountService;
        this.passwordEncoder = passwordEncoder;
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
        this.hostPort = objRequestProperties.get("hostPort").toString();
    }

    public ResponseEntity<StreamingResponseBody> getLogin(HttpServletRequest request) {
        this.handleRequestProperties(request);

        StringWriter templateStrObj = new StringWriter();
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                map.put("_csrf", csrfToken);
            } else {
                map.put("_csrf", request.getAttribute("_csrf"));
            }
            //put together on view template
            map.put("webRootUrl", this.webRootUrl);
            map.put("baseUrl", this.baseUrl);
            map.put("verifyEmail", "no");
            Template template = this.freeMarkerCfg.getObjectMamen().getTemplate(view + ".html");
            template.process(map, templateStrObj);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", String.valueOf(MediaType.TEXT_HTML));
            //this.response = ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(templateStrObj.toString().getBytes(StandardCharsets.UTF_8));
            StreamingResponseBody responseBody = outputStream -> {
                String data = templateStrObj.toString();
                outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            };
            this.response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                templateStrObj.close();
                templateStrObj.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return response;
    }

    public ResponseEntity<StreamingResponseBody> doLogin(HttpServletRequest request) {
        this.handleRequestProperties(request);
        JSONObject jObjResponse = new JSONObject();
        jObjResponse.put("rc", "01");
        jObjResponse.put("message", "Failed");
        try {
            //System.out.println("jObjParam : " + objParams.toString());
            //this is query update board
            String passWord = this.utilities.getSHA256(objParams.get("login_password").toString());
            objParams.put("account_password", passWord);
            List listAccount = this.SpreadSheetAccountService.getAccount(objParams);
            //System.out.println("jObjBoard : " + jObjBoard.toString());
            if (!listAccount.isEmpty()) {
                HashMap objAccount = (HashMap) listAccount.get(0);
//                System.out.println(objAccount.toString());
                if (!passwordEncoder.matches(objParams.get("login_password").toString(), objAccount.get("password").toString())) {
                    jObjResponse.put("rc", "06");
                    jObjResponse.put("message", "Failed, account not yet active or password not match!");
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Type", String.valueOf(MediaType.TEXT_HTML));
                    JSONObject finalJObjResponse1 = jObjResponse;
                    StreamingResponseBody responseBody = outputStream -> {
                        String data = finalJObjResponse1.toString();
                        outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                    };
                    this.response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
                } else {
                    jObjResponse = ConstantMessage.Pesan.getSuccess.pesan();
                    HttpHeaders headers = new HttpHeaders();
                    Cookie cookie = this.cookieHelper.setCookie("");
                    this.cookieHelper.addDataCookie(cookie.getValue(), "objAccount", objAccount.toString());

                    headers.add("Set-Cookie", "SpreadSheetCookie=" + cookie.getValue() + "; Max-Age=" + cookie.getMaxAge() + "; Path=/; " + cookie.getPath() + "; " + cookie.getSecure());
                    headers.add("Content-Type", String.valueOf(MediaType.TEXT_HTML));
//                response = ResponseEntity.ok().headers(headers).contentType(MediaType.TEXT_HTML).body(jObjResponse.toString().getBytes(StandardCharsets.UTF_8));
                    JSONObject finalJObjResponse = jObjResponse;
                    StreamingResponseBody responseBody = outputStream -> {
                        String data = finalJObjResponse.toString();
                        outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                    };
                    this.response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
                }
            } else {
                jObjResponse.put("rc", "06");
                jObjResponse.put("message", "Failed, account not yet active or password not match!");
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", String.valueOf(MediaType.TEXT_HTML));
                JSONObject finalJObjResponse1 = jObjResponse;
                StreamingResponseBody responseBody = outputStream -> {
                    String data = finalJObjResponse1.toString();
                    outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                };
                this.response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return this.response;
    }

    public ResponseEntity<StreamingResponseBody> doRegister(HttpServletRequest request) {
        final String data = "Internal Error";
        byte[] dataOut = data.getBytes(StandardCharsets.UTF_8);
        ResponseEntity<StreamingResponseBody> response = new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        this.handleRequestProperties(request);
        JSONObject jObjResponse = new JSONObject();
        jObjResponse.put("rc", "01");
        jObjResponse.put("message", "Failed");
        try {
            //System.out.println("jObjParam : " + objParams.toString());
            //---------------------------------insert user
            String passWord = this.utilities.getSHA256(objParams.get("account_password").toString());
            objParams.put("account_password", passWord);
            List listAcount = this.service.getAccountRegister(objParams);
            if (listAcount.size() > 0) {
                jObjResponse = ConstantMessage.Pesan.getAccountAlreadyRegister.pesan();
                HttpHeaders headers = new HttpHeaders();
                JSONObject finalJObjResponse = jObjResponse;
                StreamingResponseBody responseBody = outputStream -> {
                    String datas = finalJObjResponse.toString();
                    outputStream.write(datas.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                };
                this.response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
                return response;
            }
//            JSONObject objAccountTarget = this.utilities.getAccountByEmail(objParams.get("account_email").toString());
//            if (objAccountTarget.containsKey("account_id")) {
//                String jStrResp = ConstantMessage.Pesan.getAccountAlreadyRegister.pesan().toString();
//                jObjResponse = (JSONObject) new JSONParser().parse(jStrResp);
//                HttpHeaders headers = new HttpHeaders();
//                response = new ResponseEntity<>(jObjResponse.toString().getBytes(StandardCharsets.UTF_8), headers, HttpStatus.OK);
//                return response;
//            }
            HashMap objAccount = this.service.insertAccount(objParams);
            //--------------------------------insert ms_group
            String groupId = this.hlp.createID();
            List listGroupId = new ArrayList();
            while (listGroupId.size() > 0) {
                groupId = this.hlp.createID();
                listGroupId = this.service.getGroup(groupId);
            }
            HashMap<String, Object> objParam = new HashMap<>();
            objParam.put("group_id", groupId);
            objParam.put("account_id", objAccount.get("account_id"));
            objParam.put("level_id", "1");
            HashMap objGroup = this.service.insertGroup(objParam);
            //System.out.println("jObjBoard : " + jObjBoard.toString());
            if (objGroup.size() > 0) {
                //create session
                Cookie cookie = this.cookieHelper.setCookie("");
                this.cookieHelper.addDataCookie(cookie.getValue(), "objAccount", objAccount.toString());
                //send email verify
                String redirectUrl = this.baseUrl.replace(this.hostPort, "20001");
                if (baseUrl.toLowerCase().contains("http://")) {
                    redirectUrl = redirectUrl.replace("http://", "https://");
                }
                URI landingPage = new URI(redirectUrl + "/mamen/ControllerMamenLogin/getVerifyEmail/page_verify_email?key=" + cookie.getValue());
                objAccount.put("link", landingPage);
                objAccount.put("title", "Mamen New User");
                objAccount.put("footer", "Click link to verify your account");
                new SendEmail().send(objAccount, this.utilities, this.controllerLog);

                //create openfire user
                // Shared secret key
                //AuthenticationToken authenticationToken = new AuthenticationToken("t7bFRbse9VKDf1EB");
                //RestApiClient restApiClient = new RestApiClient("https://openfire.ngadi-ngadi.my.id", 9091, authenticationToken, SupportedMediaType.JSON);
                //UserEntity userEntity = new UserEntity(objAccount.get("account_name").toString() + "_" + objAccount.get("account_id").toString(), objAccount.get("account_name").toString(), objAccount.get("account_email").toString(), objAccount.get("account_password").toString());
                //Response responseOpenfire = restApiClient.createUser(userEntity);
                //System.out.println("responseOpenfire : " + responseOpenfire.getStatus());

                //success response
                jObjResponse = ConstantMessage.Pesan.getSuccess.pesan();
                jObjResponse.put("redirectUrl", "Success");
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", String.valueOf(MediaType.TEXT_HTML));
                JSONObject finalJObjResponse1 = jObjResponse;
                StreamingResponseBody responseBody = outputStream -> {
                    String datas = finalJObjResponse1.toString();
                    outputStream.write(datas.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                };
                this.response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
                return response;
            }
            JSONObject finalJObjResponse1 = jObjResponse;
            StreamingResponseBody responseBody = outputStream -> {
                String datas = finalJObjResponse1.toString();
                outputStream.write(datas.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            };
            this.response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return response;
    }

    public ResponseEntity<StreamingResponseBody> getVerifyEmail(HttpServletRequest request) {
        this.handleRequestProperties(request);
        StringWriter templateStrObj = new StringWriter();
        try {
            //System.out.println("jObjParam : " + objParams.toString());
            //---------------------------------insert user
            if (this.cookieHelper.checkExistCookie(objParams.get("key").toString())) {
                HashMap objCookie = this.cookieHelper.getDataCookie(objParams.get("key").toString());
                HashMap objAccount = this.hlp.hashStringToHashMap(objCookie.get("objAccount").toString());
                //set active account
                objAccount = this.service.updateAccountVerify(objAccount);
                Map<String, Object> map = new HashMap<String, Object>();
                //put together on view template
                map.put("webRootUrl", this.webRootUrl);
                Template template = this.freeMarkerCfg.getObjectMamen().getTemplate(view + ".html");
                template.process(map, templateStrObj);
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", String.valueOf(MediaType.TEXT_HTML));
                StreamingResponseBody responseBody = outputStream -> {
                    String data = templateStrObj.toString();
                    outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                };
                this.response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
            } else {
                //page resend verify link or reset password
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return response;
    }

    public ResponseEntity<StreamingResponseBody> getResetPassword(HttpServletRequest request) {
        this.handleRequestProperties(request);
        JSONObject jObjResponse = new JSONObject();
        jObjResponse.put("rc", "01");
        jObjResponse.put("message", "Failed");
        try {
            //System.out.println("jObjParam : " + objParams.toString());
            //get account
            List listAcount = this.service.getAccountRegister(objParams);
            //check email
            if (listAcount.size() <= 0) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", String.valueOf(MediaType.TEXT_HTML));
                JSONObject finalJObjResponse = jObjResponse;
                StreamingResponseBody responseBody = outputStream -> {
                    String data = finalJObjResponse.toString();
                    outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                };
                this.response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
                return response;
            }
            JSONObject objAccount = (JSONObject) listAcount.get(0);
            //insert session
            Cookie cookie = this.cookieHelper.setCookie("");
            this.cookieHelper.addDataCookie(cookie.getValue(), "objAccount", objAccount.toString());
            //send email
            String redirectUrl = this.baseUrl.replace(this.hostPort, "20001");
            if (baseUrl.toLowerCase().contains("http://")) {
                redirectUrl = redirectUrl.replace("http://", "https://");
            }
            URI landingPage = new URI(redirectUrl + "/mamen/ControllerMamenLogin/getPageResetPassword/page_reset_password?key=" + cookie.getValue());
            HashMap<String, Object> objEmail = new HashMap<>();
            objEmail.put("link", landingPage);
            objEmail.put("title", "Mamen Reset Password");
            objEmail.put("footer", "Click link to change your account password");
            objEmail.put("account_name", objAccount.get("account_name"));
            objEmail.put("account_email", objAccount.get("account_email"));
            new SendEmail().send(objEmail, this.utilities, this.controllerLog);
            //success response
            jObjResponse = ConstantMessage.Pesan.getSuccess.pesan();
            URI loginPage = new URI(redirectUrl + "/mamen/ControllerMamenLogin/getLogin/login?owner=true");
            jObjResponse.put("redirectUrl", loginPage.toString());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", String.valueOf(MediaType.TEXT_HTML));
            JSONObject finalJObjResponse1 = jObjResponse;
            StreamingResponseBody responseBody = outputStream -> {
                String data = finalJObjResponse1.toString();
                outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            };
            this.response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return response;
    }

    public ResponseEntity<StreamingResponseBody> getPageResetPassword(HttpServletRequest request) {
        this.handleRequestProperties(request);
        StringWriter templateStrObj = new StringWriter();
        try {
            //System.out.println("jObjParam : " + objParams.toString());
            //---------------------------------insert user
            if (this.cookieHelper.checkExistCookie(objParams.get("key").toString())) {
                HashMap jObjCookie = this.cookieHelper.getDataCookie(objParams.get("key").toString());
                HashMap objAccount = this.hlp.hashStringToHashMap(jObjCookie.get("objAccount").toString());

                Map<String, Object> map = new HashMap<String, Object>();
                //put together on view template
                map.put("webRootUrl", this.webRootUrl);
                Template template = this.freeMarkerCfg.getObjectMamen().getTemplate(view + ".html");
                template.process(map, templateStrObj);
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", String.valueOf(MediaType.TEXT_HTML));
                StreamingResponseBody responseBody = outputStream -> {
                    String data = templateStrObj.toString();
                    outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                };
                this.response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
            } else {
                //page resend verify link or reset password
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return response;
    }

    public ResponseEntity<StreamingResponseBody> doReset(HttpServletRequest request) {
        this.handleRequestProperties(request);
        JSONObject jObjResponse = new JSONObject();
        jObjResponse.put("rc", "01");
        jObjResponse.put("message", "Failed");
        try {
            //System.out.println("jObjParam : " + objParams.toString());
            if (this.cookieHelper.checkExistCookie(objParams.get("key").toString())) {
                HashMap objCookie = this.cookieHelper.getDataCookie(objParams.get("key").toString());
                HashMap objAccount = this.hlp.hashStringToHashMap(objCookie.get("objAccount").toString());
                //validate field password and field password

                //reset password
                String passWord = this.utilities.getSHA256(objParams.get("reset_password").toString());
                objAccount.put("account_password", passWord);
                objAccount = this.service.updateAccountPassword(objAccount);
                if(objAccount.containsKey("account_id")) {
                    //success response
                    String redirectUrl = this.baseUrl.replace(this.hostPort, "20001");
                    if (baseUrl.toLowerCase().contains("http://")) {
                        redirectUrl = redirectUrl.replace("http://", "https://");
                    }
                    jObjResponse = ConstantMessage.Pesan.getSuccess.pesan();
                    URI loginPage = new URI(redirectUrl + "/mamen/ControllerMamenLogin/getLogin/login?owner=true");
                    jObjResponse.put("redirectUrl", loginPage.toString());
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Content-Type", String.valueOf(MediaType.TEXT_HTML));
                    JSONObject finalJObjResponse = jObjResponse;
                    StreamingResponseBody responseBody = outputStream -> {
                        String data = finalJObjResponse.toString();
                        outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                    };
                    this.response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return response;
    }

    public ResponseEntity<StreamingResponseBody> getVerifyLink(HttpServletRequest request) {
        this.handleRequestProperties(request);
        JSONObject jObjResponse = new JSONObject();
        jObjResponse.put("rc", "01");
        jObjResponse.put("message", "Failed");
        try {
            //get account
            List listAcount = this.service.getAccountRegister(objParams);
            //check email
            if (listAcount.size() <= 0) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", String.valueOf(MediaType.TEXT_HTML));
                JSONObject finalJObjResponse = jObjResponse;
                StreamingResponseBody responseBody = outputStream -> {
                    String data = finalJObjResponse.toString();
                    outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                };
                this.response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
                return response;
            }
            HashMap objAccount = (HashMap) listAcount.get(0);
            if(objAccount.get("account_verify").toString().equals("t") || objAccount.get("account_verify").toString().equals("true")){
                jObjResponse = ConstantMessage.Pesan.getAccountAlreadyActive.pesan();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", String.valueOf(MediaType.TEXT_HTML));
                JSONObject finalJObjResponse1 = jObjResponse;
                StreamingResponseBody responseBody = outputStream -> {
                    String data = finalJObjResponse1.toString();
                    outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                };
                this.response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
                return response;
            }
            //create session
            Cookie cookie = this.cookieHelper.setCookie("");
            this.cookieHelper.addDataCookie(cookie.getValue(), "objAccount", objAccount.toString());
            //send email verify
            String redirectUrl = this.baseUrl.replace(this.hostPort, "20001");
            if (baseUrl.toLowerCase().contains("http://")) {
                redirectUrl = redirectUrl.replace("http://", "https://");
            }
            URI landingPage = new URI(redirectUrl + "/mamen/ControllerMamenLogin/getVerifyEmail/page_verify_email?key=" + cookie.getValue());
            objAccount.put("link", landingPage);
            objAccount.put("title", "Mamen New User");
            objAccount.put("footer", "Click link to verify your account");
            new SendEmail().send(objAccount, this.utilities, this.controllerLog);

            //success response
            jObjResponse = ConstantMessage.Pesan.getSuccess.pesan();
            jObjResponse.put("redirectUrl", "Success");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", String.valueOf(MediaType.TEXT_HTML));
            JSONObject finalJObjResponse2 = jObjResponse;
            StreamingResponseBody responseBody = outputStream -> {
                String data = finalJObjResponse2.toString();
                outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            };
            this.response = new ResponseEntity<>(responseBody, headers, HttpStatus.OK);
            return response;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return response;
    }

    public ResponseEntity<StreamingResponseBody> logout(HttpServletRequest request) {
        this.handleRequestProperties(request);
        JSONObject jObjResponse = new JSONObject();
        jObjResponse.put("rc", "01");
        jObjResponse.put("message", "Failed");
        try {
            //System.out.println("jObjParam : " + objParams.toString());
            this.cookie = WebUtils.getCookie(request, " SpreadSheetCookie");
            if (this.cookieHelper.checkExistCookie(this.cookie.getValue())) {
                this.cookieHelper.removeCookie(this.cookie.getValue());
                response = this.hlp.redirectToLogin(request, objRequestProperties);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return response;
    }
}
