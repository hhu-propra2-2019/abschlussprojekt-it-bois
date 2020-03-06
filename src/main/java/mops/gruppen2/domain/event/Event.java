package mops.gruppen2.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Event {
	long event_id;
	long group_id;
	String user_id;
}
