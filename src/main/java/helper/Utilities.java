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
import entity.sqlite.EntityHtb;
import helper.rest_client.http.HttpResponse;
import helper.rest_client.http.RestClient;
import jakarta.servlet.http.Cookie;
import koneksi.DatabaseUtilitiesPgsqlCecenet;
import koneksi.DatabaseUtilitiesPgsqlMamen;
import net.sf.ehcache.Cache;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.*;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * @author Hasan
 */

@Component("Utilities")
@Scope("prototype")
public class Utilities {

    private Cache cacheMaster;
    private Cache cacheCookies;
    private EntityConfig entityConfig;
    private final ControllerLog controllerLog;

    @Autowired
    public Utilities(DependencyConfig dependencyConfig) {
        this.cacheMaster = dependencyConfig.getCacheMaster();
        this.cacheCookies = dependencyConfig.getCacheCookies();
        this.entityConfig = dependencyConfig.getEntityConfig();
        this.controllerLog = dependencyConfig.getControllerLog();
    }

//    public Utilities(EntityConfig entityConfig, Cache cacheMaster, Cache cacheCookies, ControllerLog controllerLog) {
//        this.cacheMaster = cacheMaster;
//        this.cacheCookies = cacheCookies;
//        this.entityConfig = entityConfig;
//        this.controllerLog = controllerLog;
//    }

    public Map<String, Object> convertObjectToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields(); // Get all declared fields (public, private, protected)

        for (Field field : fields) {
            try {
                field.setAccessible(true); // Allow access to private fields

                //prevent org.json.JSONArray to infinite loop, when convert HashMap object to jArray
                // Mencegah Infinite Loop saat JSONArray men-scan relasi Bidirectional (Parent-Children).
                // Tanpa filter ini, library JSON akan memproses objek secara rekursif hingga StackOverflow.
                if ((field.getType().equals(EntityHtb.class) && field.getName().equals("parentHtbClass"))
                        || (field.getType().equals(Set.class) && field.getName().equals("childrenHtbClass"))) {
                    continue;
                }
                if(field.get(obj) == null){
                    map.put(field.getName(), "");
                } else {
                    map.put(field.getName(), field.get(obj)); // Get field name as key, field value as value
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace(); // Handle potential access issues
            }
        }
        return map;
    }

    public String GeneratePassword() {
        final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int N = alphabet.length();
        Random r = new Random();
        String password = "";
        for (int i = 0; i < 6; i++) {
            password = password + alphabet.charAt(r.nextInt(N));
        }
        return password;
    }

    public String GeneratePIN() {
        final String alphabet = "0123456789";
        final int N = alphabet.length();
        Random r = new Random();
        String password = "";
        for (int i = 0; i < 6; i++) {
            password = password + alphabet.charAt(r.nextInt(N));
        }
        return password;
    }

    public String generateVoucher() {
        final String alphabet = "0123456789";
        final int N = alphabet.length();
        Random r = new Random();
        String password = "";
        for (int i = 0; i < 10; i++) {
            password = password + alphabet.charAt(r.nextInt(N));
        }
        return password;
    }

