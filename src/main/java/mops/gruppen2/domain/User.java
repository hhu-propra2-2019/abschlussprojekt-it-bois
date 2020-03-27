package mops.gruppen2.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.security.Account;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"givenname", "familyname", "email"})
public class User {

    private String id;
    private String givenname;
    private String familyname;
    private String email;

    public User(Account account) {
        this.id = account.getName();
        this.givenname = account.getGivenname();
        this.familyname = account.getFamilyname();
        this.email = account.getEmail();
    }
}
