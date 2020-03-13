package mops.gruppen2.service;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.apiWrapper.UpdatedGroupRequestMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class APIFormatterService {
    static public UpdatedGroupRequestMapper wrapp(Long status, List<Group> groupList){
        return new UpdatedGroupRequestMapper(status, groupList);
    }
}
