package mops.gruppen2.Security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class Account {
    private final String name;
    private final String email;
    private final String givenname;
    private final String familyname;
    private final Set<String> roles;
}
