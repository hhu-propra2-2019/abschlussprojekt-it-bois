package mops.gruppen2.domain.event;

import lombok.Getter;

@Getter
public class AddUserEvent extends Event{
	String vorname, nachname, email;

	public AddUserEvent(long id, long gruppe_id, String user_id, String vorname, String nachname, String email) {
		super(id, gruppe_id, user_id);
		this.vorname = vorname;
		this.nachname = nachname;
		this.email = email;
	}
}
