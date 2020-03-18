package mops.gruppen2.security;

import lombok.Value;

import java.util.Set;

@Value
public class Account {

    String email;
    String image;
    String name; //user_id
    String givenname;
    String familyname;
    Set<String> roles;
}
