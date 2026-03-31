/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
 * @author hasan
 */
public class DatabaseUtilitiesSqlite {

    private Connection conn;
//    private ControllerLog ctrlLog;

    public Connection getConnection(EntityConfig conf) throws ParserConfigurationException {
        if (conn == null) {
            try {
                //--------------------------MySql------------------------------
//                DriverManager.registerDriver(new org.sqlite.JDBC());
                String db = conf.getDbSqliteFilePath();
                String usr = conf.getDbSqliteUser();
                String pwd = conf.getDbSqlitePassword();
                conn = DriverManager.getConnection("jdbc:sqlite:" + db);
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseUtilitiesSqlite.class.getName()).log(Level.SEVERE, null, ex);
//                this.ctrlLog.logErrorWriter(ex.toString());
//                JOptionPane.showMessageDialog(null, "\nGagal Koneksi Database\n" + ex, "Kesalahan", JOptionPane.ERROR_MESSAGE);
//                System.exit(1);
            }
        }
        return conn;
    }
}
