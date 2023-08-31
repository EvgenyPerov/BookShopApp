package DELETEsmsru.send;

import java.util.Collection;
import java.util.LinkedList;

public class SMSRuSendRequest extends AbstractSMSRuSendRequest<SMSRuSendHandler> {
    Collection<String> receivers = new LinkedList<String>();
    String text;

    public SMSRuSendRequest() {
    }

    public SMSRuSendRequest(String text, Collection<String> receivers) {
        this.text = text;
        this.receivers = receivers;
    }

    @Override
    public SMSRuSendHandler getHandler() {
        return new SMSRuSendHandler();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Collection<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(Collection<String> receivers) {
        this.receivers = receivers;
    }

    public void addReceiver(String receiver) {
        this.receivers.add(receiver);
    }
}