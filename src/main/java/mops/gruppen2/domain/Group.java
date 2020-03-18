package mops.gruppen2.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import mops.gruppen2.domain.Exceptions.UserAlreadyExistsException;
import mops.gruppen2.domain.Exceptions.UserNotFoundException;
import mops.gruppen2.domain.event.*;

import java.util.*;

/**
 * Repräsentiert den aggregierten Zustand einer Gruppe.
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@AllArgsConstructor
public class Group {
    private long id;
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

}
