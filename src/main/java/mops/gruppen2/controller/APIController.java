package mops.gruppen2.controller;


import com.github.javafaker.Faker;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.ProductSwaggerExample;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.service.EventService;
import mops.gruppen2.service.GroupService;
import mops.gruppen2.service.SerializationService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Ein Beispiel für eine API mit Swagger.
 */
@RestController
@RequestMapping("/gruppen2")
public class APIController {

    private final SerializationService serializationService;
    private final EventService eventService;
    private final GroupService groupService;

    public APIController(SerializationService serializationService, EventService eventService, GroupService groupService) {
        this.serializationService = serializationService;
        this.eventService = eventService;
        this.groupService = groupService;
    }

    @GetMapping("/updatedGroups/{status}")
    @ApiOperation(value = "Gibt alle Gruppen zurück in denen sich etwas geändert hat")
    public List<Group> updateGroup(@ApiParam("Status des Anfragestellers")  @PathVariable Long status) throws EventException {
        List<Event> events = eventService.getNewEvents(status);
        return groupService.projectEventList(events);
    }


}
