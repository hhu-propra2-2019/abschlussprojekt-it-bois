package mops.gruppen2.domain.event;

import lombok.*;

/**
 * Entfernt ein einzelnes Mitglied einer Gruppe.
 */
@Getter
public class DeleteUserEvent extends Event {

    public DeleteUserEvent(Long event_id, Long group_id, String user_id) {
        super(event_id, group_id, user_id);
    }
}
