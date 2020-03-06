package mops.gruppen2.events;

import lombok.Getter;

@Getter
public class CreateGroupEvent extends Event {
	String titel;
	String beschreibung;

	public CreateGroupEvent(long id, long gruppe_id, long user_id, String titel, String beschreibung) {
		super(id, gruppe_id, user_id);
		this.titel = titel;
		this.beschreibung = beschreibung;
	}
}
