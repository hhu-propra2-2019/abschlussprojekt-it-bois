package mops.gruppen2.config;

import mops.gruppen2.service.EventService;
import mops.gruppen2.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Gruppen2Config {

    @Autowired
    GroupService groupService;
    @Autowired
    EventService eventService;
}
