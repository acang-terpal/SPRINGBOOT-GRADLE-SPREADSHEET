
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

/**
 *
 * @author hasan
 */
public class EntityConfig {

    private String workDir;
    private String httpPort;
    private String pathUri;
    
    private String dbPgsqlCecenet;
    private String dbPgsqlIpCecenet;
    private String dbPgsqlPortCecenet;
    private String dbPgsqlUserCecenet;
    private String dbPgsqlPassCecenet;
    
    private String dbPgsqlMamen;
    private String dbPgsqlIpMamen;
    private String dbPgsqlPortMamen;
    private String dbPgsqlUserMamen;
    private String dbPgsqlPassMamen;

    private String dbPgsqlSession;
    private String dbPgsqlIpSession;
    private String dbPgsqlPortSession;
    private String dbPgsqlUserSession;
    private String dbPgsqlPassSession;
    
    private String dbMysql;
    private String dbMysqlIp;
    private String dbMysqlPort;
    private String dbMysqlUser;
    private String dbMysqlPass;

    private String dbSqliteFilePath;
    private String dbSqliteUser;
    private String dbSqlitePassword;

    private String ftpIp;
    private String ftpPort;
    private String ftpUser;
    private String ftpPass;
    private String ftpPath;
    
    private String maxThreads;
    private String minThreads;
    private String lowThreads;
    private String maxQueued;
    private String maxTotalConnections;
    private String maxIoIdleTimeMs;
    private String sessionTtl;
    private String timeRemoveBeforeSessionDelete;
    
    private String encKey;
    private String encIv;
    private String encRequest;
    private String encResponse;
    private String encKeyDb;
    private String encIvDb;
    
    private String scaleImgSize;
    private String setConnectTimeout;
    private String setReadTimeout;
    private String logStream;
    private String masterFolder;
    private String urlSession;

    private String usernamePurchase;
    private String keyPurchase;
    private String usernamePayment;
    private String keyPayment;

    public EntityConfig() {
    }

    ////////////////////////////////////////////////////////getter/////////////////////////////////////////////

    public String getWorkDir() {
        return this.workDir;
    }

    public String getHttpPort() {
        return this.httpPort;
    }

    //---------------------db postgres---------------
    public String getDbPgsqlCecenet() {
        return this.dbPgsqlCecenet;
    }

    public String getDbPgsqlIpCecenet() {
        return this.dbPgsqlIpCecenet;
    }

    public String getDbPgsqlPortCecenet() {
        return this.dbPgsqlPortCecenet;
    }

    public String getDbPgsqlUserCecenet() {
        return this.dbPgsqlUserCecenet;
    }

    public String getDbPgsqlPassCecenet() {
        return this.dbPgsqlPassCecenet;
    }
    //---------------------end db postgres---------------
    
    //---------------------db postgres---------------
    public String getDbPgsqlMamen() {
        return this.dbPgsqlMamen;
    }

    public String getDbPgsqlIpMamen() {
        return this.dbPgsqlIpMamen;
    }

    public String getDbPgsqlPortMamen() {
        return this.dbPgsqlPortMamen;
    }

    public String getDbPgsqlUserMamen() {
        return this.dbPgsqlUserMamen;
    }

    public String getDbPgsqlPassMamen() {
        return this.dbPgsqlPassMamen;
    }
    //---------------------end db postgres---------------

    //---------------------db postgres---------------
    public String getDbPgsqlSession() {
        return this.dbPgsqlSession;
    }

    public String getDbPgsqlIpSession() {
        return this.dbPgsqlIpSession;
    }

    public String getDbPgsqlPortSession() {
        return this.dbPgsqlPortSession;
    }

    public String getDbPgsqlUserSession() {
        return this.dbPgsqlUserSession;
    }

    public String getDbPgsqlPassSession() {
        return this.dbPgsqlPassSession;
    }
    //---------------------end db postgres---------------
    
    //---------------------db mysql---------------
    public String getDbMysql() {
        return this.dbMysql;
    }

    public String getDbMysqlIp() {
        return this.dbMysqlIp;
    }

    public String getDbMysqlPort() {
        return this.dbMysqlPort;
    }

    public String getDbMysqlUser() {
        return this.dbMysqlUser;
    }

    public String getDbMysqlPass() {
        return this.dbMysqlPass;
    }
    //---------------------end db mysql---------------

