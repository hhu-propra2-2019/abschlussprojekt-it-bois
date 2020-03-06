package mops.gruppen2.domain.event;

import lombok.EqualsAndHashCode;
import lombok.Value;
import mops.gruppen2.domain.User;

@EqualsAndHashCode(callSuper = true)
@Value
public class AddUserEvent extends Event{
	String givenname, familyname, email;

	public AddUserEvent(long event_id, long group_id, String user_id, String givenname, String familyname, String email) {
		super(event_id, group_id, user_id);
		this.givenname = givenname;
		this.familyname = familyname;
		this.email = email;
	}

	public AddUserEvent(long event_id, long group_id, User user) {
		super(event_id, group_id, user.getUser_id());
		this.givenname = user.getGivenname();
		this.familyname = user.getFamilyname();
		this.email = user.getEmail();
	}
}
