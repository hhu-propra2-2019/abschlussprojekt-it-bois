package mops.gruppen2.domain.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@AllArgsConstructor
@NoArgsConstructor
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
@Setter
public class Event {
    Long group_id;
    String user_id;


    public Event(Long group_id,String user_id){
        this.group_id = group_id;
        this.user_id = user_id;
    }
}
