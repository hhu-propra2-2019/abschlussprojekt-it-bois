package mops.gruppen2.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import mops.gruppen2.domain.event.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper=false)
@Getter
public class Group extends Aggregate {
	long id;
	String title;
	String description;
	List<User> members;
	Map<User, Role> roles;

	private void applyEvent(CreateGroupEvent event){
		this.id = event.getGroup_id();
		this.title = event.getGroupTitle();
		this.description = event.getGroupDescription();
		this.members = new ArrayList<>();
		this.roles = new HashMap<>();
	}

	private void applyEvent(UpdateRoleEvent event) {
		members.stream()
				.filter(user -> user.getUser_id().equals(event.getUser_id()))
				.findFirst()
				.ifPresentOrElse(user -> roles.put(user, event.getNewRole()),
						() -> System.out.println("UserNotFoundException"));
	}

	private void applyEvent(AddUserEvent event){
		User user = new User(event.getUser_id(), event.getGivenname(), event.getFamilyname(), event.getEmail());

		this.members.add(user);
	}

	private void applyEvent(UpdateGroupTitleEvent event) {
		this.title = event.getNewGroupTitle();
	}

	private void applyEvent(UpdateGroupDescriptionEvent event) {
		this.description = event.getNewGroupDescription();
	}

	private void applyEvent(DeleteUserEvent event) {
		for (User user : members) {
			if (user.getUser_id().equals(event.getUser_id())) {
				this.members.remove(user);
				break;
			}
		}
	}
}
