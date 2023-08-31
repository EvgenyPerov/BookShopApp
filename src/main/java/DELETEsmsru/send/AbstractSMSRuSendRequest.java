package DELETEsmsru.send;

import DELETEsmsru.AbstractSMSRuApiHandler;
import DELETEsmsru.api.ApiRequest;

/**
 * @author ilya.dezhin
 */
public abstract class AbstractSMSRuSendRequest<H extends AbstractSMSRuApiHandler> extends ApiRequest<H, SMSRuSendResponse> {
    String from;
    Long postponedSendingTimeMs;
    boolean testSendingEnabled;
    boolean translitEnabled;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Long getPostponedSendingTimeMs() {
        return postponedSendingTimeMs;
    }

    public void setPostponedSendingTimeMs(Long postponedSendingTimeMs) {
        this.postponedSendingTimeMs = postponedSendingTimeMs;
    }

    public boolean isTestSendingEnabled() {
        return testSendingEnabled;
    }

    public void setTestSendingEnabled(boolean testSendingEnabled) {
        this.testSendingEnabled = testSendingEnabled;
    }

    public boolean isTranslitEnabled() {
        return translitEnabled;
    }

    public void setTranslitEnabled(boolean translitEnabled) {
        this.translitEnabled = translitEnabled;
    }
}