    //---------------------db sqlite---------------
    public String getDbSqliteFilePath() {
        return this.dbSqliteFilePath;
    }
    public String getDbSqliteUser() { return this.dbSqliteUser; }
    public String getDbSqlitePassword() { return this.dbSqlitePassword; }

    //---------------------end db mysql---------------
    
    //---------------------ftp---------------
    public String getFtpIp() {
        return this.ftpIp;
    }

    public String getFtpPort() {
        return this.ftpPort;
    }

    public String getFtpUser() {
        return this.ftpUser;
    }

    public String getFtpPass() {
        return this.ftpPass;
    }
    
    public String getFtpPath() {
        return this.ftpPath;
    }
    //---------------------end ftp---------------

    public String getMaxThreads() {
        return this.maxThreads;
    }

    public String getMinThreads() {
        return this.minThreads;
    }

    public String getLowThreads() {
        return this.lowThreads;
    }

    public String getMaxQueued() {
        return this.maxQueued;
    }

    public String getMaxTotalConnections() {
        return this.maxTotalConnections;
    }

    public String getMaxIoIdleTimeMs() {
        return this.maxIoIdleTimeMs;
    }

    public String getPathUri() {
        return this.pathUri;
    }
    
    public String getSessionTtl(){
        return this.sessionTtl;
    }
    public String getTimeRemoveBeforeSessionDelete(){
        return this.timeRemoveBeforeSessionDelete;
    }
    

    public String getEncKeyDb() {
        return this.encKeyDb;
    }

    public String getEncIvDb() {
        return this.encIvDb;
    }
    
    public String getEncKey() {
        return this.encKey;
    }

    public String getEncIv() {
        return this.encIv;
    }

    public String getEncRequest() {
        return this.encRequest;
    }

    public String getEncResponse() {
        return this.encResponse;
    }

    public String getScaleImgSize() {
        return this.scaleImgSize;
    }

    public String getSetConnectTimeout() {
        return this.setConnectTimeout;
    }

    public String getSetReadTimeout() {
        return this.setReadTimeout;
    }

    public String getLogStream() {
        return this.logStream;
    }

    public String getMasterFolder() {
        return this.masterFolder;
    }

    public String getUrlSession() {
        return this.urlSession;
    }

    //----------------------------------------------base API--------------------------------------
    public String getUsernamePurchase(){
        return this.usernamePurchase;
    }

    public String getKeyPurchase(){
        return this.keyPurchase;
    }

    public String getUsernamePayment(){
        return this.usernamePayment;
    }

    public String getKeyPayment(){
        return this.keyPayment;
    }


    ///////////////////////////////////////////////setter/////////////////////////////////////////////////////////////

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    public void setHttpPort(String httpPort) {
        this.httpPort = httpPort;
    }

    //------------------------db postgres-------------------
    public void setDbPgsqlCecenet(String database) {
        this.dbPgsqlCecenet = database;
    }

    public void setDbPgsqlIpCecenet(String dbIp) {
        this.dbPgsqlIpCecenet = dbIp;
    }

    public void setDbPgsqlPortCecenet(String dbPort) {
        this.dbPgsqlPortCecenet = dbPort;
    }

    public void setDbPgsqlUserCecenet(String dbUser) {
        this.dbPgsqlUserCecenet = dbUser;
    }

    public void setDbPgsqlPassCecenet(String dbPass) {
        this.dbPgsqlPassCecenet = dbPass;
    }
    //------------------------end db postgres-------------------
    
    //------------------------db postgres-------------------
    public void setDbPgsqlMamen(String database) {
        this.dbPgsqlMamen = database;
    }

    public void setDbPgsqlIpMamen(String dbIp) {
        this.dbPgsqlIpMamen = dbIp;
    }

    public void setDbPgsqlPortMamen(String dbPort) {
        this.dbPgsqlPortMamen = dbPort;
    }

    public void setDbPgsqlUserMamen(String dbUser) {
        this.dbPgsqlUserMamen = dbUser;
    }

    public void setDbPgsqlPassMamen(String dbPass) {
        this.dbPgsqlPassMamen = dbPass;
    }
    //------------------------end db postgres-------------------

    //------------------------db postgres-------------------
    public void setDbPgsqlSession(String database) {
        this.dbPgsqlSession = database;
    }

    public void setDbPgsqlIpSession(String dbIp) {
        this.dbPgsqlIpSession= dbIp;
    }

    public void setDbPgsqlPortSession(String dbPort) {
        this.dbPgsqlPortSession = dbPort;
    }

