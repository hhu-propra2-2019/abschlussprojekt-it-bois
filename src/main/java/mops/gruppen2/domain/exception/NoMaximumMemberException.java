package mops.gruppen2.domain.exception;

import org.springframework.http.HttpStatus;

public class NoMaximumMemberException extends EventException {

	public NoMaximumMemberException(String info) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, "Es wurde keine maximale Gruppenanzahl festgelegt", info);
	}
}
