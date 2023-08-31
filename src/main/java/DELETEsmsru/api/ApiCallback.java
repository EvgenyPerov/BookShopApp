package DELETEsmsru.api;


public interface ApiCallback<Req extends ApiRequest, Resp extends ApiResponse> {
    /**
     * Checks if this callback is applicable to the following request and response.
     * @param request
     * @param response could be null if network error occurred
     */
    boolean apply(Req request, Resp response);

    /**
     * The method is called iff {@link #apply} returned true.
     * Is designed for saving or updating a row in a database, logging, etc.
     * @param request
     * @param response could be null if network error occurred.
     */
    void execute(Req request, Resp response);
}
