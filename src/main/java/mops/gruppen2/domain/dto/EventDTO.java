package mops.gruppen2.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("event")
@Getter
@AllArgsConstructor
public class EventDTO {

    @Id
    Long event_id;
    String group_id;
    String user_id;
    String event_type;
    String event_payload;
}
