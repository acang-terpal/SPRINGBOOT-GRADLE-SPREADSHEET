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
import helper.Helper;
import helper.Utilities;
import interfc.InterfcSpreadSheetLogin;
import koneksi.DatabaseUtilitiesPgsqlMamen;
import koneksi.DatabaseUtilitiesSqlite;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.xml.parsers.ParserConfigurationException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Hasan
 */
@Service
@Scope("prototype")
public class ImpleSpreadSheetLogin implements InterfcSpreadSheetLogin {

    EntityConfig entityConfig;
    Helper hlp;
    private final Utilities utilities;
    private final ControllerLog controllerLog;

    @Autowired
    public ImpleSpreadSheetLogin(DependencyConfig dependencyConfig, Utilities utilities, Helper helper) {
        this.entityConfig = dependencyConfig.getEntityConfig();
        this.controllerLog = dependencyConfig.getControllerLog();
        this.utilities = utilities;
        this.hlp = helper;
    }

    @Override
    public List getAccount(HashMap objParams) {
        DatabaseUtilitiesSqlite databaseUtilitiesSqlite = new DatabaseUtilitiesSqlite();
        Connection connSqlite = null;
        PreparedStatement stSqlite = null;
        ResultSet rsSqlite = null;
        List listRow = new ArrayList();
        try {
            connSqlite = databaseUtilitiesSqlite.getConnection(entityConfig);
            String query = "SELECT   " +
                    " *    " +
                    "FROM   " +
                    " ms_account A " +
                    "WHERE   " +
                    " A.username = ?  " +
                    " AND A.password = ?;";
            stSqlite = connSqlite.prepareStatement(query);
            stSqlite.setString(1, objParams.get("login_username").toString());
            stSqlite.setString(2, objParams.get("account_password").toString());
            rsSqlite = stSqlite.executeQuery();
            ResultSetMetaData metaData = rsSqlite.getMetaData();
            int colCount = metaData.getColumnCount();
            //convert resultset to jsonArray
            int j = 0;
            while (rsSqlite.next()) {
                HashMap<String, String> objColumn = new HashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    if (rsSqlite.getObject(i) == null) {
                        objColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        objColumn.put(metaData.getColumnLabel(i), rsSqlite.getString(i));
                    }
                }
                listRow.add(j, objColumn);
                j = j++;
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
        return listRow;
    }

    @Override
    public List getGroup(String groupId) {
        DatabaseUtilitiesPgsqlMamen databaseUtilitiesPgsql = new DatabaseUtilitiesPgsqlMamen();
        Connection connPgsql = null;
        PreparedStatement stPgsql = null;
        ResultSet rsPgsql = null;
        List listRow = new ArrayList();
        try {
            connPgsql = databaseUtilitiesPgsql.getConnection(entityConfig);
            String query = "SELECT  " +
                    " *   " +
                    "FROM  " +
                    " ms_group a " +
                    "WHERE  " +
                    " a.group_id = ?; ";
            stPgsql = connPgsql.prepareStatement(query);
            stPgsql.setString(1, groupId);
            rsPgsql = stPgsql.executeQuery();
            ResultSetMetaData metaData = rsPgsql.getMetaData();
            int colCount = metaData.getColumnCount();
            //convert resultset to jsonArray
            int j = 0;
            while (rsPgsql.next()) {
                HashMap<String, String> objColumn = new HashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    if (rsPgsql.getObject(i) == null) {
                        objColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        objColumn.put(metaData.getColumnLabel(i), rsPgsql.getString(i));
                    }
                }
                listRow.add(j, objColumn);
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
        return listRow;
    }

    @Override
    public HashMap insertGroup(HashMap objParam) {
        DatabaseUtilitiesPgsqlMamen databaseUtilitiesPgsqlRemit = new DatabaseUtilitiesPgsqlMamen();
        Connection connPgsql = null;
        PreparedStatement stPgsql = null;
        ResultSet rsPgsql = null;
        long idResultTmp = 0;
        HashMap objColumn = new JSONObject();
        try {
            connPgsql = databaseUtilitiesPgsqlRemit.getConnection(this.entityConfig);
            String query = "INSERT INTO \"public\".\"ms_group\"( \"group_id\", \"account_id\", \"level_id\", \"flag_active\") " +
                    "VALUES (?, ?, ?, true) RETURNING *;";
            stPgsql = connPgsql.prepareStatement(query);
            stPgsql.setString(1, objParam.get("group_id").toString());
            stPgsql.setLong(2, Long.parseLong(objParam.get("account_id").toString()));
            stPgsql.setLong(3, Long.parseLong(objParam.get("level_id").toString()));
            rsPgsql = stPgsql.executeQuery();
            ResultSetMetaData metaData = rsPgsql.getMetaData();
            int colCount = metaData.getColumnCount();
            //convert resultset to jsonObject
            while (rsPgsql.next()) {
                for (int i = 1; i <= colCount; i++) {
                    if (rsPgsql.getObject(i) == null) {
                        objColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        objColumn.put(metaData.getColumnLabel(i), rsPgsql.getString(i));
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
        return objColumn;
    }

    @Override
    public List getAccountRegister(HashMap objParams) {
        DatabaseUtilitiesPgsqlMamen databaseUtilitiesPgsql = new DatabaseUtilitiesPgsqlMamen();
        Connection connPgsql = null;
        PreparedStatement stPgsql = null;
        ResultSet rsPgsql = null;
        List listRow = new ArrayList();
        try {
            connPgsql = databaseUtilitiesPgsql.getConnection(entityConfig);
            String query = "SELECT  " +
                    " *   " +
                    "FROM  " +
                    " ms_account A " +
                    "WHERE  " +
                    " A.account_email = ?; ";
            stPgsql = connPgsql.prepareStatement(query);
            stPgsql.setString(1, objParams.get("account_email").toString().trim());
            rsPgsql = stPgsql.executeQuery();
            ResultSetMetaData metaData = rsPgsql.getMetaData();
            int colCount = metaData.getColumnCount();
            //convert resultset to jsonArray
            int j = 0;
            while (rsPgsql.next()) {
                HashMap<String, String> objColumn = new HashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    if (rsPgsql.getObject(i) == null) {
                        objColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        objColumn.put(metaData.getColumnLabel(i), rsPgsql.getString(i));
                    }
                }
                listRow.add(j, objColumn);
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
        return listRow;
    }

    @Override
    public HashMap insertAccount(HashMap objParams) {
        DatabaseUtilitiesPgsqlMamen databaseUtilitiesPgsqlRemit = new DatabaseUtilitiesPgsqlMamen();
        Connection connPgsql = null;
        PreparedStatement stPgsql = null;
        ResultSet rsPgsql = null;
        long idResultTmp = 0;
        HashMap<String, String> objColumn = new HashMap();
        try {
            connPgsql = databaseUtilitiesPgsqlRemit.getConnection(this.entityConfig);
            String query = "INSERT INTO \"public\".\"ms_account\"( \"account_name\", \"account_email\", \"account_password\", \"account_verify\") " +
                    "VALUES (?, ?, ?, ?) RETURNING *;";
            stPgsql = connPgsql.prepareStatement(query);
            stPgsql.setString(1, objParams.get("account_name").toString().trim());
            stPgsql.setString(2, objParams.get("account_email").toString().trim());
            stPgsql.setString(3, objParams.get("account_password").toString());
            stPgsql.setBoolean(4, false);
            rsPgsql = stPgsql.executeQuery();
            ResultSetMetaData metaData = rsPgsql.getMetaData();
            int colCount = metaData.getColumnCount();
            //convert resultset to jsonObject
            while (rsPgsql.next()) {
                for (int i = 1; i <= colCount; i++) {
                    if (rsPgsql.getObject(i) == null) {
                        objColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        objColumn.put(metaData.getColumnLabel(i), rsPgsql.getString(i));
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
        return objColumn;
    }

    @Override
    public HashMap updateAccountVerify(HashMap objCookie) {
        DatabaseUtilitiesPgsqlMamen databaseUtilitiesPgsqlRemit = new DatabaseUtilitiesPgsqlMamen();
        Connection connPgsql = null;
        PreparedStatement stPgsql = null;
        ResultSet rsPgsql = null;
        long idResultTmp = 0;
        HashMap<String, String> objColumn = new HashMap<>();
        try {
            connPgsql = databaseUtilitiesPgsqlRemit.getConnection(this.entityConfig);
            String query = "UPDATE \"public\".\"ms_account\" " +
                    "SET \"account_verify\" = true " +
                    "WHERE \"account_id\" = ? RETURNING *;";
            stPgsql = connPgsql.prepareStatement(query);
            stPgsql.setLong(1, Long.parseLong(objCookie.get("account_id").toString()));
            rsPgsql = stPgsql.executeQuery();
            ResultSetMetaData metaData = rsPgsql.getMetaData();
            int colCount = metaData.getColumnCount();
            //convert resultset to jsonObject
            while (rsPgsql.next()) {
                for (int i = 1; i <= colCount; i++) {
                    if (rsPgsql.getObject(i) == null) {
                        objColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        objColumn.put(metaData.getColumnLabel(i), rsPgsql.getString(i));
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
        return objColumn;
    }

    @Override
    public HashMap updateAccountPassword(HashMap objAccount) {
        DatabaseUtilitiesPgsqlMamen databaseUtilitiesPgsqlRemit = new DatabaseUtilitiesPgsqlMamen();
        Connection connPgsql = null;
        PreparedStatement stPgsql = null;
        ResultSet rsPgsql = null;
        long idResultTmp = 0;
        HashMap<String, String> objColumn = new HashMap<>();
        try {
            connPgsql = databaseUtilitiesPgsqlRemit.getConnection(this.entityConfig);
            String query = "UPDATE \"public\".\"ms_account\" " +
                    "SET \"account_password\" = ? " +
                    "WHERE \"account_id\" = ? RETURNING *;";
            stPgsql = connPgsql.prepareStatement(query);
            stPgsql.setString(1, objAccount.get("account_password").toString());
            stPgsql.setLong(2, Long.parseLong(objAccount.get("account_id").toString()));
            rsPgsql = stPgsql.executeQuery();
            ResultSetMetaData metaData = rsPgsql.getMetaData();
            int colCount = metaData.getColumnCount();
            //convert resultset to jsonObject
            while (rsPgsql.next()) {
                for (int i = 1; i <= colCount; i++) {
                    if (rsPgsql.getObject(i) == null) {
                        objColumn.put(metaData.getColumnLabel(i), "");
                    } else {
                        objColumn.put(metaData.getColumnLabel(i), rsPgsql.getString(i));
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
        return objColumn;
    }
}
