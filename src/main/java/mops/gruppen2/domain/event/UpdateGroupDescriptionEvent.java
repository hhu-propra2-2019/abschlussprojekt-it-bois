package mops.gruppen2.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;

/**
 * Ändert nur die Gruppenbeschreibung.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGroupDescriptionEvent extends Event {

    String newGroupDescription;

    public UpdateGroupDescriptionEvent(Long group_id, String user_id, String newGroupDescription) {
        super(group_id, user_id);
        this.newGroupDescription = newGroupDescription;
    }

    public void apply(Group group) {
        group.setDescription(this.newGroupDescription);
    }
}
