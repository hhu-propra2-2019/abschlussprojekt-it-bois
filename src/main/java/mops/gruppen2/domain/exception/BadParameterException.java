package mops.gruppen2.domain.exception;

import org.springframework.http.HttpStatus;

public class BadParameterException extends EventException {

	public BadParameterException(String info) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, "Fehlerhafter Parameter angegeben!", info);
	}
}
