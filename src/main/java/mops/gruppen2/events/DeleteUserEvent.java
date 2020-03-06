package mops.gruppen2.events;

import lombok.Getter;

@Getter
public class DeleteUserEvent extends Event{

    public DeleteUserEvent(long id, long gruppe_id, long user_id) {
        super(id, gruppe_id, user_id);
    }
}
