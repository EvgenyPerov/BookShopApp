package DELETEsmsru.api;

import DELETEsmsru.sender.SenderServiceConfiguration;

public abstract class AbstractApiRequestHandler<Req extends ApiRequest, Resp extends ApiResponse>
        implements ApiRequestHandler<Req, Resp> {
    protected SenderServiceConfiguration config;

    @Override
    public void setConfig(SenderServiceConfiguration config) {
        this.config = config;
    }
}