package mops.gruppen2.service;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.apiWrapper.UpdatedGroupRequestMapper;

import java.util.List;

public class APIFormatter {
    static public UpdatedGroupRequestMapper wrapp(Long status, List<Group> groupList){
        return new UpdatedGroupRequestMapper(status, groupList);
    }
}
