/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package koneksi;

import entity.EntityConfig;

import javax.xml.parsers.ParserConfigurationException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acang
 */
public class DatabaseUtilitiesPgsqlSession {

    private Connection conn;

    public Connection getConnection(EntityConfig conf) throws ParserConfigurationException {
        if (conn == null) {
            try {
                //--------------------------PgSql------------------------------
                DriverManager.registerDriver(new org.postgresql.Driver());
                String ip = conf.getDbPgsqlIpSession();
                String port = conf.getDbPgsqlPortSession();
                String db = conf.getDbPgsqlSession();
                String usr = conf.getDbPgsqlUserSession();
                String pwd = conf.getDbPgsqlPassSession();
                //conn=DriverManager.getConnection("jdbc:postgresql://10.1.15.120:5432/sijungjung","postgres","");
                conn = DriverManager.getConnection("jdbc:postgresql://" + ip + ":" + port + "/" + db, usr, pwd);
            } catch (SQLException ex) {
                //JOptionPane.showMessageDialog(null,"\nGagal Koneksi Database\n" +ex,"Kesalahan",JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(DatabaseUtilitiesPgsqlSession.class.getName()).log(Level.SEVERE, null, ex);
                //System.exit(1);
            }
        }
        return conn;
    }
}
