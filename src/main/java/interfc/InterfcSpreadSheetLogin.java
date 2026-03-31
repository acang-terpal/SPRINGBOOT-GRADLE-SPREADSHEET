package interfc;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author hasan
 */
public interface InterfcSpreadSheetLogin {

    List getAccount(HashMap objParams);

    List getGroup(String groupId);

    HashMap insertGroup(HashMap objParams);

    List getAccountRegister(HashMap objParams);

    HashMap insertAccount(HashMap objParams);

    HashMap updateAccountVerify(HashMap objCookie);

    HashMap updateAccountPassword(HashMap objCookie);
}
