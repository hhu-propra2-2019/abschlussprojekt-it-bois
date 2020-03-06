package mops.gruppen2.domain.event;

import lombok.Getter;

@Getter
public class AddUserEvent extends Event{
	String givenname, familyname, email;

	public AddUserEvent(long event_id, long group_id, String user_id, String givenname, String familyname, String email) {
		super(event_id, group_id, user_id);
		this.givenname = givenname;
		this.familyname = familyname;
		this.email = email;
	}
}
