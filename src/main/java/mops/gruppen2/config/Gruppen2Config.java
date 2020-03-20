package mops.gruppen2.config;

import mops.gruppen2.service.EventService;
import mops.gruppen2.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Gruppen2Config {

    final GroupService groupService;
    final EventService eventService;

    public Gruppen2Config(GroupService groupService, EventService eventService) {
        this.groupService = groupService;
        this.eventService = eventService;
    }
}
