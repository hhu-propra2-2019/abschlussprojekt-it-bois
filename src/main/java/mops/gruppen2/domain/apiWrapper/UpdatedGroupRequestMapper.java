package mops.gruppen2.domain.apiWrapper;

import lombok.AllArgsConstructor;
import mops.gruppen2.domain.Group;

import java.util.List;

@AllArgsConstructor
public class UpdatedGroupRequestMapper {

    private final Long status;
    private final List<Group> groupList;
}
