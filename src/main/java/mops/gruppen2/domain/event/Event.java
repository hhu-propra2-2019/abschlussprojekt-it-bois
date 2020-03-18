package mops.gruppen2.domain.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Exceptions.GroupIdMismatchException;
import mops.gruppen2.domain.Group;

@JsonTypeInfo(
        include = JsonTypeInfo.As.PROPERTY,
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
@Setter
@NoArgsConstructor // Needed by Lombok in Subclasses
@AllArgsConstructor
public abstract class Event {

    protected Long group_id;
    protected String user_id;

    public void apply(Group group) throws EventException {
        checkGroupIdMatch(group.getId());
        applyEvent(group);
    }

    protected abstract void applyEvent(Group group) throws EventException;

    private void checkGroupIdMatch(Long group_id) {
        if (group_id == null || this.group_id.equals(group_id)) {
            return;
        }

        throw new GroupIdMismatchException(this.getClass().toString());
    }
}
