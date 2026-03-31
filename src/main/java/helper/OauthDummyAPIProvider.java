package helper;

import com.github.scribejava.core.builder.api.DefaultApi10a;

public class OauthDummyAPIProvider extends DefaultApi10a {
    public String getRequestTokenEndpoint() {
        return null;
    }

    public String getAccessTokenEndpoint() {
        return null;
    }

    protected String getAuthorizationBaseUrl() {
        return null;
    }
}
