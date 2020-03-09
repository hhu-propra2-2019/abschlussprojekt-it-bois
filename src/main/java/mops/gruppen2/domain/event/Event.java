package mops.gruppen2.domain.event;

import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@NonFinal
public class Event {
    long event_id;
    long group_id;
    String user_id;
}
