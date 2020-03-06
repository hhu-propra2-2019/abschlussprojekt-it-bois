package mops.gruppen2.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import mops.gruppen2.domain.event.*;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.UpdateGroupDescriptionEvent;
import mops.gruppen2.domain.event.UpdateGroupTitleEvent;
import mops.gruppen2.domain.event.DeleteUserEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper=false)
@Data
public class Group extends Aggregate {
	long id;
	String titel;
	String beschreibung;
	List<User> teilnehmersList;
	Map<User, Role> rollenList;

	public void applyEvent(CreateGroupEvent event){
		this.id = event.getGruppe_id();
		this.titel = event.getTitel();
		this.beschreibung = event.getBeschreibung();
		this.teilnehmersList = new ArrayList<>();
		this.rollenList = new HashMap<>();
	}

	public void applyEvent(UpdateRoleEvent event) {
		teilnehmersList.stream()
				.filter(user -> user.getId().equals(event.getUser_id()))
				.findFirst()
				.ifPresentOrElse(user -> rollenList.put(user, event.getRole()),
						() -> System.out.println("UserNotFoundException"));
	}

	public void  applyEvent(AddUserEvent event){
		User user = new User();

		user.setId(event.getUser_id());
		user.setVorname(event.getVorname());
		user.setNachname(event.getNachname());
		user.setEmail(event.getEmail());

		this.teilnehmersList.add(user);
	}

	public void applyEvent(UpdateGroupTitleEvent event) {
		this.titel = event.getTitel();
	}

	public void applyEvent(UpdateGroupDescriptionEvent event) {
		this.beschreibung = event.getBeschreibung();
	}

	public void applyEvent(DeleteUserEvent event) {
		for (User user : teilnehmersList) {
			if (user.getId().equals(event.getUser_id())) {
				this.teilnehmersList.remove(user);
				break;
			}
		}
	}
}
