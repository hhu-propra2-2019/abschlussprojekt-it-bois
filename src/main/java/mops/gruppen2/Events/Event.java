package mops.gruppen2.Events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;

@Getter
@AllArgsConstructor
public class Event {
	@Id
	Long id;
	Long gruppe_id;
	Long user_id;

}
