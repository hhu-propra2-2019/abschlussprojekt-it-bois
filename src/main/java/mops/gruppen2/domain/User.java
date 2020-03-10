package mops.gruppen2.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"givenname", "familyname", "email"})
public class User {

    String user_id;

    String givenname;
    String familyname;
    String email;

    List<Long> group_ids;

    public void addGroup(Long group_id){
        group_ids.add(group_id);
    }
}
