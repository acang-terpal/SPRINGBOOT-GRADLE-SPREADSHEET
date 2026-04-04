package service.sqlite;

import entity.sqlite.EntityAccount;
import helper.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.sqlite.SpreadSheetAccountRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("sqliteAccountService")
public class SpreadSheetAccountService {

    private final Utilities utilities;
    private final SpreadSheetAccountRepo SpreadSheetAccountRepo;

    @Autowired
    public SpreadSheetAccountService(SpreadSheetAccountRepo SpreadSheetAccountRepo, Utilities utilities) {
        this.SpreadSheetAccountRepo = SpreadSheetAccountRepo;
        this.utilities = utilities;
    }

    public List<Object>  getAccount(HashMap<String, Object> objParams) {
        List<EntityAccount> entityAccounts = SpreadSheetAccountRepo.getAccount(objParams.get("login_username").toString(), objParams.get("account_password").toString());
        List  result = new ArrayList();
        if(entityAccounts.size() > 0) {
            HashMap<String, Object> mapAccount = (HashMap<String, Object>) this.utilities.convertObjectToMap(entityAccounts.get(0));
            result.add(mapAccount);
        }
        return result;
    }

}
