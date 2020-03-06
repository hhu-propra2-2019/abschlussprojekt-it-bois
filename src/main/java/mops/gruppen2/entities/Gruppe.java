package mops.gruppen2.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import mops.gruppen2.events.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class Gruppe extends Aggregat {
	long id;
	String titel;
	String beschreibung;
	List<Teilnehmer> teilnehmersList;
	Map<Teilnehmer, Rolle> rollenList;

	public void applyEvent(CreateGroupEvent event){
		this.id = event.getId();
		this.titel = event.getTitel();
		this.beschreibung = event.getBeschreibung();
		this.teilnehmersList = new ArrayList<>();
		this.rollenList = new HashMap<>();
	}

	public void applyEvent(UpdateRoleEvent event) {
		teilnehmersList.stream()
				.filter(teilnehmer -> teilnehmer.getId().equals(event.getUser_id()))
				.findFirst()
				.ifPresentOrElse(teilnehmer -> rollenList.put(teilnehmer, event.getRole()),
						() -> System.out.println("UserNotFoundException"));
	}

	public void  applyEvent(AddUser event){
		Teilnehmer teilnehmer = new Teilnehmer();

		teilnehmer.setId(event.getUser_id());
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
	}
}
