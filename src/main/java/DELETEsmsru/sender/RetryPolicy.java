package DELETEsmsru.sender;

import DELETEsmsru.api.ApiRequest;
import DELETEsmsru.api.ApiResponse;

public interface RetryPolicy<Req extends ApiRequest, Resp extends ApiResponse> {
    /**
     *
     * @param request request instance to the remote API
     * @param response response from the remote API, could be null.
     * @return true if retry must be done otherwise false
     */
    boolean shouldRetry(Req request, Resp response);

    /**
     * @return delay duration in ms before next retry
     */
    long getDelayDurationMs();
}