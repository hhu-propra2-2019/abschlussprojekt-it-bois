package mops.gruppen2.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Event {
	long id;
	long gruppe_id;
	long user_id;
}