    public synchronized String createStan() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(999999999);
        String formatted = String.format("%012d", num);
        return formatted;
    }

    public String createTrxid() {
        String hasil = "";
        try {
            NumberFormat formatter = new DecimalFormat("000");
            Random random = new Random();
            int temp = random.nextInt(999);
            hasil = this.getDate("yyyyMMddHHmmss") + formatter.format(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasil;
    }

    public String getSHA256(String string) {
        String hasil = "";
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(string.getBytes());

            byte byteData[] = md.digest();

            // convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            hasil = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasil;
    }

    public String getSignatureSha1(String string) {
        String hasil = "";
        try {

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(string.getBytes());

            byte byteData[] = md.digest();

            // convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            hasil = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasil;
    }

    public String getMD5(String string) {
        String hasil = "";
        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());

            byte byteData[] = md.digest();

            // convert the byte to hex format method 1
            StringBuilder sb = new StringBuilder();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }

            hasil = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hasil;
    }

    public Timestamp getDate() {
        Timestamp timestamp = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String date = format.format(new Date()).toString();
            Date date1 = format.parse(date);
            timestamp = new Timestamp(date1.getTime());
        } catch (ParseException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        }
        return timestamp;
    }

    public String getDate(String format) {
        // "yyyy-MM-dd HH:mm:ss"
        Date now = new Date();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat(format);
        String strDate = sdf.format(now);

        sdf = null;
        return strDate;
    }

    public JSONObject curlPost(String url, String data, boolean encode) {
        JSONObject responseObj = null;
        HttpURLConnection con = null;
        DataOutputStream wr = null;
        InputStream is = null;
        BufferedReader rd = null;
        boolean apiSession = (url.contains("Session")) ? true : false;
        try {
            String line = null;
            if (encode) {
                data = URLEncoder.encode(data.toString(), "UTF-8");
            }
            con = (HttpURLConnection) new URL(url).openConnection();

            if (apiSession == false) {
                this.controllerLog.logStreamWriter("SEND REQUEST TO : " + url + " with data : " + data);
            } else {
                this.controllerLog.logParsingWriter("SEND REQUEST TO : " + url + " with data : " + data);
            }

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Length", "" + Integer.toString(data.getBytes().length));
            con.setRequestProperty("Content-Language", "en-US");
            con.setRequestProperty("Content-Type", "text/plain");

            con.setConnectTimeout(120000);
            con.setReadTimeout(120000);

            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);

            // Send request
            wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(data);

            String code = String.valueOf(con.getResponseCode());

            //System.err.println(code);
            // Get Response
            is = con.getInputStream();
            rd = new BufferedReader(new InputStreamReader(is));

            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            if (response.toString().trim().length() > 10) {
                //System.out.println("RESPONSE OK..!!");
                JSONParser parser = new JSONParser();
                if (apiSession == false) {
                    this.controllerLog.logStreamWriter("RESPONSE FROM : " + url + " with data : " + response.toString());
                } else {
                    this.controllerLog.logParsingWriter("RESPONSE FROM : " + url + " with data : " + response.toString());
                }
                Object obj = parser.parse(response.toString());
                responseObj = (JSONObject) obj;
                responseObj.put("ResponseCode", code);
            } else {
                //System.out.println("RESPONSE KOSONG..!!");
                //System.out.println(response);
                responseObj = new JSONObject();
                responseObj.put("ResponseCode", "500");
                responseObj.put("ACK", "NOK");
                responseObj.put("pesan", "Mohon maaf untuk sementara transaksi tidak dapat dilakukan");
                responseObj.put("MSG", "Mohon maaf untuk sementara transaksi tidak dapat dilakukan");
                responseObj.put("result_code", "");
            }

        } catch (java.net.SocketTimeoutException e) {
            responseObj = new JSONObject();
            responseObj.put("ResponseCode", "504");
            responseObj.put("ACK", "NOK");
            responseObj.put("pesan", "TIME OUT!!");
            responseObj.put("result_code", "");
            String s = Throwables.getStackTraceAsString(e);
            controllerLog.logErrorWriter(s);
        } catch (Exception e) {
            String s = Throwables.getStackTraceAsString(e);
            controllerLog.logErrorWriter(s);

            responseObj = new JSONObject();
            responseObj.put("ResponseCode", "500");
            responseObj.put("ACK", "NOK");
            responseObj.put("pesan", "EXT : INTERNAL SERVER ERROR");
            responseObj.put("result_code", "");

        } finally {
            try {
                if (wr != null) {
                    wr.flush();
                    wr.close();
                }
                if (is != null) {
                    is.close();
                }
                if (rd != null) {
                    rd.close();
                }
                if (con != null) {
                    con.disconnect();
                }
            } catch (Exception ex) {
                String s = Throwables.getStackTraceAsString(ex);
                controllerLog.logErrorWriter(s);
            }
        }
        return responseObj;
    }

    public JSONObject sendUnirest(String url, Map<String, String> headers, JSONObject body) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        JSONObject resp = new JSONObject();
        JSONObject jsonResp = new JSONObject();
        // creating all trust manager. It will accept as valid any certificate.
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509ExtendedTrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) throws CertificateException {
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) throws CertificateException {
                    }
                }
        };
        SSLContext sslContext = SSLContext.getInstance("SSL"); //TLSv1,TLSv1.1,TLSv1.2
        // set our friendly trust manager to ssl context. we pass it to connection later
        sslContext.init(null, trustAllCerts, new SecureRandom());

        SSLConnectionSocketFactory sslConnectionSocketFactory =
                new SSLConnectionSocketFactory(sslContext, new String[]
                        {"SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"}, null,
                        NoopHostnameVerifier.INSTANCE);

        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder.setConnectTimeout(120000);
        requestBuilder.setConnectionRequestTimeout(120000);
        requestBuilder.setSocketTimeout(120000);

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setSSLSocketFactory(sslConnectionSocketFactory);
        httpClientBuilder.setDefaultRequestConfig(requestBuilder.build());
        httpClientBuilder.setSSLHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
        RestClient client = RestClient.with(httpClientBuilder, sslConnectionSocketFactory);
