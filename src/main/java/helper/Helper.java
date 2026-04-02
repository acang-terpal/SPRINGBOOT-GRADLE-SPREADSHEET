/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import com.google.common.base.Throwables;
import configuration.ControllerLog;
import configuration.DependencyConfig;
import entity.EntityConfig;
import jakarta.servlet.http.HttpServletRequest;
import koneksi.DatabaseUtilitiesMysql;
import koneksi.DatabaseUtilitiesPgsqlCecenet;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.fileupload.FileItemStream;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.json.XML;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Hasan
 */
@Component("Helper")
@Scope("prototype")
public class Helper {
    private final Cache cacheMaster;
    private Cache cacheCookies;
    private final EntityConfig entityConfig;
    private Utilities utilities;
    private ControllerLog controllerLog;

    @Autowired
    public Helper(DependencyConfig dependencyConfig, Utilities utilities) {
        this.entityConfig = dependencyConfig.getEntityConfig();
        this.cacheMaster = dependencyConfig.getCacheMaster();
        this.cacheCookies = dependencyConfig.getCacheCookies();
        this.controllerLog = dependencyConfig.getControllerLog();
        this.utilities = utilities;
    }

//    public Helper(EntityConfig entityConfig, Cache cacheMaster, Cache cacheCookies, Utilities utilities, ControllerLog controllerLog) {
//        this.entityConfig = entityConfig;
//        this.cacheMaster = cacheMaster;
//        this.cacheCookies = cacheCookies;
//        this.utilities = utilities;
//        this.controllerLog = controllerLog;
//    }

    public boolean checkDuplicateExtRef(String extRef) {
        if (this.cacheCookies.isKeyInCache(extRef)) {
            Element elemCache = this.cacheCookies.get(extRef);
            if (elemCache != null) {
                if (this.cacheCookies.isExpired(elemCache)) {
                    cacheCookies.remove(extRef);
                    this.cacheCookies.put(new Element(extRef, extRef));
                    return false;
                } else {
                    return true;
                }
            } else {
                cacheCookies.remove(extRef);
                this.cacheCookies.put(new Element(extRef, extRef));
                return false;
            }
        } else {
            this.cacheCookies.put(new Element(extRef, extRef));
            return false;
        }

//        return false;
    }

