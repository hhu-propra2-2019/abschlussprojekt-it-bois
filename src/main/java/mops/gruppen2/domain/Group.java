package mops.gruppen2.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import mops.gruppen2.domain.Exceptions.UserAlreadyExistsException;
import mops.gruppen2.domain.Exceptions.UserNotFoundException;
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
    private final List<User> members;
    private final Map<String, Role> roles;

    private GroupType type;
    private Visibility visibility;
    private Long parent;

    public Group() {
        this.members = new ArrayList<>();
        this.roles = new HashMap<>();
    }

    private void applyEvent(CreateGroupEvent event) {
        id = event.getGroup_id();
        visibility = event.getGroupVisibility();
        parent = event.getGroupParent();
        type = event.getGroupType();
    }

    private void applyEvent(UpdateRoleEvent event) throws UserNotFoundException {
        User user;

        Optional<User> userOptional = members.stream()
                .filter(u -> u.getUser_id().equals(event.getUser_id()))
                .findFirst();

        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            throw new UserNotFoundException("Nutzer wurde nicht gefunden!");
        }

        if (roles.containsKey(user) && event.getNewRole() == Role.MEMBER) {
            roles.remove(user);
        } else {
            roles.put(user.getUser_id(), event.getNewRole());
        }
    }

    private void applyEvent(AddUserEvent event) throws UserAlreadyExistsException {
        User user = new User(event.getUser_id(), event.getGivenname(), event.getFamilyname(), event.getEmail());

        if (!this.members.contains(user)) {
            this.members.add(user);
        } else {
            throw new UserAlreadyExistsException("Nutzer bereits in Gruppe vorhanden!");
        }
    }

    private void applyEvent(UpdateGroupTitleEvent event) {
        this.title = event.getNewGroupTitle();
    }

    private void applyEvent(UpdateGroupDescriptionEvent event) {
        this.description = event.getNewGroupDescription();
    }

    private void applyEvent(DeleteUserEvent event) throws UserNotFoundException {
        User user = new User(event.getUser_id(), "", "", "");

        if (this.members.contains(user)) {
            this.members.remove(user);
        } else {
            throw new UserNotFoundException("Nutzer wurde nicht gefunden!");
        }
    }
}
