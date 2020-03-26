package mops.gruppen2.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.exception.NoValueException;

import java.util.UUID;

/**
 * Ändert nur die Gruppenbeschreibung.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor // For Jackson
public class UpdateGroupDescriptionEvent extends Event {

    private String newGroupDescription;

    public UpdateGroupDescriptionEvent(UUID groupId, String userId, String newGroupDescription) {
        super(groupId, userId);
        this.newGroupDescription = newGroupDescription.trim();
    }

    @Override
    protected void applyEvent(Group group) {
        if (this.newGroupDescription.isEmpty()) {
            throw new NoValueException(this.getClass().toString());
        }

        group.setDescription(this.newGroupDescription);
    }
}
