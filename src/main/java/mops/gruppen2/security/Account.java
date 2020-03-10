package mops.gruppen2.security;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Account {
    private final String name; //user_id
    private final String email;
    private final String image;
    private final String givenname;
    private final String familyname;
    private final Set<String> roles;
}
