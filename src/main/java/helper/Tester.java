package helper;

import configuration.ControllerEncDec;
import configuration.ControllerLog;
import configuration.DependencyConfig;
import entity.EntityConfig;
import io.joshworks.restclient.http.HttpResponse;
import io.joshworks.restclient.http.RestClient;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Hasan on 6/22/2018.
 */
@Component("Tester")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class Tester {
    private final Utilities utilities;
    private final Cache cache;
    private final Cache cacheCookies;
    private final Cache cacheMaster;
    private final ControllerLog controllerLog;
    private final EntityConfig entityConfig;
    private final ControllerEncDec controllerEncDec;

    @Autowired
    public Tester(DependencyConfig dependencyConfig, Utilities utilities, ControllerEncDec controllerEncDec) {
        CacheManager cacheManager = CacheManager.getInstance();
        this.cache = cacheManager.getCache(" SpreadSheetCacheApp");
        Element config = this.cache.get("config");
        this.entityConfig = dependencyConfig.getEntityConfig();
        this.cacheCookies = dependencyConfig.getCacheCookies();
        this.cacheMaster = dependencyConfig.getCacheMaster();
        this.controllerLog = dependencyConfig.getControllerLog();
        this.utilities = utilities;
        this.controllerEncDec = controllerEncDec;
    }

    public void time() {
        Date date = new Date();
        DateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS'Z'");
        TimeZone gmtTime = TimeZone.getTimeZone("GMT");
        gmtFormat.setTimeZone(gmtTime);
        System.out.println("Current Time: " + date);
        System.out.println("GMT Time: " + gmtFormat.format(date));
    }

    public void getFastPay() {
        try {
            String trxid = "25558888888888888888888888813";
            String amount = "50000";
            String merchant_id = "aggregator_inception".toUpperCase();
            String txn_password = "eyknw".toUpperCase();
            String privateKey = "##" + merchant_id + "##" + txn_password + "##" + trxid + "##" + amount + ".00##0##";
            String sign = this.utilities.getSignatureSha1(privateKey);

            RestClient client = RestClient.builder().followRedirect(false).build();
            HttpResponse<String> response = client.post("https://fpg.faspay.co.id/payment")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36")
                    .header("cache-control", "no-cache")
                    .header("Accept-Language", "en-US,en;q=0.5")
                    .field("TRANSACTIONTYPE", "1")
                    .field("RESPONSE_TYPE", "2")
                    .field("LANG", "")
                    .field("MERCHANTID", "aggregator_inception")
                    .field("PAYMENT_METHOD", "1")
                    .field("TXN_PASSWORD", "eyknw")
                    .field("MERCHANT_TRANID", trxid)
                    .field("CURRENCYCODE", "IDR")
                    .field("AMOUNT", "50000.00")
                    .field("CUSTNAME", "merhcant test CC")
                    .field("CUSTEMAIL", "testing@faspay.co.id")
                    .field("DESCRIPTION", "transaski test")
                    .field("RETURN_URL", "http://localhost/creditcard/merchant_return_page.php")
                    .field("SIGNATURE", sign)
                    .field("BILLING_ADDRESS", "Jakarta pusat pintu air raya")
                    .field("BILLING_ADDRESS_CITY", "Jakarta pusat pintu air raya")
                    .field("BILLING_ADDRESS_REGION", "DKI")
                    .field("BILLING_ADDRESS_STATE", "Jakarta pusat")
                    .field("BILLING_ADDRESS_POSCODE", "10710")
                    .field("BILLING_ADDRESS_COUNTRY_CODE", "ID")
                    .field("RECEIVER_NAME_FOR_SHIPPING", "ega")
                    .field("SHIPPING_ADDRESS", "pintu air raya")
                    .field("SHIPPING_ADDRESS_CITY", "Jakarta pusat")
                    .field("SHIPPING_ADDRESS_REGION", "Jakarta pusat")
                    .field("SHIPPING_ADDRESS_STATE", "Jakarta pusat")
                    .field("SHIPPING_ADDRESS_POSCODE", "Jakarta pusat")
                    .field("SHIPPING_ADDRESS_COUNTRY_CODE", "Jakarta pusat")
                    .field("SHIPPINGCOST", "0.00")
                    .field("PHONE_NO", "0897867688989")
                    .field("MREF1", "tes")
                    .field("MREF2", "bossss")
                    .field("MREF3", "Tas;2;3000000")
                    .field("MREF4", "")
                    .field("MREF5", "")
                    .field("MREF6", "")
                    .field("MREF7", "")
                    .field("MREF8", "")
                    .field("MREF9", "")
                    .field("MREF10", "")
                    .field("MPARAM1", "")
                    .field("MPARAM2", "Testing")
                    .field("CUSTOMER_REF", "")
                    .field("PYMT_IND", "")
                    .field("PYMT_CRITERIA", "")
                    .field("PYMT_TOKEN", "")
                    .field("FRISK1", "")
                    .field("FRISK2", "")
                    .field("DOMICILE_ADDRESS", "")
                    .field("DOMICILE_ADDRESS_CITY", "")
                    .field("DOMICILE_ADDRESS_REGION", "")
                    .field("DOMICILE_ADDRESS_STATE", "")
                    .field("DOMICILE_ADDRESS_POSCODE", "")
                    .field("DOMICILE_ADDRESS_COUNTRY_CODE", "")
                    .field("DOMICILE_PHONE_NO", "")
                    .field("handshake_url", "")
                    .field("handshake_param", "")
                    .field("style_merchant_name", "black")
                    .field("style_order_summary", "black")
                    .field("style_order_no", "black")
                    .field("style_order_desc", "black")
                    .field("style_amount", "black")
                    .field("style_background_left", "#fff")
                    .field("style_button_cancel", "grey")
                    .field("style_font_cancel", "white")
                    .asString();

            System.out.println("statusCode = " + response.getStatus());
            System.out.println("body = " + IOUtils.toString(response.getRawBody(), "UTF-8"));
//            int status = response.getStatus();
//            boolean redirect = false;
//            if (status != HttpURLConnection.HTTP_OK) {
//                if (status == HttpURLConnection.HTTP_MOVED_TEMP
//                        || status == HttpURLConnection.HTTP_MOVED_PERM
//                        || status == HttpURLConnection.HTTP_SEE_OTHER)
//                    redirect = true;
//            }

//            System.out.println("Response Code ... " + status);
//
//            if (redirect) {
            String newUrl = response.getHeaders().getFirst("Location");
            ;
//                Headers headers = response.getHeaders();
//                for (String header : headers.keySet()) {
//                    for (String headerValue : headers.get(header)) {
//                        System.out.println("key : " + header);
//                        System.out.println("value : " + headerValue);
//                    }
//                }
//                System.out.println(newUrl);
//
//                String cookie = response.getHeaders().getFirst("Set-Cookie");;
//                System.out.println(cookie);
//
//                response = client.get(newUrl)
//                        .header("Cookie", cookie)
//                        .header("Accept-Language", "en-US,en;q=0.8")
//                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36")
//                        .asString();
//                System.out.println("statusCode = " + response.getStatus());
//                System.out.println("body = " + IOUtils.toString(response.getRawBody(), "UTF-8"));
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void tesEmailAms() {
        //tes email
        Map<String, String> map = new HashMap<>();
        map.put("Tipe", "sendRegisterAgentAndroid");
        map.put("idAgent", "13123");
        map.put("idAgentAccount", "94375983934");
        map.put("VarNama", "tes");
        map.put("VarHP", "0823423423");
        map.put("VarEmail", "acang.torvalds@gmail.com");
        map.put("VarKTP", "werewrww");

        new SendEmailAMSAgentReg().sent(map);
    }


    public void tesEncryptDecrypt() {
        //encrypt dec
        try {
            //System.out.println(this.controllerEncDec.decryptDbVal("I2v3S5H91p1gn9M7OeoIWw==", Constant.KUNCI));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tesSms() {
        //test sms
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        new SmsHelper().sent(uuid, "082190764546", "tes sms tm", "INFO_REGISTRASI", this.entityConfig);
    }

    public void tesEncrypt() {
        try {
            byte[] fileContent = FileUtils.readFileToByteArray(new File("D:\\tmp\\ImagesAndWords.jpg"));
            String encodedStringImage = new String(Base64.encodeBase64(fileContent));

            String enc = new String(Base64.encodeBase64(controllerEncDec.getEncrypt("{\n" +
                    "\t\"signature\": \"SIGNATURE\",\n" +
                    "\t\"initiator\": \"USERNAME\",\n" +
                    "\t\"ext_ref\": \"1234567890\",\n" +
                    "\t\"customer_type\": \"MEMBER\",\n" +
                    "\t\"customer_phone_number\": \"0812345678 9\",\n" +
                    "\t\"customer_country_code\": \"IDN\",\n" +
                    "\t\"customer_province\": \"1\",\n" +
                    "\t\"customer_city\": \"1\",\n" +
                    "\t\"customer_postal_code\": \"123456\",\n" +
                    "\t\"customer_address\": \"STREET NAME\",\n" +
                    "\t\"customer_currency\": \"IDR\",\n" +
                    "\t\"customer_national_id_type\": \"1\",\n" +
                    "\t\"customer_national_id\": \"12345678901 23456\",\n" +
                    "\t\"customer_name\": \"CUSTOMER NAME\",\n" +
                    "\t\"customer_gender\": \"M\",\n" +
                    "\t\"customer_email\": \"EMAIL@EMAIL.COM\",\n" +
                    "\t\"customer_dob\": \"2019-01-31\",\n" +
                    "\t\"customer_pob\": \"JAKARTA\",\n" +
                    "\t\"customer_mother_name\": \"MOTHER NAME\",\n" +
                    "\t\"customer_pin\": \"8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92\",\n" +
                    "\t\"customer_photo_national_id_base64\": \"" + encodedStringImage + "\",\n" +
                    "\t\"customer_photo_profil_base64\": \"" + encodedStringImage + "\"\n" +
                    "}")));

//            System.out.println(enc);

            byte[] decodedBytes = Base64.decodeBase64(enc);
            String dataEncStr = controllerEncDec.getDecrypt(decodedBytes);

//            System.out.println(dataEncStr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tesDecryptPgCrypto() {
        try {
            System.out.println(new String(Base64.encodeBase64(controllerEncDec.getEncrypt("data whatever to encrypt"))));

            byte[] decodedBytes = Base64.decodeBase64("wes8z3RcRoqz/2D5r/68D+lg8RCzJ1HhAboRCaUR6Y0=");
            //----------------------parsing the json message-------------------------------
            String queryJson = controllerEncDec.getDecrypt(decodedBytes);
            System.out.println(queryJson);
        } catch (Exception ex) {
            ex.printStackTrace();
            String tes = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "<style>\n" +
                    "#container {\n" +
                    "  padding: 10px;\n" +
                    "}" +
                    "table {\n" +
                    "  font-family: arial;\n" +
                    "  border-collapse: collapse;\n" +
                    "  width: 100%;\n" +
                    "}\n" +
                    "\n" +
                    "td, th {\n" +
                    "  border: 1px solid #333333;\n" +
                    "  text-align: left;\n" +
                    "  padding: 8px;\n" +
                    "}\n" +
                    "</style/\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div id=\"container\">" +
                    "<p>Tipe Transaksi : Tunai Ke Tunai</p>\n" +
                    "<table>\n" +
                    "  <tr>\n" +
                    "    <th>Cara Pembayaran</th>\n" +
                    "    <th>Cara Pencairan</th>\n" +
                    "    <th>Biaya</th>\n" +
                    "  </tr>\n" +
                    "  <tr>\n" +
                    "    <td>LOVE Counter</td>\n" +
                    "    <td>LOVE Counter</td>\n" +
                    "    <td>Rp7.500</td>\n" +
                    "  </tr>\n" +
                    "  <tr>\n" +
                    "    <td>Indomaret</td>\n" +
                    "    <td>LOVE Counter</td>\n" +
                    "    <td>Rp7.500</td>\n" +
                    "  </tr>\n" +
                    "</table>\n" +
                    "\n" +
                    "<p>Tipe Transaksi : Tunai Ke Bank</p>\n" +
                    "<table>\n" +
                    "  <tr>\n" +
                    "    <th>Cara Pembayaran</th>\n" +
                    "    <th>Cara Pencairan</th>\n" +
                    "    <th>Biaya</th>\n" +
                    "  </tr>\n" +
                    "  <tr>\n" +
                    "    <td>LOVE Counter</td>\n" +
                    "    <td>Ke Rekening Bank</td>\n" +
                    "    <td>Rp6.500</td>\n" +
                    "  </tr>\n" +
                    "  <tr>\n" +
                    "    <td>Indomaret</td>\n" +
                    "    <td>Ke Rekening Bank</td>\n" +
                    "    <td>Rp11.000</td>\n" +
                    "  </tr>\n" +
                    "</table>\n" +
                    "</div>" +
                    "\n" +
                    "</body>\n" +
                    "</html>";
        }
    }

    public void testDecrypt(){
        try {
            byte[] decodedBytes = Base64.decodeBase64("hPtiXhD+Ro4BeFjEBIvrBw==");
            String mtcc = controllerEncDec.getDecrypt(decodedBytes);
            System.out.println("mtcn : " + mtcc);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
