package mops.gruppen2.entities;

import lombok.Data;
import mops.gruppen2.events.CreateGroupEvent;

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
}
