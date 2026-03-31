package helper;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class OAuthRequestValidator {
    private final Cache cache;

    public OAuthRequestValidator() {
        //get config in cacheConfig
        CacheManager cacheManager = CacheManager.getInstance();
        this.cache = cacheManager.getCache(" SpreadSheetCacheAppCookies");
    }

    public boolean validate(Verb verb, String url, String timestamp, String nonce, String signatureMethod,
                            String version, String publicKey, String secretKey, String requestSignature) {

        DefaultApi10a api = new OauthDummyAPIProvider();

        //check if it has request once, prevent request hit more than one on each signature
        if(this.cache.isKeyInCache(timestamp + "_" + nonce + "_" + publicKey)){
            return false;
        } else {
            this.cache.put(new Element(timestamp + "_" + nonce + "_" + publicKey, "signIfReqHasBeenProcessBefore"));
        }

        OAuthRequest request = new OAuthRequest(verb, url);
        // add the necessary parameters
        request.addOAuthParameter(OAuthConstants.TIMESTAMP, timestamp);
        request.addOAuthParameter(OAuthConstants.NONCE, nonce);
        request.addOAuthParameter(OAuthConstants.CONSUMER_KEY, publicKey);
        request.addOAuthParameter(OAuthConstants.SIGN_METHOD, signatureMethod);
        request.addOAuthParameter(OAuthConstants.VERSION, version);

        String baseString = api.getBaseStringExtractor().extract(request);
        // Passing an empty token, as the 2-legged OAuth doesn't require it
        //System.out.println("baseString " + baseString);
        //System.out.println("secretKey " + secretKey);

        String realSignature = api.getSignatureService().getSignature(baseString, secretKey, "");
        //System.out.println("realSignature " + realSignature);
        //System.out.println("requestSignature " + requestSignature);

        return (requestSignature.compareTo(realSignature) == 0);
    }
}
