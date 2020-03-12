package mops.gruppen2.domain.apiWrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import mops.gruppen2.domain.Group;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatedGroupRequestMapper {
    private Long status;
    private List<Group> groupList;
}
