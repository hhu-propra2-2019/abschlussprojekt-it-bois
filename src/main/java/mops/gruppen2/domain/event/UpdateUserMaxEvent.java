package mops.gruppen2.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.exception.EventException;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserMaxEvent extends Event {

    private  Long userMaximum;

    public UpdateUserMaxEvent(UUID groupId, String userId, Long userMaximum) {
        super(groupId, userId);
        this.userMaximum = userMaximum;
    }

    @Override
    protected void applyEvent(Group group) throws EventException {
        group.setUserMaximum(this.userMaximum);
    }
}
