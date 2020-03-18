package mops.gruppen2.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Exceptions.NoValueException;
import mops.gruppen2.domain.Group;

/**
 * Ändert nur die Gruppenbeschreibung.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor // For Jackson
public class UpdateGroupDescriptionEvent extends Event {

    private String newGroupDescription;

    public UpdateGroupDescriptionEvent(Long groupId, String userId, String newGroupDescription) {
        super(groupId, userId);
        this.newGroupDescription = newGroupDescription;
    }

    @Override
    public void applyEvent(Group group) {
        if (this.newGroupDescription.isEmpty()) {
            throw new NoValueException(this.getClass().toString());
        }

        group.setDescription(this.newGroupDescription);
    }
}
