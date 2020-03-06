package mops.gruppen2.Events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
public class CreateGroupEvent extends Event{
	String titel;
	String beschreibung;

	public CreateGroupEvent(Long id, Long gruppe_id, Long user_id, String titel,String beschreibung) {
		super(id, gruppe_id, user_id);
		this.titel = titel;
		this.beschreibung = beschreibung;
	}
}
