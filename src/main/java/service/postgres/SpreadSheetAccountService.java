package service.postgres;

import entity.postgres.EntityUser;
import helper.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import repository.postgres.SpreadSheetAccountRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("postgresAccountService")
public class SpreadSheetAccountService {

    private final Utilities utilities;
    private final SpreadSheetAccountRepo spreadSheetAccountRepo;

    @Autowired
    public SpreadSheetAccountService(SpreadSheetAccountRepo SpreadSheetAccountRepo, Utilities utilities) {
        this.spreadSheetAccountRepo = SpreadSheetAccountRepo;
        this.utilities = utilities;
    }

    public List<Object>  getAccount(HashMap<String, Object> objParams) {
        List<EntityUser> entityAccounts = spreadSheetAccountRepo.getAccount(objParams.get("login_username").toString(), objParams.get("account_password").toString());
        List  result = new ArrayList();
        if(entityAccounts.size() > 0) {
            HashMap<String, Object> mapAccount = (HashMap<String, Object>) this.utilities.convertObjectToMap(entityAccounts.get(0));
            result.add(mapAccount);
        }
        return result;
    }
}
