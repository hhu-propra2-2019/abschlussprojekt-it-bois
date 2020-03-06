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
	String title;
	String description;
	List<User> members;
	Map<User, Role> roles;

	public void applyEvent(CreateGroupEvent event){
		this.id = event.getGroup_id();
		this.title = event.getGroupTitle();
		this.description = event.getGroupDescription();
		this.members = new ArrayList<>();
		this.roles = new HashMap<>();
	}

	public void applyEvent(UpdateRoleEvent event) {
		members.stream()
				.filter(user -> user.getUser_id().equals(event.getUser_id()))
				.findFirst()
				.ifPresentOrElse(user -> roles.put(user, event.getNewRole()),
						() -> System.out.println("UserNotFoundException"));
	}

	public void  applyEvent(AddUserEvent event){
		User user = new User();

		user.setUser_id(event.getUser_id());
		user.setGivenname(event.getGivenname());
		user.setFamilyname(event.getFamilyname());
		user.setEmail(event.getEmail());

		this.members.add(user);
	}

	public void applyEvent(UpdateGroupTitleEvent event) {
		this.title = event.getNewGroupTitle();
	}

	public void applyEvent(UpdateGroupDescriptionEvent event) {
		this.description = event.getNewGroupDescription();
	}

	public void applyEvent(DeleteUserEvent event) {
		for (User user : members) {
			if (user.getUser_id().equals(event.getUser_id())) {
				this.members.remove(user);
				break;
			}
		}
	}
}
