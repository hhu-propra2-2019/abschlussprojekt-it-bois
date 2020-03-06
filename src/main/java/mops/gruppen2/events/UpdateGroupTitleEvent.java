package mops.gruppen2.events;

import lombok.Getter;

@Getter
public class UpdateGroupTitleEvent extends Event {
    String titel;

    public UpdateGroupTitleEvent(long id, long gruppe_id, String user_id, String titel) {
        super(id, gruppe_id, user_id);
        this.titel = titel;
    }
}
