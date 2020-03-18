package mops.gruppen2.domain.dto;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("event")
@Value
public class EventDTO {

    @Id
    Long event_id;
    Long group_id;
    String user_id;
    String event_payload;
    boolean visibility;
}
