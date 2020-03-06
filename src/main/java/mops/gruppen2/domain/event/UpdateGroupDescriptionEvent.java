package mops.gruppen2.domain.event;

import lombok.Getter;

@Getter
public class UpdateGroupDescriptionEvent extends Event {
    String beschreibung;

    public UpdateGroupDescriptionEvent(long id, long gruppe_id, String user_id, String beschreibung) {
        super(id, gruppe_id, user_id);
        this.beschreibung = beschreibung;
    }
}
