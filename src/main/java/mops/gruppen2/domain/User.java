package mops.gruppen2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
	String user_id;
	String givenname;
	String familyname;
	String email;
}
