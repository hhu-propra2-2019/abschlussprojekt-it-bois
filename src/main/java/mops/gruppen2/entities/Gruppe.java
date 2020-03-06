package mops.gruppen2.entities;

import lombok.Data;
import mops.gruppen2.events.CreateGroupEvent;
import mops.gruppen2.events.UpdateGroupDescriptionEvent;
import mops.gruppen2.events.UpdateGroupTitleEvent;

import java.util.List;

@Data
public class Gruppe extends Aggregat {
	long id;
	String titel;
	String beschreibung;
	List<Teilnehmer> teilnehmersList;

	public void applyEvent(CreateGroupEvent event){
		this.id = event.getId();
		this.titel = event.getTitel();
		this.beschreibung = event.getBeschreibung();
		this.teilnehmersList= null;
	}

	public void applyEvent(UpdateGroupTitleEvent event) {
		this.titel = event.getTitel();
	}

	public void applyEvent(UpdateGroupDescriptionEvent event) {
		this.beschreibung = event.getBeschreibung();
	}
}
