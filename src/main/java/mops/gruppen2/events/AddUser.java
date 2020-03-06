package mops.gruppen2.events;

import lombok.Getter;

import java.util.List;

@Getter
public class AddUser extends Event{
	String vorname, nachname, email;

	public AddUser(long id, long gruppe_id, long user_id, String vorname, String nachname, String email) {
		super(id, gruppe_id, user_id);
		this.vorname = vorname;
		this.nachname = nachname;
		this.email = email;
	}
}
