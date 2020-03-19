package mops.gruppen2.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repräsentiert den aggregierten Zustand einer Gruppe.
 */
@Getter
@Setter
public class Group {

    private final List<User> members;
    private final Map<String, Role> roles;
    private Long id;
    private String title;
    private String description;
    private Long userMaximum;
    private GroupType type;
    private Visibility visibility;
    private Long parent;

    public Group() {
        this.members = new ArrayList<>();
        this.roles = new HashMap<>();
    }

}
