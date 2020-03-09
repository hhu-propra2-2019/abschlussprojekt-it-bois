package mops.gruppen2.domain.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        include = JsonTypeInfo.As.PROPERTY,
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AddUserEvent.class, name = "AddUserEvent"),
        @JsonSubTypes.Type(value = AddUserEvent.class, name = "CreateGroupEvent"),
        @JsonSubTypes.Type(value = AddUserEvent.class, name = "DeleteUserEvent"),
        @JsonSubTypes.Type(value = AddUserEvent.class, name = "UpdateGroupDescriptionEvent"),
        @JsonSubTypes.Type(value = AddUserEvent.class, name = "UpdateGroupTitleEvent"),
        @JsonSubTypes.Type(value = AddUserEvent.class, name = "UpdateRoleEvent"),
})
public class Event {
    long event_id;
    long group_id;
    String user_id;
}
