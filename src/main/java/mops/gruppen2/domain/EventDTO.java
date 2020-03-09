package mops.gruppen2.domain;

import lombok.Data;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("event")
@Data
public class EventDTO {
    @Id
    long event_id;
    long group_id;
    String user_id;
    String event_payload;
}
