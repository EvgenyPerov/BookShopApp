package DELETEsmsru.auth;

import java.util.ArrayList;
import java.util.List;

import DELETEsmsru.sender.SenderServiceConfiguration;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class DefaultAuthProvider implements AuthProvider {
    private final SenderServiceConfiguration config;

    public DefaultAuthProvider(SenderServiceConfiguration config) {
        this.config = config;
        if (config.getApiId() == null || config.getApiId().isEmpty()) {
            throw new IllegalStateException("ApiId in required for default authentication.");
        }
    }

    @Override
    public List<NameValuePair> provideAuthParams() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("api_id", config.getApiId()));
        return params;
    }
}