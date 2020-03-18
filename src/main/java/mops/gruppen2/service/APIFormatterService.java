package mops.gruppen2.service;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.api.GroupRequestWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class APIFormatterService {

    static public GroupRequestWrapper wrap(Long status, List<Group> groupList) {
        return new GroupRequestWrapper(status, groupList);
    }
}