    public synchronized String createID() {
        String Id = null;
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.reset();
            messageDigest.update(Long.toString(System.nanoTime()).getBytes(Charset.forName("UTF8")));
            final byte[] resultByte = messageDigest.digest();
            Id = new String(Hex.encodeHex(resultByte));
        } catch (NoSuchAlgorithmException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        }
        return Id;
    }

    public BufferedImage resizeImage(BufferedImage originalImage, int type, int IMG_WIDTH, int IMG_HEIGHT) {
        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();

        return resizedImage;
    }

    public BufferedImage resizeImageWithHint(BufferedImage originalImage, int type, int IMG_WIDTH, int IMG_HEIGHT) {

        BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        g.dispose();
        g.setComposite(AlphaComposite.Src);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        return resizedImage;
    }

    public String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            imageString = Base64.encodeBase64String(imageBytes);

            bos.close();
        } catch (IOException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        }
        return imageString;
    }

    public String encodeImage(byte[] imageByteArray) {
        return Base64.encodeBase64String(imageByteArray);
    }

    public ArrayList<HashMap<String, Object>> executeQuery(EntityConfig conf, String query, JSONObject jObj) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        ArrayList<HashMap<String, Object>> row = new ArrayList<>();
        try {
            DatabaseUtilitiesMysql databaseUtilitiesMysql = new DatabaseUtilitiesMysql();
            try {
                conn = databaseUtilitiesMysql.getConnection(conf);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
            }
            st = conn.prepareStatement(query);

            Object json = new JSONParser().parse(jObj.toString());
            JSONObject jsonObject = (JSONObject) json;
            for (int i = 1; i <= jsonObject.size(); i++) {
                JSONObject jsonobj = (JSONObject) jsonObject.get(String.valueOf(i));
                String getType = (String) jsonobj.get("type");
                String getVal = (String) jsonobj.get("val");
                if (getType.equals("string")) {
                    st.setString(i, getVal);
                } else if (getType.equals("timestamp")) {
                    st.setTimestamp(i, Timestamp.valueOf(getVal));
                } else if (getType.equals("int")) {
                    st.setInt(i, Integer.parseInt(getVal));
                }
            }
            rs = st.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int colCount = metaData.getColumnCount();
            row = new ArrayList<>();
            //convert resultset to arraylist
            while (rs.next()) {
                HashMap<String, Object> columns = new HashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    columns.put(metaData.getColumnLabel(i), rs.getObject(i).toString());
                }
                row.add(columns);
            }
        } catch (SQLException | ParseException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } finally {
            try {
                rs.close();
            } catch (Exception e) { /* ignored */ }
            try {
                st.close();
            } catch (Exception e) { /* ignored */ }
            try {
                conn.close();
            } catch (Exception e) { /* ignored */ }
        }
        return row;
    }

    public String executeUpdate(EntityConfig conf, String queryUpd, JSONObject jObjUpd) {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            DatabaseUtilitiesMysql databaseUtilitiesMysql = new DatabaseUtilitiesMysql();
            try {
                conn = databaseUtilitiesMysql.getConnection(conf);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
            }
            st = conn.prepareStatement(queryUpd);

            Object json = new JSONParser().parse(jObjUpd.toString());
            JSONObject jsonObject = (JSONObject) json;
            for (int i = 1; i <= jsonObject.size(); i++) {
                JSONObject jsonobj = (JSONObject) jsonObject.get(String.valueOf(i));
                String getType = (String) jsonobj.get("type");
                String getVal = (String) jsonobj.get("val");
                if (getType.equals("string")) {
                    st.setString(i, getVal);
                } else if (getType.equals("timestamp")) {
                    st.setTimestamp(i, Timestamp.valueOf(getVal));
                } else if (getType.equals("int")) {
                    st.setInt(i, Integer.parseInt(getVal));
                }
            }
            st.executeUpdate();
        } catch (SQLException | ParseException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } finally {
            try {
                rs.close();
            } catch (Exception e) { /* ignored */ }
            try {
                st.close();
            } catch (Exception e) { /* ignored */ }
            try {
                conn.close();
            } catch (Exception e) { /* ignored */ }
        }
        return null;
    }

    public void createFile(FileItemStream fi, String nameFile) {
        InputStream attachmentStream = null;
        ByteArrayOutputStream out = null;
        FileOutputStream fos = null;
        try {
            attachmentStream = fi.openStream();
            byte[] attachmentBytes;

            //cara 1
//                    try {
//                        attachmentBytes = ByteStreams.toByteArray(attachmentStream);
//                    } finally {
//                        attachmentStream.close();
//                    }
            //cara 2
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024]; // you can configure the buffer size
            int length;
            while ((length = attachmentStream.read(buffer)) != -1) {
                out.write(buffer, 0, length); //copy streams
            }
            attachmentBytes = out.toByteArray();

            fos = new FileOutputStream(entityConfig.getWorkDir() + "tmp" + File.separator + nameFile);
            fos.write(attachmentBytes);
        } catch (Exception ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } finally {
            try {
                if (attachmentStream != null) {
                    attachmentStream.close();
                }
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                String s = Throwables.getStackTraceAsString(e);
                controllerLog.logErrorWriter(s);
            }
        }
    }

    public String getParamMsParameter(String paramName) {
        DatabaseUtilitiesPgsqlCecenet databaseUtilitiesPgsql = new DatabaseUtilitiesPgsqlCecenet();
        Connection connPgsql = null;
        PreparedStatement stPgsql = null;
        ResultSet rsPgsql = null;
        String url = "";
        try {
            //get msparam
            connPgsql = databaseUtilitiesPgsql.getConnection(entityConfig);
            String query = "SELECT * FROM ms_parameter "
                    + "WHERE parameter_name = ?";
            stPgsql = connPgsql.prepareStatement(query);
            stPgsql.setString(1, paramName);
            rsPgsql = stPgsql.executeQuery();
            while (rsPgsql.next()) {
                url = rsPgsql.getString("parameter_value");
            }
        } catch (Exception ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } finally {
            try {
                if (rsPgsql != null) {
                    rsPgsql.close();
                }
                if (stPgsql != null) {
                    stPgsql.close();
                }
                if (connPgsql != null) {
                    connPgsql.close();
                }
            } catch (SQLException ex) {
                String s = Throwables.getStackTraceAsString(ex);
                controllerLog.logErrorWriter(s);
            }
        }
        return url;
    }

    public String requestToGwSocket(JSONObject payloadRequest, String paramName) {
        String paramValue = this.getParamMsParameter(paramName);
        String[] param = paramValue.split(":");
        InetSocketAddress socketAddress = new InetSocketAddress(param[0], Integer.parseInt(param[1]));
        final Socket socket = new Socket();
        final int tTimeout = 1000 * 120;
        final byte cEndMessageByte = -0x01;

        ControllerLog ctrlLog = new ControllerLog();
        ctrlLog.logStreamWriter("Upline -> Req : " + socketAddress.getAddress() + ":" + socketAddress.getPort()
                + " " + payloadRequest.toString());

        String tResponse = "";
        try {
            socket.setSoTimeout(tTimeout);
            socket.connect(socketAddress, tTimeout);

            ByteArrayOutputStream tRequestByteStream = new ByteArrayOutputStream();
            tRequestByteStream.write(payloadRequest.toString().getBytes());
            tRequestByteStream.write(cEndMessageByte);
            socket.getOutputStream().write(tRequestByteStream.toByteArray());

            byte tMessageByte = cEndMessageByte;
            StringBuffer sb = new StringBuffer();
            while ((tMessageByte = (byte) socket.getInputStream().read()) != cEndMessageByte) {
                sb.append((char) tMessageByte);
            }
            tResponse = sb.toString();
            ctrlLog.logStreamWriter("Upline -> Resp : " + tResponse);
        } catch (IOException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        }
        return tResponse;
    }

    public String getClientIpAddress(HttpServletRequest request) {
        for (String header : Constant.IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    public JSONObject enhanceRequest(JSONObject jsonReq) {
        String ipAddr = this.getClientIpAddress(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest());
        int port = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRemotePort();

        String downlineId = this.createID();
        jsonReq.put("downlineId", downlineId);
        jsonReq.put("ipAddr", ipAddr);
        jsonReq.put("port", port);
        jsonReq.put("browserAgent", "api internal");
        return jsonReq;
    }

    public org.json.JSONObject convertToJSONObject(String dataRequest) {
        org.json.JSONObject jsonReq = new org.json.JSONObject();
        try {
            jsonReq = new org.json.JSONObject(dataRequest);
        } catch (Exception ex) {
            try {
                SAXBuilder saxBuilder = new SAXBuilder();
                org.jdom2.Document doc = saxBuilder.build(new ByteArrayInputStream(dataRequest.getBytes("UTF-8")));
                jsonReq = XML.toJSONObject(new XMLOutputter().outputString(doc));
            } catch (Exception ex1) {
                //String s = Throwables.getStackTraceAsString(ex);
                //controllerLog.logErrorWriter(s);
            }
        }
        return jsonReq;
    }

    public JSONObject parsingRequest(String dataRequest) {
        JSONObject jsonReq = null;
        try {
            dataRequest = URLDecoder.decode(dataRequest, "UTF-8");
            org.json.JSONObject jsonReqOrg = this.convertToJSONObject(dataRequest);
            jsonReqOrg.put("rawReq", dataRequest);
            jsonReq = (JSONObject) new JSONParser().parse(jsonReqOrg.toString());
            jsonReq = this.enhanceRequest(jsonReq);
        } catch (Exception ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        }
        return jsonReq;
    }

    public byte[] getFile(String filePath) {
        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        String data = "Internal Error";
        byte[] dataOut = data.getBytes(StandardCharsets.UTF_8);
        try {
            //---------------read file
            File fileResource = new File(filePath);
            //---------------first way
            //dataOut = FileUtils.readFileToByteArray(fileResource);
            //---------------another way
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(fileResource);
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
            dataOut = ous.toByteArray();
            //-----------------
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (ous != null)
                    ous.close();
            } catch (IOException e) {
                // swallow, since not that important
            }
            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
                // swallow, since not that important
            }
        }
        return dataOut;
    }

    public ResponseEntity<StreamingResponseBody> routeDefaultHandler(HttpServletRequest request) {
        ResponseEntity<StreamingResponseBody> response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        try {
            String fullUrl = request.getRequestURI();
            String fullUri = request.getRequestURI().substring(1);
            String baseUrl = request.getRequestURL().toString().replace(fullUrl, "");
            String[] uriSplit = fullUri.split("/");
            if (uriSplit.length == 0) return ResponseEntity.notFound().build();
            for (String uri : uriSplit) {
                //System.out.println("uriSplit : " + uri);
            }
            //handle exteral/resource file
            String fileName = uriSplit[uriSplit.length - 1];
//            String fileName = uriSplit[uriSplit.length - 1].toLowerCase();
            Optional<MediaType> mediaTypeOpt = MediaTypeFactory.getMediaType(fileName);
            // Jika tidak ketemu (seperti untuk file .map)
            if (mediaTypeOpt.isEmpty() && fileName.endsWith(".map")) {
                mediaTypeOpt = Optional.of(MediaType.parseMediaType("application/json"));
            }
            // if (fileName.matches(".*\\.(js|css|png|jpg|gif|ttf|woff|woff2|json)$")) // bisa pakai ini
            if (mediaTypeOpt.isPresent()) {
                //-----------------------------------------get file from cache if exist-------------------------------
                String fileResourcePath = fullUrl.replace(baseUrl, "");
                byte[] dataOut = "".getBytes();
                if (this.cacheMaster.isKeyInCache(fileResourcePath)) {
                    Element elemCache = this.cacheMaster.get(fileResourcePath);
                    if (elemCache != null) {
                        if (this.cacheMaster.isExpired(elemCache)) {
                            cacheMaster.remove(fileResourcePath);
                            dataOut = this.getFile(this.entityConfig.getWorkDir() + "webroot" + fileResourcePath);
                            this.cacheMaster.put(new Element(fileResourcePath, dataOut));
                        } else {
                            dataOut = (byte[]) elemCache.getObjectValue();
                        }
                    } else {
                        cacheMaster.remove(fileResourcePath);
                        dataOut = this.getFile(this.entityConfig.getWorkDir() + "webroot" + fileResourcePath);
                        this.cacheMaster.put(new Element(fileResourcePath, dataOut));
                    }
                } else {
                    dataOut = this.getFile(this.entityConfig.getWorkDir() + "webroot" + fileResourcePath);
                    this.cacheMaster.put(new Element(fileResourcePath, dataOut));
                }
                //force no cache
                //dataOut = this.getFile(this.entityConfig.getWorkDir() + "webroot" + fileResourcePath);
                if (dataOut == null || dataOut.length == 0) {
                    return ResponseEntity.notFound().build();
                }
                MediaType mediaType = mediaTypeOpt.orElse(MediaType.APPLICATION_OCTET_STREAM);
                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Type", String.valueOf(mediaType));
                byte[] finalDataOut = dataOut;
                StreamingResponseBody responseBody = outputStream -> {
                    outputStream.write(finalDataOut);
                    outputStream.flush();
                };
                response = ResponseEntity.ok()
                        .headers(headers)
                        .contentType(mediaType)
                        .body(responseBody);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

        }
        return response;
    }

    public HashMap handleRequestProperties(HttpServletRequest request) {
        String data = "Internal Error";
        byte[] dataOut = data.getBytes(StandardCharsets.UTF_8);
        //ResponseEntity<byte[]> response = ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        ResponseEntity<byte[]> response = new ResponseEntity<>(HttpStatus.BAD_GATEWAY);

        String fullUri = request.getRequestURI().substring(1);
        String fullUrl = request.getRequestURI();
        String query = (Objects.isNull(request.getQueryString())) ? "" : request.getQueryString();
        String baseUrl = "";
        if (!fullUri.equals("")) {
            baseUrl = request.getRequestURL().toString().replace(fullUrl, "");
        } else {
            fullUrl = request.getRequestURL().toString();
            baseUrl = fullUrl.substring(0, fullUrl.length() - 1);
        }
        String hostPort = String.valueOf(request.getServerPort());

//        System.out.println("fullUri : " + fullUri);
//        System.out.println("fullUrl : " + fullUrl);
//        System.out.println("query : " + query);

        //to convert query get and post param http to hashmap
        HashMap<String, Object> objParams = new HashMap<>();
        for (String params : Collections.list(request.getParameterNames())) {
            // Whatever you want to do with your map
            // Key : params
            // Value : httpServletRequest.getParameter(params)
//            System.out.println("params : " + params);
//            System.out.println("Value : " + request.getParameter(params));
            objParams.put(params, request.getParameter(params));
        }
        if (!request.getParameterNames().hasMoreElements()) {
            // 2. BACA JSON BODY (Karena getParameter pasti kosong buat JSON)
            try {
                // Pakai Scanner atau IOUtils buat baca String dari request body
                String body = new java.util.Scanner(request.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();

                if (body != null && !body.isEmpty()) {
                    // Convert String JSON ke JSONObject
                    org.json.JSONObject jBody = new org.json.JSONObject(body);
                    // Pindahin isi JSON ke objParams
                    objParams.put("dataSource", jBody.getJSONArray("dataSource").toList());
                    objParams.put("sheetName", jBody.getString("sheetName"));
//                System.out.println(this.objParams.toString());
                }
            } catch (Exception e) {
                System.err.println("Gagal baca JSON Body: " + e.getMessage());
            }
        }

        String[] uriSplit = fullUri.split("/");
//        for (String uri : uriSplit) {
//            System.out.println("uriSplit : " + uri);
//        }
        String webroot = uriSplit[0];
        String controller = uriSplit[1];
        String methode = uriSplit[2];
        String view = (uriSplit.length >= 4) ? uriSplit[3] : "";
        String webRootUrl = request.getRequestURL().toString().replace(fullUrl, "") + "/" + webroot + "/";

        HashMap<String, Object> objRequestProperties = new HashMap<>();
        objRequestProperties.put("response", response);
        objRequestProperties.put("fullUri", fullUri);
        objRequestProperties.put("fullUrl", fullUrl);
        objRequestProperties.put("query", query);
        objRequestProperties.put("objParams", objParams);
        objRequestProperties.put("webroot", webroot);
        objRequestProperties.put("controller", controller);
        objRequestProperties.put("methode", methode);
        objRequestProperties.put("view", view);
        objRequestProperties.put("webRootUrl", webRootUrl);
        objRequestProperties.put("baseUrl", baseUrl);
        objRequestProperties.put("hostPort", hostPort);

        return objRequestProperties;
    }

    public ResponseEntity<StreamingResponseBody> redirectToLogin(HttpServletRequest request, HashMap objRequestProperties) {
        ResponseEntity<StreamingResponseBody> response = new ResponseEntity<>(HttpStatus.SEE_OTHER);
        try {
            String baseUrl = objRequestProperties.get("baseUrl").toString();
            String hostPort = objRequestProperties.get("hostPort").toString();
            String redirectUrl = baseUrl;
//            String redirectUrl = baseUrl.replace(hostPort, "20001");
//            if (baseUrl.toLowerCase().contains("http://")) {
//                redirectUrl = redirectUrl.replace("http://", "https://");
//            }
            URI loginPage = new URI(redirectUrl + "/SpreadSheet/ControllerSpreadSheetLogin/getLogin/login?owner=true");
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(loginPage);
            response = new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
            if (request.getMethod().equals("POST")) {
                JSONObject jObjResponse = new JSONObject();
                jObjResponse.put("rc", "02");
                jObjResponse.put("message", "Failed");
                jObjResponse.put("redirectUrl", loginPage.toString());
                headers.add("Content-Type", String.valueOf(MediaType.TEXT_HTML));
                StreamingResponseBody responseBody = outputStream -> {
                    String data = jObjResponse.toString();
                    outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                };
                response = ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaType.TEXT_HTML)
                        .body(responseBody);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }

    public HashMap hashStringToHashMap(String hashStr) {
        StringBuilder sb = new StringBuilder(hashStr);
        sb.deleteCharAt(0); // Remove the first character
        sb.deleteCharAt(sb.length() - 1); // Remove the last character
        String modifiedString = sb.toString();
        HashMap<String, Object> hashObj = new HashMap<>();
        String[] pairs = modifiedString.split(",(?![^{]*})"); // Split by comma not inside braces
        for (int i = 0; i < pairs.length; i++) {
            String pair = pairs[i];
            String[] keyValue = pair.split("=", 2); // Split by first '=' only
            if (keyValue.length == 2) {
                if (String.valueOf(keyValue[1]).startsWith("{") && String.valueOf(keyValue[1]).endsWith("}")) {
                    // Recursively parse nested map
                    hashObj.put(String.valueOf(keyValue[0]).replaceAll("\\s+", ""), hashStringToHashMap(keyValue[1]));
                } else {
                    hashObj.put(String.valueOf(keyValue[0]).replaceAll("\\s+", ""), String.valueOf(keyValue[1]));
                }
            }
        }
        return hashObj;
    }
}
