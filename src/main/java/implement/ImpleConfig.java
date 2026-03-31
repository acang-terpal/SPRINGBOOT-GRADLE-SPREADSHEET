/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implement;

import entity.EntityConfig;
import interfc.InterfcConfig;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author hasan
 */
public class ImpleConfig implements InterfcConfig {

    public ImpleConfig() {
    }

    @Override
    public EntityConfig getData(EntityConfig conf, String workDir) {
        try {
            File file = new File(workDir + File.separator + "config" + File.separator + "config.xml");
//            File file = new File("/etc/love-id-internal-baseapi-remit-v2/config/config.xml");
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            SAXBuilder saxBuilder = new SAXBuilder();
            org.jdom2.Document doc = saxBuilder.build(file);
            Element rootNode = doc.getRootElement();

            //Get file from resources folder
//            ClassLoader classLoader = getClass().getClassLoader();
//            String configString = IOUtils.toString(classLoader.getResourceAsStream("config.xml"));
//            SAXBuilder saxBuilder = new SAXBuilder();
//            org.jdom2.Document doc = saxBuilder.build(new ByteArrayInputStream(configString.getBytes("UTF-8")));
//            Element rootNode = doc.getRootElement();

            conf.setWorkDir(workDir);

            //populate commonServer config
            List list = rootNode.getChildren("commonServer");
            for (Object objectCommonServer : list) {
                Element node = (Element) objectCommonServer;
                conf.setHttpPort(node.getChildText("portAdapter"));
                conf.setMaxThreads(node.getChildText("maxThreads"));
                conf.setMinThreads(node.getChildText("minThreads"));
                conf.setLowThreads(node.getChildText("lowThreads"));
                conf.setMaxQueued(node.getChildText("maxQueued"));
                conf.setMaxTotalConnections(node.getChildText("maxTotalConnections"));
                conf.setMaxIoIdleTimeMs(node.getChildText("maxIoIdleTimeMs"));
                conf.setPathUri(node.getChildText("pathUri"));
                conf.setSessionTtl(node.getChildText("sessionTtl"));
                conf.setTimeRemoveBeforeSessionDelete(node.getChildText("timeRemoveBeforeSessionDelete"));
            }
            //populate database pgsql cecenet config
            list = rootNode.getChildren("databasePgsqlCecenet");
            for (Object objectDatabase : list) {
                Element node = (Element) objectDatabase;
                conf.setDbPgsqlCecenet(node.getChildText("database"));
                conf.setDbPgsqlIpCecenet(node.getChildText("ip"));
                conf.setDbPgsqlPortCecenet(node.getChildText("port"));
                conf.setDbPgsqlUserCecenet(node.getChildText("user"));
                conf.setDbPgsqlPassCecenet(node.getChildText("password"));
            }
            //populate database pgsql mamen config
            list = rootNode.getChildren("databasePgsqlMamen");
            for (Object objectDatabase : list) {
                Element node = (Element) objectDatabase;
                conf.setDbPgsqlMamen(node.getChildText("database"));
                conf.setDbPgsqlIpMamen(node.getChildText("ip"));
                conf.setDbPgsqlPortMamen(node.getChildText("port"));
                conf.setDbPgsqlUserMamen(node.getChildText("user"));
                conf.setDbPgsqlPassMamen(node.getChildText("password"));
            }
            //populate database pgsql session config
            list = rootNode.getChildren("databasePgsqlSession");
            for (Object objectDatabase : list) {
                Element node = (Element) objectDatabase;
                conf.setDbPgsqlSession(node.getChildText("database"));
                conf.setDbPgsqlIpSession(node.getChildText("ip"));
                conf.setDbPgsqlPortSession(node.getChildText("port"));
                conf.setDbPgsqlUserSession(node.getChildText("user"));
                conf.setDbPgsqlPassSession(node.getChildText("password"));
            }
            //populate database mysql config
            list = rootNode.getChildren("databaseMysql");
            for (Object objectDatabase : list) {
                Element node = (Element) objectDatabase;
                conf.setDbMysql(node.getChildText("database"));
                conf.setDbMysqlIp(node.getChildText("ip"));
                conf.setDbMysqlPort(node.getChildText("port"));
                conf.setDbMysqlUser(node.getChildText("user"));
                conf.setDbMysqlPass(node.getChildText("password"));
            }
            //populate database sqlite config
            list = rootNode.getChildren("databaseSqlite");
            for (Object objectDatabase : list) {
                Element node = (Element) objectDatabase;
                conf.setDbSqliteFilePath(node.getChildText("dbFilePath"));
                conf.setDbSqliteUser(node.getChildText("user"));
                conf.setDbSqlitePassword(node.getChildText("password"));
            }
            //populate ftp server config
            list = rootNode.getChildren("ftpServerInventory");
            for (Object objectDatabase : list) {
                Element node = (Element) objectDatabase;
                conf.setFtpIp(node.getChildText("ip"));
                conf.setFtpPort(node.getChildText("port"));
                conf.setFtpUser(node.getChildText("user"));
                conf.setFtpPass(node.getChildText("password"));
                conf.setFtpPath(node.getChildText("path"));
            }
            //populate encryption config
            list = rootNode.getChildren("encryption");
            for (Object objectXMPP : list) {
                Element node = (Element) objectXMPP;
                conf.setEncKeyDb(node.getChildText("keyDb"));
                conf.setEncIvDb(node.getChildText("ivDb"));
                conf.setEncKey(node.getChildText("key"));
                conf.setEncIv(node.getChildText("iv"));
                conf.setEncRequest(node.getChildText("encRequest"));
                conf.setEncResponse(node.getChildText("encResponse"));
            }
            //populate misc config
            list = rootNode.getChildren("misc");
            for (Object objectXMPP : list) {
                Element node = (Element) objectXMPP;
                conf.setScaleImgSize(node.getChildText("scaleImgSize"));
                conf.setSetConnectTimeout(node.getChildText("setConnectTimeout"));
                conf.setSetReadTimeout(node.getChildText("setReadTimeout"));
                conf.setlogStream(node.getChildText("logStream"));
                conf.setMasterFolder(node.getChildText("masterFolder"));
                conf.setUrlSession(node.getChildText("sessionUrl"));
            }
            //populate base config
            list = rootNode.getChildren("baseApi");
            for (Object objectXMPP : list) {
                Element node = (Element) objectXMPP;
                conf.setUsernamePurchase(node.getChildText("usernamePurchase"));
                conf.setKeyPurchase(node.getChildText("keyPurchase"));
                conf.setUsernamePayment(node.getChildText("usernamePayment"));
                conf.setKeyPayment(node.getChildText("keyPayment"));
            }
        } catch (JDOMException | IOException e) {
            System.out.println(e);
        }
        return conf;
    }

}
