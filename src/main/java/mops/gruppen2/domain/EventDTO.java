package mops.gruppen2.domain;

import lombok.Value;
import org.springframework.data.relational.core.mapping.Table;

@Value
@Table("event")
public class EventDTO {
    long event_id;
    long group_id;
    String user_id;
    String event_payload;
}
