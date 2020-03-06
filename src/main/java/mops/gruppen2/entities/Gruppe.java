package mops.gruppen2.entities;

import lombok.Data;
import mops.gruppen2.Events.CreateGroupEvent;
import mops.gruppen2.Events.Event;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class Gruppe {
	@Id
	Long id;
	String titel;
	String beschreibung;
	List<Teilnehmer> teilnehmersList;

	public void applyEvent(Event event){

	}

	public void applyEvent(CreateGroupEvent event){
		this.id = event.getId();
		this.titel = event.getTitel();
		this.beschreibung = event.getBeschreibung();
		this.teilnehmersList= null;
	}

}