//        RestClient client = RestClient.builder().followRedirect(false).build();
        InputStream is = null;
        BufferedReader rd = null;
        boolean apiSession = (url.contains("Session")) ? true : false;
        boolean apiSendMessage = (url.contains("sendMessage")) ? true : false;
        try {
            if (apiSession == false && apiSendMessage == false) {
                this.controllerLog.logStreamWriter("Request_to_partner = " + url + " header = " + headers.toString() + " request = " + body.toString());
            } else {
                this.controllerLog.logParsingWriter("Request_to : " + url + " with data : " + body.toString());
            }
            HttpResponse<String> response = client.post(url).headers(headers).body(body).asString();
            int status = response.getStatus();
            is = response.getRawBody();
            rd = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();
            for (String line; (line = rd.readLine()) != null; ) {
                sb.append(line);
//                System.out.println(line);
//                this.ctrlLog.logStremWriter(line);
            }
            JSONParser parser = new JSONParser();
            jsonResp = (JSONObject) parser.parse(sb.toString().trim());
            if (apiSession == false && apiSendMessage == false) {
                this.controllerLog.logStreamWriter("Response_from_partner : " + url + " status : " + status);
            } else {
                this.controllerLog.logParsingWriter("Response_from : " + url + " status : " + status);
            }
        } catch (Exception ex) {
            String s = Throwables.getStackTraceAsString(ex);
            this.controllerLog.logErrorWriter(s);
        } finally {
            try {
                client.close();
            } catch (Exception ex) {
                String s = Throwables.getStackTraceAsString(ex);
                this.controllerLog.logErrorWriter(s);
            }
        }
        return jsonResp;
    }

    public JSONObject removeJobjForbidKey(JSONObject jsonReq, boolean noLogDataEnc) {
        JSONObject objJsonRecreate = new JSONObject();
        for (Iterator iterator = jsonReq.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            //System.out.println(this.jSONObject.get(key));
            if ((key.equalsIgnoreCase("data_enc") || key.equalsIgnoreCase("rawReq")) && !noLogDataEnc) {
                objJsonRecreate.put(key, jsonReq.get(key));
            } else if (!key.equalsIgnoreCase("password")
                    && !key.equalsIgnoreCase("data_enc")
                    && !key.equalsIgnoreCase("rawReq")
                    && !key.equalsIgnoreCase("customer_photo_national_id_base64")
                    && !key.equalsIgnoreCase("customer_photo_profil_base64")
                    && !key.equalsIgnoreCase("fotoKtpBase64")
                    && !key.equalsIgnoreCase("sender_identity_picture")
                    && !key.equalsIgnoreCase("sender_signature_picture")
                    && !key.equalsIgnoreCase("fotoKtpMukaBase64")
                    && !key.equalsIgnoreCase("sender_identity_picture")
                    && !key.equalsIgnoreCase("sender_signature_picture")
                    && !key.equalsIgnoreCase("sender_selfie_picture")
                    && !key.equalsIgnoreCase("receiver_identity_picture")
                    && !key.equalsIgnoreCase("receiver_signature_picture")
                    && !key.equalsIgnoreCase("receiver_selfie_picture")
                    && !key.equalsIgnoreCase("pin")
                    && !key.equalsIgnoreCase("pinBaru")
                    && !key.equalsIgnoreCase("pinLama")) {
                objJsonRecreate.put(key, jsonReq.get(key));
            }
        }
        return objJsonRecreate;
    }

    public int insertLog(String idTrx, JSONObject objJson) {
        //<editor-fold defaultstate="collapsed" desc="query insertLog">
        DatabaseUtilitiesPgsqlCecenet databaseUtilitiesPgsql = new DatabaseUtilitiesPgsqlCecenet();
        Connection connPgsql = null;
        PreparedStatement stPgsql = null;
        ResultSet rsPgsql = null;
        int idTemp = 0;
        int idResultTmp = 0;
        //remove password object before insert to log
        JSONObject objJsonRecreate = new JSONObject();
        for (Iterator iterator = objJson.keySet().iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            //System.out.println(this.jSONObject.get(key));
            if (!key.equals("password")) {
                objJsonRecreate.put(key, objJson.get(key));
            }
        }
        try {
            connPgsql = databaseUtilitiesPgsql.getConnection(this.entityConfig);
            String query = "insert "
                    + "    into"
                    + "        \"tr_trx_token_log\""
                    + "        (\"id_trx\", \"request\", \"request_time\") "
                    + "    values"
                    + "        (?, ?, ?);";
            stPgsql = connPgsql.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stPgsql.setString(1, idTrx);
            stPgsql.setString(2, objJsonRecreate.toString());
            stPgsql.setTimestamp(3, this.getDate());
            idTemp = stPgsql.executeUpdate();
            rsPgsql = stPgsql.getGeneratedKeys();
            while (rsPgsql.next()) {
                idResultTmp = rsPgsql.getInt(1);
            }
        } catch (ParserConfigurationException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } catch (SQLException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } finally {
            try {
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
        //</editor-fold>

        return idResultTmp;
    }

    public void updateLog(int idTmp, String resp) {
        JSONParser parser = new JSONParser();
        JSONObject jsonResp = null;
        try {
            jsonResp = (JSONObject) parser.parse(resp);
        } catch (org.json.simple.parser.ParseException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        }
        //<editor-fold defaultstate="collapsed" desc="query updateTmp">
        DatabaseUtilitiesPgsqlCecenet databaseUtilitiesPgsql = new DatabaseUtilitiesPgsqlCecenet();
        Connection connPgsql = null;
        PreparedStatement stPgsql = null;
        ResultSet rsPgsql = null;
        try {
            connPgsql = databaseUtilitiesPgsql.getConnection(this.entityConfig);
            String query = "update \"tr_trx_token_log\" set \"response\" = ?, \"response_time\" = ? where id_trx_token_log = ?;";
            stPgsql = connPgsql.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stPgsql.setString(1, jsonResp.toString());
            stPgsql.setTimestamp(2, this.getDate());
            stPgsql.setLong(3, idTmp);
            stPgsql.executeUpdate();
        } catch (ParserConfigurationException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } catch (SQLException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } finally {
            try {
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
        //</editor-fold>
    }

    public JSONObject writeLogStreamRequest(String tipe, JSONObject jsonRequest, boolean noLogDataEnc) {
        JSONObject jsonReqForLog = this.removeJobjForbidKey(jsonRequest, noLogDataEnc);
        //System.out.println("json req for log : " + jsonReqForLog.toString());
        if (tipe.equals("setRate")) {
            this.controllerLog.logDebugWriter("Req " + tipe + ": id = " + jsonRequest.get("downlineId").toString() + " ; sourceIp/sport = " + jsonRequest.get("ipAddr").toString() + '/' + jsonRequest.get("port").toString() + "; Agent = " + jsonRequest.get("browserAgent").toString() + "; Body = " + jsonReqForLog.toString());
        } else {
            this.controllerLog.logStreamWriter("Req " + tipe + ": id = " + jsonRequest.get("downlineId").toString() + " ; sourceIp/sport = " + jsonRequest.get("ipAddr").toString() + '/' + jsonRequest.get("port").toString() + "; Agent = " + jsonRequest.get("browserAgent").toString() + "; Body = " + jsonReqForLog.toString());
        }
        return jsonReqForLog;
    }

    public JSONObject writeLogStreamResponse(String tipe, JSONObject jsonRequest, String resp) {
        JSONObject jsonReqForLog = new JSONObject();
        try {
            if (tipe.equals("setRate")) {
                this.controllerLog.logDebugWriter("Resp " + tipe + ": id = " + jsonRequest.get("downlineId").toString() + " ; Response = " + resp);
            } else {
                this.controllerLog.logStreamWriter("Resp " + tipe + ": id = " + jsonRequest.get("downlineId").toString() + " ; Response = " + resp);
            }
        } catch (Exception ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        }
        return jsonReqForLog;
    }

    public static void setupFolder(String workDir) {
        //check if folder cache exist
//        File f = new File(workDir + File.separator + "config");
//        if (!f.exists() && !f.isDirectory()) {
//            System.out.println("creating directory: " + f.getName());
//            boolean result = false;
//            try {
//                f.mkdir();
//                result = true;
//            } catch (SecurityException se) {
//                //handle it
//            }
//            if (result) {
//                System.out.println("directory config created");
//            }
//        }

        //check if folder cache exist
        File f = new File(workDir + File.separator + "cache");
        if (!f.exists() && !f.isDirectory()) {
            System.out.println("creating directory: " + f.getName());
            boolean result = false;
            try {
                f.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
            if (result) {
                System.out.println("directory cache created");
            }
        }

        //check if folder logs exist
        f = new File(workDir + File.separator + "logs");
        if (!f.exists() && !f.isDirectory()) {
            System.out.println("creating directory: " + f.getName());
            boolean result = false;
            try {
                f.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
            if (result) {
                System.out.println("directory cache created");
            }
        }

        //check if folder cache exist
        f = new File(workDir + File.separator + "app-logs");
        if (!f.exists() && !f.isDirectory()) {
            System.out.println("creating directory: " + f.getName());
            boolean result = false;
            try {
                f.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
            if (result) {
                System.out.println("directory cache created");
            }
        }
    }

    public JSONArray getAccountLevelByBoardId(JSONObject jObjReq){
        DatabaseUtilitiesPgsqlMamen databaseUtilitiesPgsql = new DatabaseUtilitiesPgsqlMamen();
        Connection connPgsql = null;
        PreparedStatement stPgsql = null;
        ResultSet rsPgsql = null;
        JSONArray jArrRow = new JSONArray();
        try {
            connPgsql = databaseUtilitiesPgsql.getConnection(entityConfig);
            String query = "SELECT " +
                    " *  " +
                    "FROM " +
                    " ms_board " +
                    " A LEFT JOIN ms_group b ON b.group_id = A.group_id  " +
                    "WHERE " +
                    " A.board_id = ?  " +
                    " AND account_id = ?;";
            stPgsql = connPgsql.prepareStatement(query);
            stPgsql.setLong(1, Long.parseLong(jObjReq.get("board_id").toString()));
            stPgsql.setLong(2, Long.parseLong(jObjReq.get("account_id").toString()));
            rsPgsql = stPgsql.executeQuery();
            ResultSetMetaData metaData = rsPgsql.getMetaData();
            int colCount = metaData.getColumnCount();
            //convert resultset to jsonArray
            int j = 0;
            while (rsPgsql.next()) {
                JSONObject jObjColumn = new JSONObject();
                for (int i = 1; i <= colCount; i++) {
                    if (rsPgsql.getObject(i) == null) {
                        jObjColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        jObjColumn.put(metaData.getColumnLabel(i), rsPgsql.getString(i));
                    }
                }
                jArrRow.add(j, jObjColumn);
                j = j++;
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
        return jArrRow;
    }

    public HttpHeaders getHeaderAndRenewCookie(CookieHelperDb cookieHelper, String cookie){
        Cookie cookieObj = cookieHelper.renewCookie(cookie);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", String.valueOf(MediaType.TEXT_HTML));
        headers.add("Set-Cookie", "mamenCookie=" + cookieObj.getValue() + "; Max-Age=" + cookieObj.getMaxAge() + "; Path=/; " + cookieObj.getPath() + "; " + cookieObj.getSecure());
        return headers;
    }

    public JSONArray getBoardByTaskId(JSONObject objParams) {
        DatabaseUtilitiesPgsqlMamen databaseUtilitiesPgsql = new DatabaseUtilitiesPgsqlMamen();
        Connection connPgsql = null;
        PreparedStatement stPgsql = null;
        ResultSet rsPgsql = null;
        JSONArray jArrRow = new JSONArray();
        try {
            connPgsql = databaseUtilitiesPgsql.getConnection(entityConfig);
            String query = "SELECT " +
                    " *  " +
                    "FROM " +
                    " ms_task " +
                    " A LEFT JOIN ms_column b ON b.column_id = A.column_id " +
                    " LEFT JOIN ms_board C ON C.board_id = b.board_id  " +
                    "WHERE " +
                    " A.task_id = ?;";
            stPgsql = connPgsql.prepareStatement(query);
            stPgsql.setLong(1, Long.parseLong(objParams.get("task_id").toString()));
            rsPgsql = stPgsql.executeQuery();
            ResultSetMetaData metaData = rsPgsql.getMetaData();
            int colCount = metaData.getColumnCount();
            //convert resultset to jsonArray
            int j = 0;
            while (rsPgsql.next()) {
                JSONObject jObjColumn = new JSONObject();
                for (int i = 1; i <= colCount; i++) {
                    if (rsPgsql.getObject(i) == null) {
                        jObjColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        jObjColumn.put(metaData.getColumnLabel(i), rsPgsql.getString(i));
                    }
                }
                jArrRow.add(j, jObjColumn);
                j = j++;
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
        return jArrRow;
    }

    public JSONArray getBoardByColumnId(JSONObject objParams) {
        DatabaseUtilitiesPgsqlMamen databaseUtilitiesPgsql = new DatabaseUtilitiesPgsqlMamen();
        Connection connPgsql = null;
        PreparedStatement stPgsql = null;
        ResultSet rsPgsql = null;
        JSONArray jArrRow = new JSONArray();
        try {
            connPgsql = databaseUtilitiesPgsql.getConnection(entityConfig);
            String query = "SELECT " +
                    " *  " +
                    "FROM " +
                    " ms_column a LEFT JOIN ms_board b ON b.board_id = a.board_id  " +
                    "WHERE " +
                    " A.column_id = ?;";
            stPgsql = connPgsql.prepareStatement(query);
            stPgsql.setLong(1, Long.parseLong(objParams.get("column_id").toString()));
            rsPgsql = stPgsql.executeQuery();
            ResultSetMetaData metaData = rsPgsql.getMetaData();
            int colCount = metaData.getColumnCount();
            //convert resultset to jsonArray
            int j = 0;
            while (rsPgsql.next()) {
                JSONObject jObjColumn = new JSONObject();
                for (int i = 1; i <= colCount; i++) {
                    if (rsPgsql.getObject(i) == null) {
                        jObjColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        jObjColumn.put(metaData.getColumnLabel(i), rsPgsql.getString(i));
                    }
                }
                jArrRow.add(j, jObjColumn);
                j = j++;
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
        return jArrRow;
    }

    public JSONObject getAccountByEmail(String account_email) {
        DatabaseUtilitiesPgsqlMamen databaseUtilitiesPgsqlRemit = new DatabaseUtilitiesPgsqlMamen();
        Connection connPgsql = null;
        PreparedStatement stPgsql = null;
        ResultSet rsPgsql = null;
        long idResultTmp = 0;
        JSONObject jObjColumn = new JSONObject();
        try {
            connPgsql = databaseUtilitiesPgsqlRemit.getConnection(this.entityConfig);
            String query = "SELECT " +
                    " *  " +
                    "FROM " +
                    " ms_account a   " +
                    "WHERE " +
                    " A.account_email = ?;";
            stPgsql = connPgsql.prepareStatement(query);
            stPgsql.setString(1, account_email);
            rsPgsql = stPgsql.executeQuery();
            ResultSetMetaData metaData = rsPgsql.getMetaData();
            int colCount = metaData.getColumnCount();
            //convert resultset to jsonObject
            while (rsPgsql.next()) {
                for (int i = 1; i <= colCount; i++) {
                    if (rsPgsql.getObject(i) == null) {
                        jObjColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        jObjColumn.put(metaData.getColumnLabel(i), rsPgsql.getString(i));
                    }
                }
            }
        } catch (ParserConfigurationException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } catch (SQLException ex) {
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
        return jObjColumn;
    }

    public JSONObject getAccountGroupByEmail(JSONObject jObjReq) {
        DatabaseUtilitiesPgsqlMamen databaseUtilitiesPgsqlRemit = new DatabaseUtilitiesPgsqlMamen();
        Connection connPgsql = null;
        PreparedStatement stPgsql = null;
        ResultSet rsPgsql = null;
        long idResultTmp = 0;
        JSONObject jObjColumn = new JSONObject();
        try {
            connPgsql = databaseUtilitiesPgsqlRemit.getConnection(this.entityConfig);
            String query = "SELECT " +
                    " *  " +
                    "FROM " +
                    " ms_account " +
                    " A LEFT JOIN ms_group b ON b.account_id = A.account_id " +
                    " LEFT JOIN ms_level C ON C.level_id = b.level_id  " +
                    "WHERE " +
                    " A.account_email = ?" +
                    " and b.group_id = ?;";
            stPgsql = connPgsql.prepareStatement(query);
            stPgsql.setString(1, jObjReq.get("account_email").toString().trim());
            stPgsql.setString(2, jObjReq.get("group_id").toString());
            rsPgsql = stPgsql.executeQuery();
            ResultSetMetaData metaData = rsPgsql.getMetaData();
            int colCount = metaData.getColumnCount();
            //convert resultset to jsonObject
            while (rsPgsql.next()) {
                for (int i = 1; i <= colCount; i++) {
                    if (rsPgsql.getObject(i) == null) {
                        jObjColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        jObjColumn.put(metaData.getColumnLabel(i), rsPgsql.getString(i));
                    }
                }
            }
        } catch (ParserConfigurationException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } catch (SQLException ex) {
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
        return jObjColumn;
    }

    public JSONObject getLevelName(String level_id) {
        DatabaseUtilitiesPgsqlMamen databaseUtilitiesPgsqlRemit = new DatabaseUtilitiesPgsqlMamen();
        Connection connPgsql = null;
        PreparedStatement stPgsql = null;
        ResultSet rsPgsql = null;
        long idResultTmp = 0;
        JSONObject jObjColumn = new JSONObject();
        try {
            connPgsql = databaseUtilitiesPgsqlRemit.getConnection(this.entityConfig);
            String query = "SELECT " +
                    " *  " +
                    "FROM " +
                    " ms_level a " +
                    "WHERE " +
                    " A.level_id = ?;";
            stPgsql = connPgsql.prepareStatement(query);
            stPgsql.setLong(1, Long.parseLong(level_id));
            rsPgsql = stPgsql.executeQuery();
            ResultSetMetaData metaData = rsPgsql.getMetaData();
            int colCount = metaData.getColumnCount();
            //convert resultset to jsonObject
            while (rsPgsql.next()) {
                for (int i = 1; i <= colCount; i++) {
                    if (rsPgsql.getObject(i) == null) {
                        jObjColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        jObjColumn.put(metaData.getColumnLabel(i), rsPgsql.getString(i));
                    }
                }
            }
        } catch (ParserConfigurationException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } catch (SQLException ex) {
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
        return jObjColumn;
    }
}
