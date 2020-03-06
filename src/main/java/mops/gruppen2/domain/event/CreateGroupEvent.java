package mops.gruppen2.domain.event;

import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@Value
public class CreateGroupEvent extends Event {
	String groupTitle, groupDescription;

	public CreateGroupEvent(long event_id, long group_id, String user_id, String groupTitle, String groupDescription) {
		super(event_id, group_id, user_id);
		this.groupTitle = groupTitle;
		this.groupDescription = groupDescription;
	}
}
