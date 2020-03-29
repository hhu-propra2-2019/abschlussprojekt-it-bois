package mops.gruppen2.domain;

import lombok.Value;

import java.util.Set;

@Value
public class Account {

    String name; //user_id
    String email;
    String image;
    String givenname;
    String familyname;
    Set<String> roles;
}
