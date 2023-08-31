package DELETEsmsru.send;

import java.util.ArrayList;
import java.util.List;

public class SMSRuSendResponse extends SMSRuApiResponse {
    List<String> msgIds = new ArrayList<String>();
    Double balance;

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public void addMsgId(String msgId) {
        msgIds.add(msgId);
    }

    public List<String> getMsgIds() {
        return msgIds;
    }
}