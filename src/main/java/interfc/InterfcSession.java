package interfc;

import java.util.HashMap;

/**
 *
 * @author hasan
 */
public interface InterfcSession {

    HashMap setCookie(HashMap<String, Object> hashObj);

    HashMap getCookie(HashMap<String, Object> hashObj);

    HashMap updateCookie(HashMap<String, Object> hashObj);
}