    public void setDbPgsqlUserSession(String dbUser) {
        this.dbPgsqlUserSession = dbUser;
    }

    public void setDbPgsqlPassSession(String dbPass) {
        this.dbPgsqlPassSession = dbPass;
    }
    //------------------------end db postgres-------------------
    
    //------------------------db mysql-------------------
    public void setDbMysql(String database) { this.dbMysql = database; }

    public void setDbMysqlIp(String dbIp) { this.dbMysqlIp = dbIp; }

    public void setDbMysqlPort(String dbPort) { this.dbMysqlPort = dbPort; }

    public void setDbMysqlUser(String dbUser) { this.dbMysqlUser = dbUser; }

    public void setDbMysqlPass(String dbPass) { this.dbMysqlPass = dbPass; }
    //------------------------end db mysql-------------------

    //------------------------db sqlite-------------------
    public void setDbSqliteFilePath(String database) { this.dbSqliteFilePath = database; }

    public void setDbSqliteUser(String dbUser) { this.dbSqliteUser = dbUser; }

    public void setDbSqlitePassword(String dbPass) { this.dbSqlitePassword = dbPass; }
    //------------------------end sqlite-------------------
    
    //------------------------ftp-------------------
    public void setFtpIp(String ftpIp) {
        this.ftpIp = ftpIp;
    }

    public void setFtpPort(String ftpPort) {
        this.ftpPort = ftpPort;
    }

    public void setFtpUser(String ftpUser) {
        this.ftpUser = ftpUser;
    }

    public void setFtpPass(String ftpPass) {
        this.ftpPass = ftpPass;
    }
    
    public void setFtpPath(String ftpPath) {
        this.ftpPath = ftpPath;
    }
    //------------------------end ftp-------------------

    public void setMaxThreads(String maxThreads) {
        this.maxThreads = maxThreads;
    }

    public void setMinThreads(String minThreads) {
        this.minThreads = minThreads;
    }

    public void setLowThreads(String lowThreads) {
        this.lowThreads = lowThreads;
    }

    public void setMaxQueued(String maxQueued) {
        this.maxQueued = maxQueued;
    }

    public void setMaxTotalConnections(String maxTotalConnections) {
        this.maxTotalConnections = maxTotalConnections;
    }

    public void setMaxIoIdleTimeMs(String maxIoIdleTimeMs) {
        this.maxIoIdleTimeMs = maxIoIdleTimeMs;
    }

    public void setPathUri(String pathUri) {
        this.pathUri = pathUri;
    }
    
    public void setSessionTtl(String sessionTtl){
        this.sessionTtl = sessionTtl;
    }
    
    public void setTimeRemoveBeforeSessionDelete(String timeRemoveBeforeSessionDelete){
        this.timeRemoveBeforeSessionDelete = timeRemoveBeforeSessionDelete;
    }
    
    
    public void setEncKeyDb(String encKey) {
        this.encKeyDb = encKey;
    }

    public void setEncIvDb(String encIvDb) {this.encIvDb = encIvDb;}

    public void setEncKey(String encKey) {
        this.encKey = encKey;
    }

    public void setEncIv(String encIv) {
        this.encIv = encIv;
    }

    public void setEncRequest(String encRequest) {
        this.encRequest = encRequest;
    }

    public void setEncResponse(String encResponse) {
        this.encResponse = encResponse;
    }

    public void setScaleImgSize(String scaleImgSize) {
        this.scaleImgSize = scaleImgSize;
    }

    public void setSetConnectTimeout(String setConnectTimeout) {
        this.setConnectTimeout = setConnectTimeout;
    }

    public void setSetReadTimeout(String setReadTimeout) {
        this.setReadTimeout = setReadTimeout;
    }

    public void setlogStream(String logStream) {
        this.logStream = logStream;
    }

    public void setMasterFolder(String masterFolder) {
        this.masterFolder = masterFolder;
    }

    public void setUrlSession(String urlSession) {
        this.urlSession = urlSession;
    }

    //------------------------------------------base API--------------------------------------
    public void setUsernamePurchase(String usernamePurchase){
        this.usernamePurchase = usernamePurchase;
    }

    public void setKeyPurchase(String keyPurchase){
        this.keyPurchase = keyPurchase;
    }

    public void setUsernamePayment(String usernamePayment){
        this.usernamePayment = usernamePayment;
    }

    public void setKeyPayment(String keyPayment){
        this.keyPayment = keyPayment;
    }
}
