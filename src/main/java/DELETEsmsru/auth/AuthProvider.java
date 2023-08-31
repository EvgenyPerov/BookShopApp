package DELETEsmsru.auth;

import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;

/**
 * @author ilya.dezhin
 */
public interface AuthProvider {
    List<NameValuePair> provideAuthParams() throws IOException;
}