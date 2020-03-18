package mops.gruppen2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"givenname", "familyname", "email"})
public class User {

    String user_id;
    String givenname;
    String familyname;
    String email;
}
