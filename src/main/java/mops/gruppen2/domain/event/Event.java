package mops.gruppen2.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Value;
import lombok.experimental.NonFinal;

@Value
@NonFinal
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class Event {
    long event_id;
    long group_id;
    String user_id;
}
