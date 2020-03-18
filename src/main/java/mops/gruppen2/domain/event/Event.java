package mops.gruppen2.domain.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.exception.EventException;


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
              })
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Event {

    protected Long groupId;
    protected String userId;

    public void apply(Group group) throws EventException {
        checkGroupIdMatch(group.getId());
        applyEvent(group);
    }

    protected abstract void applyEvent(Group group) throws EventException;

    private void checkGroupIdMatch(Long groupId) {
        if (groupId == null || this.group_id.equals(groupId)) {
            return;
        }

        throw new GroupIdMismatchException(this.getClass().toString());
    }
}
