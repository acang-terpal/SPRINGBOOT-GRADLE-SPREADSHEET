package helper;

import entity.EntityConfig;
import koneksi.DatabaseUtilitiesPgsqlCecenet;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SendEmailAMSAgentReg {

    public void sent(Map<String, String> map) {
        try {
            SendEmailAMSThread gcm = new SendEmailAMSThread(map);
            Thread t = new Thread(gcm);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("idAgent", "479375634");
        map.put("idAgentAccount", "546757456");
        map.put("VarNama", "JUMINTEN");
        map.put("VarHP", "5456746");
        map.put("VarEmail", "TESTING@TESTING.COM");
        map.put("VarKTP", "54647665656");

        new SendEmailAMSAgentReg().sent(map);
    }
}

class SendEmailAMSThread implements Runnable {

    static SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
    private Map<String, String> map;
    private Cache cacheConfig;
    private EntityConfig entityConfig;

    public SendEmailAMSThread(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public void run() {
        CacheManager cacheManager = CacheManager.getInstance();
        this.cacheConfig = cacheManager.getCache(" SpreadSheetCacheAppApp");
        Element config = this.cacheConfig.get("config");
        this.entityConfig = (EntityConfig) config.getObjectValue();

        DatabaseUtilitiesPgsqlCecenet databaseUtilitiesPgsql = new DatabaseUtilitiesPgsqlCecenet();
        Connection connPgsql = null;
        PreparedStatement stPgsql = null;
        ResultSet rsPgsql = null;

        String emailAMS = null;
        String senderuser = null;
        String senderpassword = null;

        try {
            String port = null;
            String host = null;
            connPgsql = databaseUtilitiesPgsql.getConnection(entityConfig);

            String sql = "SELECT * FROM tb_mailserver WHERE keyword = 'NOREPLY'"
                    + " AND flagactive = TRUE ORDER BY \"timestamp\" DESC LIMIT 1";

            PreparedStatement ps = connPgsql.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                senderuser = rs.getString("username");
                senderpassword = rs.getString("password");
                port = rs.getString("port");
                host = rs.getString("host");
            }

            final String username = senderuser;
            final String password = senderpassword;

            sql = "SELECT mail_address FROM tb_mailgroup WHERE keyword = 'AMS'"
                    + " AND flagactive = TRUE AND \"upper\"(program_name) = 'AGENT_REGISTRATION' ORDER BY"
                    + "	\"timestamp\" DESC LIMIT 1";

            ps = connPgsql.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                emailAMS = rs.getString("mail_address");
            }

            if (emailAMS != null && !emailAMS.isEmpty()) {
                Properties props = new Properties();

                props.put("mail.smtp.port", port);
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.starttls.required", "true");
                props.put("mail.smtp.host", host);

                Session session = Session.getInstance(props,
                        new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });

                // Create a default MimeMessage object.
                Message message = new MimeMessage(session);

                try {
                    message.setFrom(new InternetAddress(senderuser, "Info TrueMoney"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                // Set To: header field of the header.
                InternetAddress[] toArray = InternetAddress.parse(emailAMS);
                message.setRecipients(Message.RecipientType.TO, toArray);

                sql = "SELECT wording_content FROM \"MsWordingContent\" WHERE wording_content_name = 'NOTIF_AGENT_REGISTRATION_AMS'";

                ps = connPgsql.prepareStatement(sql);
                rs = ps.executeQuery();

                String pesan = null;

                while (rs.next()) {
                    pesan = rs.getString("wording_content");
                }

                String idAgent = map.get("idAgent").toString();
                String idAgentAcc = map.get("idAgentAccount").toString();
                String nama = map.get("VarNama").toString();
                String handphone = map.get("VarHP").toString();
                String ktp = map.get("VarKTP").toString();
                String email = map.get("VarEmail").toString();

                String pesan_new = String.format(pesan, idAgent, idAgentAcc, format.format(new Date()).toString(),
                        nama, "INACTIVE", ktp, handphone, email);

                // Set Subject: header field
                message.setSubject("[New TM Agent Registration Notification] ID Agent: " + map.get("idAgent"));

                // Create the message part
                BodyPart messageBodyPart = new MimeBodyPart();

                // Create a multipar message
                Multipart multipart = new MimeMultipart();

                // Set text message part
                multipart.addBodyPart(messageBodyPart);

                // Initialize velocity
                VelocityEngine ve = new VelocityEngine();
                ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
                ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
                ve.init();

                /* next, get the Template */
                Template t = ve.getTemplate("mail-template.vm");

                /* create a context and add data */
                VelocityContext context1 = new VelocityContext();
                context1.put("headerTemplate", "New TM Agent Registration Notification");
                context1.put("bodyTemplate", pesan_new);

                /* now render the template into a StringWriter */
                StringWriter out = new StringWriter();
                t.merge(context1, out);

                messageBodyPart.setContent(out.toString(), "text/html");

                // Part two is attachment
                messageBodyPart = new MimeBodyPart();

                // Send the complete message parts
                message.setContent(multipart);

                // Send message
                Transport.send(message);
            }

        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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
                ex.printStackTrace();
            }
        }

    }

}
