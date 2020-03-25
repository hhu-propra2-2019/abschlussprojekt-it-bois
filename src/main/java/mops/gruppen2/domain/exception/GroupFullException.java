package mops.gruppen2.domain.exception;

import org.springframework.http.HttpStatus;

public class GroupFullException extends EventException {

	public GroupFullException(String info) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, "Die Gruppe hat die maximale Midgliederanzahl bereits erreicht!", info);
	}
}

