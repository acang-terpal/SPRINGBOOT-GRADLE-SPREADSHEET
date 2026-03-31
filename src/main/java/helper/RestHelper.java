package helper;

import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.Verb;
import configuration.ControllerLog;
import entity.EntityConfig;
import net.sf.ehcache.Cache;
import org.json.simple.JSONObject;

public class RestHelper {
    Cache cacheConfig;
    Helper hlp;
    private EntityConfig entityConfig;
    private ControllerLog controllerLog;
    private Utilities utilities;


    public RestHelper(EntityConfig entityConfig, Cache cacheMaster, Cache cacheCookies, Utilities utilities, Helper helper, ControllerLog controllerLog){
        this.entityConfig = entityConfig;
        this.hlp = helper;
        this.utilities = utilities;
        this.controllerLog = controllerLog;
    }

    public String logRequest(String tipeRequest, JSONObject jsonReq, boolean noLogDataEnc) {
        JSONObject jsonReqForLog = this.utilities.writeLogStreamRequest(tipeRequest, jsonReq, noLogDataEnc);
        //String idTmp = this.utilities.insertTmp(tipeRequest, jsonReqForLog);
        String idTmp = "0";
        return idTmp;
    }

    public void logResponse(String tipeRequest, JSONObject jsonReq, String resp, String idTmp) {
        JSONObject jObjForLog = this.utilities.writeLogStreamResponse(tipeRequest, jsonReq, resp);
        //this.utilities.updateTmp(idTmp, jObjForLog.toString());
    }

    public boolean oauth2LegVerify(JSONObject jsonReq, String method, String reference) {
        // Check that all the needed parameters are contained in the request
        // Validate the request
        OAuthRequestValidator oAuthRequestValidator = new OAuthRequestValidator();
        boolean valid = oAuthRequestValidator.validate(
                Verb.valueOf(method),
                reference,
                jsonReq.get(OAuthConstants.TIMESTAMP).toString(),
                jsonReq.get(OAuthConstants.NONCE).toString(),
                jsonReq.get(OAuthConstants.SIGN_METHOD).toString(),
                jsonReq.get(OAuthConstants.VERSION).toString(),
                jsonReq.get(OAuthConstants.CONSUMER_KEY).toString(),
                jsonReq.get("secretKeyHeader").toString(),
                jsonReq.get(OAuthConstants.SIGNATURE).toString());
        return valid;
    }
}
