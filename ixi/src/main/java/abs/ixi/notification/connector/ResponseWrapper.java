package abs.ixi.notification.connector;

public class ResponseWrapper<T> {
    private T response;

    public ResponseWrapper(T response) {
	super();
	this.response = response;
    }

    public T getResponse() {
	return response;
    }

}
