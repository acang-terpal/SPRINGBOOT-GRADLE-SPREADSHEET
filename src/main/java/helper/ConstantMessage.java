package helper;

import org.json.JSONObject;

/**
 * Create by Hasan on 04/09/2019.
 */
public class ConstantMessage {

    public enum Pesan {
        getSuccess,
        getFailed,
        getCookieExpire,
        getPermissionDenied,
        getDeniedAddSelfAsMember,
        getAccountAlreadyActive,
        getAccountAlreadyMember,
        getAccountAlreadyRegister,
        UNKNOWN;

        public JSONObject pesan() {
            JSONObject checkResponse = new JSONObject();
            switch (this) {
                case getSuccess:
                    checkResponse.put("message", "Success");
                    checkResponse.put("rc", "00");
                    return checkResponse;
                case getFailed:
                    checkResponse.put("message", "Failed");
                    checkResponse.put("rc", "01");
                    return checkResponse;
                case getCookieExpire:
                    checkResponse.put("message", "Session Expire");
                    checkResponse.put("rc", "02");
                    return checkResponse;
                case getPermissionDenied:
                    checkResponse.put("message", "Permission Denied");
                    checkResponse.put("rc", "03");
                    return checkResponse;
                case getDeniedAddSelfAsMember:
                    checkResponse.put("message", "Cant add your self as member");
                    checkResponse.put("rc", "04");
                    return checkResponse;
                case getAccountAlreadyActive:
                    checkResponse.put("message", "Failed, account already active!");
                    checkResponse.put("rc", "05");
                    return checkResponse;
                case getAccountAlreadyMember:
                    checkResponse.put("message", "Account already your member");
                    checkResponse.put("rc", "06");
                    return checkResponse;
                case getAccountAlreadyRegister:
                    checkResponse.put("message", "Account already register");
                    checkResponse.put("rc", "07");
                    return checkResponse;
                default:
                    throw new AssertionError("Unknown operations " + this);
            }
        }
    }
}
