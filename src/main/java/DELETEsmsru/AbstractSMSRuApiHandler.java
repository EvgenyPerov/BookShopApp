package DELETEsmsru;

import java.util.StringTokenizer;

import DELETEsmsru.api.AbstractApiRequestHandler;
import DELETEsmsru.api.ApiRequest;
import DELETEsmsru.api.ApiResponse;
import DELETEsmsru.api.InvocationStatus;

/**
 * @author ilya.dezhin
 */
public abstract class AbstractSMSRuApiHandler<Req extends ApiRequest, Resp extends ApiResponse>
        extends AbstractApiRequestHandler<Req, Resp> {

    protected StringTokenizer tokenizeResponse(String responseStr) {
        return responseStr != null
                ? new StringTokenizer(responseStr, "\r?\n")
                : null;
    }

    protected void parseAndSetStatus(Req request, Resp response, StringTokenizer tokens) {
        if (tokens == null || !tokens.hasMoreTokens()) {
            request.setStatus(InvocationStatus.RESPONSE_PARSING_ERROR);
            return;
        }

        try {
            response.setResponseStatus(SMSRuResponseStatus.forValue(Integer.parseInt(tokens.nextToken().trim())));
        } catch (NumberFormatException e) {
            request.setStatus(InvocationStatus.RESPONSE_PARSING_ERROR);
            return;
        }
    }

    protected Double parseDoubleSafe(Req request, StringTokenizer tokens) {
        Double res = null;
        if (tokens.hasMoreTokens()) {
            try {
                res = Double.parseDouble(tokens.nextToken());
            } catch (NumberFormatException nfe) {
                request.setStatus(InvocationStatus.RESPONSE_PARSING_ERROR);
            }
        }
        return res;
    }

    protected Integer parseIntSafe(Req request, StringTokenizer tokens) {
        Integer res = null;
        if (tokens.hasMoreTokens()) {
            try {
                res = Integer.parseInt(tokens.nextToken());
            } catch (NumberFormatException nfe) {
                request.setStatus(InvocationStatus.RESPONSE_PARSING_ERROR);
            }
        }
        return res;
    }
}