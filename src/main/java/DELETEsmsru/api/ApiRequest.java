package DELETEsmsru.api;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class ApiRequest<H extends ApiRequestHandler, R extends ApiResponse> {
    public abstract H getHandler();

    /**
     * Number of attempts made to execute request. If the request is executed
     * from the first attempt then the value of this field would be equals to 1.
     */
    protected final AtomicInteger executionAttempt = new AtomicInteger(0);

       protected volatile Object attachment;

    /**
     * Invocation status field, shouldn't be null.
     */
    protected volatile InvocationStatus status = InvocationStatus.QUEUED;
    /**
     * Is not empty iff this.status.isAbnormal() returns true.
     */
    protected volatile Throwable exception;

    public Object attachment() {
        return attachment;
    }

    public void attach(Object attachment) {
        this.attachment = attachment;
    }

    public int incrementExecutionAttempt() {
        return executionAttempt.incrementAndGet();
    }

    public int getExecutionAttempt() {
        return executionAttempt.get();
    }

    public InvocationStatus getStatus() {
        return status;
    }

    public void setStatus(InvocationStatus status) {
        this.status = status;
    }

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}
