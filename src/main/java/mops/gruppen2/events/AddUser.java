package mops.gruppen2.events;

import lombok.Getter;

@Getter
public class AddUser extends Event{
	String vorname, nachname, email;

	public AddUser(long id, long gruppe_id, String user_id, String vorname, String nachname, String email) {
		super(id, gruppe_id, user_id);
		this.vorname = vorname;
		this.nachname = nachname;
		this.email = email;
	}
}
