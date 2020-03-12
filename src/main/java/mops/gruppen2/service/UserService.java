package mops.gruppen2.service;

import mops.gruppen2.domain.EventDTO;
import mops.gruppen2.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    final EventRepository eventRepository;

    public UserService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Long> getUserGroups(Long user_id) {
        List<Long> group_ids = eventRepository.findGroup_idsWhereUser_id(user_id);

        return null;
    }
}
