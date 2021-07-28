package abs.sf.ads.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerError extends Exception {
	private static final long serialVersionUID = -7095122248511976133L;

	public InternalServerError() {
		super();
	}

	public InternalServerError(String msg) {
		super(msg);
	}

	public InternalServerError(Exception cause) {
		super(cause);
	}

	public InternalServerError(String msg, Exception cause) {
		super(msg, cause);
	}

}