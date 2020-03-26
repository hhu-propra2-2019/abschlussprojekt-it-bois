package mops.gruppen2.domain.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mops.gruppen2.domain.Group;

import java.util.List;

/**
 * Kombiniert den Status und die Gruppenliste zur ausgabe über die API.
 */
@AllArgsConstructor
@Getter
public class GroupRequestWrapper {

    private final Long status;
    private final List<Group> groupList;
}
