package mops.gruppen2.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import mops.gruppen2.events.AddUser;
import mops.gruppen2.events.CreateGroupEvent;
import mops.gruppen2.events.UpdateGroupDescriptionEvent;
import mops.gruppen2.events.UpdateGroupTitleEvent;
import mops.gruppen2.events.DeleteUserEvent;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class Gruppe extends Aggregat {
	long id;
	String titel;
	String beschreibung;
	List<Teilnehmer> teilnehmersList;

	public Gruppe(){
		this.teilnehmersList = new ArrayList<>();
	}

	public void applyEvent(CreateGroupEvent event){
		this.id = event.getGruppe_id();
		this.titel = event.getTitel();
		this.beschreibung = event.getBeschreibung();
	}

	public void  applyEvent(AddUser event){
		Teilnehmer teilnehmer = new Teilnehmer();
		teilnehmer.setId(event.getId());
		teilnehmer.setVorname(event.getVorname());
		teilnehmer.setNachname(event.getNachname());
		teilnehmer.setEmail(event.getEmail());
		this.teilnehmersList.add(teilnehmer);
	}

	public void applyEvent(UpdateGroupTitleEvent event) {
		this.titel = event.getTitel();
	}

	public void applyEvent(UpdateGroupDescriptionEvent event) {
		this.beschreibung = event.getBeschreibung();
		this.teilnehmersList = new ArrayList<>();
	}

	public void applyEvent(DeleteUserEvent event) {
		for (Teilnehmer teilnehmer: teilnehmersList) {
			if (teilnehmer.getId().equals(event.getUser_id())) {
				this.teilnehmersList.remove(teilnehmer);
				break;
			}
		}
	}
}
