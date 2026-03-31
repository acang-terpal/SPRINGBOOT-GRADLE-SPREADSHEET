/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package configuration;

import org.apache.log4j.Logger;

/**
 *
 * @author hasan
 */
public class ControllerLog {

    final static Logger logStream = Logger.getLogger("stream");
    final static Logger logError = Logger.getLogger("error");
    final static Logger logDebug = Logger.getLogger("debug");
    final static Logger logParsing = Logger.getLogger("parsing");
    final static Logger logXmpp = Logger.getLogger("xmpp");

    public ControllerLog() {
    }

    public void logStreamWriter(String str) {
        logStream.info(str);
    }

    public void logErrorWriter(String str) {
        logError.error(str);
    }

    public void logDebugWriter(String str) {
        logDebug.debug(str);
    }

    public void logParsingWriter(String str) {
        logParsing.info(str);
    }

    public void logXmppWriter(String str) {
        logXmpp.info(str);
    }
}
