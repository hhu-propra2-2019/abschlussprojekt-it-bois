package mops.gruppen2.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"givenname", "familyname", "email"})
public class User {

    private final String id;
    private final String givenname;
    private final String familyname;
    private final String email;
}
