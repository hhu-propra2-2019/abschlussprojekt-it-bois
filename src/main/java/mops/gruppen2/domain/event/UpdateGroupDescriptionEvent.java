package mops.gruppen2.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Ändert nur die Gruppenbeschreibung.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateGroupDescriptionEvent extends Event {
    String newGroupDescription;

    public UpdateGroupDescriptionEvent(long event_id, long group_id, String user_id, String newGroupDescription) {
        super(event_id, group_id, user_id);
        this.newGroupDescription = newGroupDescription;
    }
}
