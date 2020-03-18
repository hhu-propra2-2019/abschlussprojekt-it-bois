package mops.gruppen2.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.apiWrapper.UpdatedGroupRequestMapper;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.service.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Ein Beispiel für eine API mit Swagger.
 */
@RestController
@RequestMapping("/gruppen2/api")
public class APIController {

    private final SerializationService serializationService;
    private final EventService eventService;
    private final GroupService groupService;

    public APIController(SerializationService serializationService, EventService eventService, GroupService groupService) {
        this.serializationService = serializationService;
        this.eventService = eventService;
        this.groupService = groupService;
    }

    @GetMapping("/updateGroups/{status}")
    @Secured("ROLE_api_user")
    @ApiOperation(value = "Gibt alle Gruppen zurück in denen sich etwas geändert hat")
    public UpdatedGroupRequestMapper updateGroup(@ApiParam("Letzter Status des Anfragestellers")  @PathVariable Long status) throws EventException {
        List<Event> events = eventService.getNewEvents(status);
        UpdatedGroupRequestMapper updatedGroupRequestMapper = APIFormatterService.wrapp(eventService.getMaxEvent_id(), groupService.projectEventList(events));

        return updatedGroupRequestMapper;
    }

    @GetMapping("/getGroupIdsOfUser/{teilnehmer}")
    @Secured("ROLE_api_user")
    @ApiOperation(value = "Gibt alle Gruppen zurück in denen sich ein Teilnehmer befindet")
    public List<Long> getGroupsOfUser(@ApiParam("Teilnehmer dessen groupIds zurückgegeben werden sollen")  @PathVariable String teilnehmer) throws EventException {
        return eventService.getGroupsOfUser(teilnehmer);
    }

    @GetMapping("/getGroup/{groupId}")
    @Secured("ROLE_api_user")
    @ApiOperation(value = "Gibt die Gruppe mit der als Parameter mitgegebenden groupId zurück")
    public Group getGroupFromId(@ApiParam("GruppenId der gefordeten Gruppe") @PathVariable Long groupId) throws EventException{
        List<Event> eventList = eventService.getEventsOfGroup(groupId);

        List<Group> groups = groupService.projectEventList(eventList);
        return groups.get(0);
    }

}
