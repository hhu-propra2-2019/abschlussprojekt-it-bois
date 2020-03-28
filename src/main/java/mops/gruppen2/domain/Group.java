package mops.gruppen2.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Repräsentiert den aggregierten Zustand einer Gruppe.
 */
@Getter
@Setter
public class Group {

    //TODO: List to Hashmap
    private final List<User> members;
    private final Map<String, Role> roles;
    private UUID id;
    private String title;
    private String description;
    private Long userMaximum;
    private GroupType type;
    private Visibility visibility;
    private UUID parent;

    public Group() {
        members = new ArrayList<>();
        roles = new HashMap<>();
    }

}
