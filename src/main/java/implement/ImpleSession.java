/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package implement;

import com.google.common.base.Throwables;
import configuration.ControllerLog;
import configuration.DependencyConfig;
import entity.EntityConfig;
import interfc.InterfcSession;
import koneksi.DatabaseUtilitiesSqlite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.xml.parsers.ParserConfigurationException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Hasan
 */
@Service
@Scope("prototype")
public class ImpleSession implements InterfcSession {

    private final EntityConfig entityConfig;
    private final ControllerLog controllerLog;

    @Autowired
    public ImpleSession(DependencyConfig dependencyConfig) {
        this.entityConfig = dependencyConfig.getEntityConfig();
        this.controllerLog = dependencyConfig.getControllerLog();
    }

    @Override
    public HashMap setCookie(HashMap<String, Object> hashObj) {
        DatabaseUtilitiesSqlite databaseUtilitiesSqlite = new DatabaseUtilitiesSqlite();
        Connection connSqlite = null;
        PreparedStatement stSqlite = null;
        ResultSet rsSqlite = null;
        long idResultTmp = 0;
        HashMap<String, String> objColumn = new HashMap<>();
        try {
            connSqlite = databaseUtilitiesSqlite.getConnection(this.entityConfig);
            String query = "INSERT INTO" +
                    " sessions(" +
                    "id, " + //1
                    "data, " + //2
                    "created, " +
                    "expires)" + //3
                    " VALUES (?, ?, datetime('now'), ?);";
            stSqlite = connSqlite.prepareStatement(query);
            stSqlite.setString(1, hashObj.get("cookie").toString());
            stSqlite.setString(2, hashObj.toString());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
            Date parsedDate = dateFormat.parse(hashObj.get("expireDate").toString());
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            stSqlite.setString(3, timestamp.toString());
            stSqlite.executeUpdate();

            query = "select id as session_id, created, modified, data as session_value, expires as timestamp_expired from sessions where id = ?;";
            stSqlite = connSqlite.prepareStatement(query);
            stSqlite.setString(1, hashObj.get("cookie").toString());
            rsSqlite = stSqlite.executeQuery();
            ResultSetMetaData metaData = rsSqlite.getMetaData();
            int colCount = metaData.getColumnCount();
            //convert resultset to jsonObject
            while (rsSqlite.next()) {
                for (int i = 1; i <= colCount; i++) {
                    if (rsSqlite.getObject(i) == null) {
                        objColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        objColumn.put(metaData.getColumnLabel(i), rsSqlite.getString(i));
                    }
                }
            }
        } catch (ParserConfigurationException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } catch (SQLException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } catch (ParseException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } finally {
            try {
                if (rsSqlite != null) {
                    rsSqlite.close();
                }
                if (stSqlite != null) {
                    stSqlite.close();
                }
                if (connSqlite != null) {
                    connSqlite.close();
                }
            } catch (SQLException ex) {
                String s = Throwables.getStackTraceAsString(ex);
                controllerLog.logErrorWriter(s);
            }
        }
        return objColumn;
    }

    @Override
    public HashMap getCookie(HashMap<String, Object> hashObj) {
        DatabaseUtilitiesSqlite databaseUtilitiesSqlite = new DatabaseUtilitiesSqlite();
        Connection connSqlite = null;
        PreparedStatement stSqlite = null;
        ResultSet rsSqlite = null;
        HashMap<String, String>  objColumn = new HashMap<>();
        try {
            connSqlite = databaseUtilitiesSqlite.getConnection(entityConfig);
            String query = "select id as session_id, created, modified, data as session_value, expires as timestamp_expired from sessions where id = ?;";
            stSqlite = connSqlite.prepareStatement(query);
            stSqlite.setString(1, hashObj.get("cookie").toString());
            rsSqlite = stSqlite.executeQuery();

            ResultSetMetaData metaData = rsSqlite.getMetaData();
            int colCount = metaData.getColumnCount();
            //convert resultset to jsonArray
            int j = 0;
            while (rsSqlite.next()) {
                for (int i = 1; i <= colCount; i++) {
                    if (rsSqlite.getObject(i) == null) {
                        objColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        objColumn.put(metaData.getColumnLabel(i), rsSqlite.getString(i));
                    }
                }
            }
        } catch (Exception ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } finally {
            try {
                if (rsSqlite != null) {
                    rsSqlite.close();
                }
                if (stSqlite != null) {
                    stSqlite.close();
                }
                if (connSqlite != null) {
                    connSqlite.close();
                }
            } catch (SQLException ex) {
                String s = Throwables.getStackTraceAsString(ex);
                controllerLog.logErrorWriter(s);
            }
        }
        return objColumn;
    }

    @Override
    public HashMap updateCookie(HashMap<String, Object> hashObj) {
//        for(Map.Entry<String, String> entry : hashObj.entrySet()){
//            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
//        }
//        System.out.println("hashObj.get(\"expireDate\") " + hashObj.containsKey("expireDate"));
//        System.out.println("hashObj.get(\"cookie\") " + hashObj.containsKey("cookie"));
//        System.out.println("hashObj.get(\"objAccount\") " + hashObj.containsKey("objAccount"));
//        System.out.println("hashObj.get(\"maxAge\") " + hashObj.containsKey("maxAge"));
        DatabaseUtilitiesSqlite databaseUtilitiesSqlite = new DatabaseUtilitiesSqlite();
        Connection connSqlite = null;
        PreparedStatement stSqlite = null;
        ResultSet rsSqlite = null;
        long idResultTmp = 0;
        HashMap<String, String> objColumn = new HashMap<>();
        try {
            connSqlite = databaseUtilitiesSqlite.getConnection(this.entityConfig);
            String query = "UPDATE \"main\".\"sessions\" SET " +
                    "\"data\" = ?, " +
                    "\"created\" = datetime('now'), " +
                    "\"expires\" = ?, " +
                    "\"modified\" = NULL " +
                    "WHERE \"id\" = ?;";
            stSqlite = connSqlite.prepareStatement(query);
            stSqlite.setString(1, hashObj.toString());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
            Date parsedDate = dateFormat.parse(hashObj.get("expireDate").toString());
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            stSqlite.setString(2, timestamp.toString());
            stSqlite.setString(3, hashObj.get("cookie").toString());
            stSqlite.executeUpdate();

            query = "select id as session_id, created, modified, data as session_value, expires as timestamp_expired from sessions where id = ?;";
            stSqlite = connSqlite.prepareStatement(query);
            stSqlite.setString(1, hashObj.get("cookie").toString());
            rsSqlite = stSqlite.executeQuery();
            ResultSetMetaData metaData = rsSqlite.getMetaData();
            int colCount = metaData.getColumnCount();
            //convert resultset to jsonObject
            while (rsSqlite.next()) {
                for (int i = 1; i <= colCount; i++) {
                    if (rsSqlite.getObject(i) == null) {
                        objColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        objColumn.put(metaData.getColumnLabel(i), rsSqlite.getString(i));
                    }
                }
            }
        } catch (ParserConfigurationException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } catch (SQLException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } catch (ParseException ex) {
            String s = Throwables.getStackTraceAsString(ex);
            controllerLog.logErrorWriter(s);
        } finally {
            try {
                if (rsSqlite != null) {
                    rsSqlite.close();
                }
                if (stSqlite != null) {
                    stSqlite.close();
                }
                if (connSqlite != null) {
                    connSqlite.close();
                }
            } catch (SQLException ex) {
                String s = Throwables.getStackTraceAsString(ex);
                controllerLog.logErrorWriter(s);
            }
        }
        return objColumn;
    }
}
