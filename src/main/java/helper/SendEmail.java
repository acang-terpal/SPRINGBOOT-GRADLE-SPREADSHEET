package helper;

import configuration.ControllerLog;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.json.simple.parser.ParseException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Properties;

public class SendEmail {

    public void send(HashMap objData, Utilities utilities, ControllerLog controllerLog) {
        try {
            SendEmailThread gcm = new SendEmailThread(objData, utilities, controllerLog);
            Thread t = new Thread(gcm);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class SendEmailThread implements Runnable {

    private HashMap objData;
    private Utilities utilities;
    private ControllerLog controllerLog;

    public SendEmailThread(HashMap objData, Utilities utilities, ControllerLog controllerLog) throws ParseException {
        this.objData = objData;
        this.utilities = utilities;
        this.controllerLog = controllerLog;
    }

    @Override
    public void run() {
        this.directEmail();
    }

    public void directEmail() {
        try {
            String from = "[Do Not Reply] MAMEN";
            String subject = "";
            String header = "";
            String emailBody = "";

            final String emailSender = "no_reply@ngadi-ngadi.my.id";
//            final String emailReceiver = "acang.torvalds@gmail.com, muhammad.hasan.mustofa@gmail.com, winda.l@inception.asia, fakhri.a@inception.asia, ario.jatmiko@inception.asia";
            final String emailReceiver = this.objData.get("account_email").toString();
            final String password = "d4mnDud3";

            Properties props = new Properties();
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2"); // Added this line
            props.put("mail.smtp.ssl.trust", "*");
            //props.put("mail.debug", "true");
            props.put("mail.smtp.host", "mail.ngadi-ngadi.my.id");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailSender, password);
                }
            });

            // Create a default MimeMessage object.
            Message message = new MimeMessage(session);

            // Set From: header field of the header.
            try {
                message.setFrom(new InternetAddress(emailSender, from));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // Set To: header field of the header.
            InternetAddress[] toArray = InternetAddress.parse(emailReceiver);
            message.setRecipients(Message.RecipientType.TO, toArray);

            //set email content
            subject = "[Mamen Notification] " + objData.get("title").toString();
            header = "[Mamen Notification] " + objData.get("title").toString();
            emailBody = "<b>" + objData.get("title").toString() + "</b>" +
                    "<br>Account Name : " + objData.get("account_name").toString() +
                    "<br>Email : " + objData.get("account_email").toString() +
                    "<br>link : <a href=\"" + objData.get("link").toString() + "\">" + objData.get("link").toString() + "</a>" +
                    "<br>"  + objData.get("footer").toString() +
                    "<br>";

            // Set Subject: header field
            message.setSubject(subject);

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Initialize velocity
//            VelocityEngine ve = new VelocityEngine();
//            ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
//            ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
//            ve.init();

            VelocityEngine ve = new VelocityEngine();
            ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
            ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity.log");
            ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            ve.init();

            /* next, get the Template */
            Template t = ve.getTemplate("mail-template.vm");

            /* create a context and add data */
            VelocityContext context1 = new VelocityContext();
            context1.put("headerTemplate", header);
            context1.put("bodyTemplate", emailBody);

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

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public String getHtmlTemplate() {
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> " +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\"> " +
                "<head> " +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /> " +
                "<title>Demystifying Email Design</title> " +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/> " +
                "</head> " +
                "<body style=\"margin: 0; padding: 0;\"> " +
                " <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">  " +
                "  <tr> " +
                "   <td style=\"padding: 10px 0 30px 0;\"> " +
                "    <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border: 1px solid #cccccc; border-collapse: collapse;\"> " +
                "     <tr> " +
                "      <td   style=\"padding: 10px 10px 10px 10px;\"> " +
                " " +
                "          " +
                "      </td> " +
                "     </tr> " +
                "     <tr> " +
                "      <td bgcolor=\"#ffffff\" style=\"padding: 40px 30px 40px 30px;\"> " +
                "       <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"> " +
                "        <tr> " +
                "         <td align=\"center\" style=\"color: #444; font-family: Arial, sans-serif; font-size: 20px;\"> " +
                "          <b>$headerTemplate</b> " +
                "         </td> " +
                "        </tr> " +
                "        <tr> " +
                "         <td align=\"center\" > " +
                "          <b><hr color=\"gray\" /></b> " +
                "         </td> " +
                "        </tr> " +
                "        <tr> " +
                "         <td  align=\"justify\" style=\"padding: 20px 0 30px 0; color: #444; font-family: Arial, sans-serif; font-size: 16px; line-height: 20px;\"> " +
                "          $bodyTemplate " +
                "         </td> " +
                "        </tr> " +
                "       </table> " +
                "      </td> " +
                "     </tr> " +
                "     <tr> " +
                "      <td align=\"justify\" style=\"padding: 0px 30px 0px 30px; size:1em; color: gray; font-family: Arial, sans-serif; font-size: 10px; line-height: 10px;\">Note : This is an automated mail. Do not reply to this mail</td> " +
                "     </tr> " +
                "     <tr> " +
                "      <td bgcolor=\"#cc0000\" style=\"padding: 10px 10px 10px 10px;\"> " +
                "      </td> " +
                "     </tr> " +
                "    </table> " +
                "   </td> " +
                "  </tr> " +
                " </table> " +
                "</body> " +
                "</html>";
    }
}
