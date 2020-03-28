package mops.gruppen2.domain.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.domain.exception.GroupIdMismatchException;

import java.util.UUID;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
                      @JsonSubTypes.Type(value = AddUserEvent.class, name = "AddUserEvent"),
                      @JsonSubTypes.Type(value = CreateGroupEvent.class, name = "CreateGroupEvent"),
                      @JsonSubTypes.Type(value = DeleteUserEvent.class, name = "DeleteUserEvent"),
                      @JsonSubTypes.Type(value = UpdateGroupDescriptionEvent.class, name = "UpdateGroupDescriptionEvent"),
                      @JsonSubTypes.Type(value = UpdateGroupTitleEvent.class, name = "UpdateGroupTitleEvent"),
                      @JsonSubTypes.Type(value = UpdateRoleEvent.class, name = "UpdateRoleEvent"),
                      @JsonSubTypes.Type(value = DeleteGroupEvent.class, name = "DeleteGroupEvent"),
                      @JsonSubTypes.Type(value = UpdateUserMaxEvent.class, name = "UpdateUserMaxEvent")
              })
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Event {

    protected UUID groupId;
    protected String userId;

    public void apply(Group group) throws EventException {
        checkGroupIdMatch(group.getId());
        applyEvent(group);
    }

    private void checkGroupIdMatch(UUID groupId) {
        if (groupId == null || this.groupId.equals(groupId)) {
            return;
        }

        throw new GroupIdMismatchException(getClass().toString());
    }

    protected abstract void applyEvent(Group group) throws EventException;
}
