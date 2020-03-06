package mops.gruppen2.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import mops.gruppen2.domain.event.*;

import java.util.*;

/**
 * Repräsentiert den aggregierten Zustand einer Gruppe.
 */
@EqualsAndHashCode(callSuper = false)
@Getter
public class Group extends Aggregate {
    private String title;
    private String description;
    private List<User> members;
    private Map<User, Role> roles;

    public Group(CreateGroupEvent event) {
        super(event.getGroup_id());
        this.title = event.getGroupTitle();
        this.description = event.getGroupDescription();
        this.members = new ArrayList<>();
        this.roles = new HashMap<>();
    }

    private void applyEvent(UpdateRoleEvent event) {
        User user;

        Optional<User> userOptional = members.stream()
                .filter(u -> u.getUser_id().equals(event.getUser_id()))
                .findFirst();

        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            System.out.println("UserNotFoundException");
            return;
        }

        if (roles.containsKey(user) && event.getNewRole() == Role.STUDENT) {
            roles.remove(user);
        } else {
            roles.put(user, event.getNewRole());
        }
    }

    private void applyEvent(AddUserEvent event) {
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
