package mops.gruppen2.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Exceptions.NoValueException;
import mops.gruppen2.domain.Group;

/**
 * Ändert nur den Gruppentitel.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor // For Jackson
public class UpdateGroupTitleEvent extends Event {

    private String newGroupTitle;

    public UpdateGroupTitleEvent(Long groupId, String userId, String newGroupTitle) {
        super(groupId, userId);
        this.newGroupTitle = newGroupTitle;
    }

    @Override
    public void applyEvent(Group group) {
        if (this.getNewGroupTitle().isEmpty()) {
            throw new NoValueException(this.getClass().toString());
        }

        group.setTitle(this.newGroupTitle);
    }

}
