package mops.gruppen2.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.api.GroupRequestWrapper;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.service.APIFormatterService;
import mops.gruppen2.service.EventService;
import mops.gruppen2.service.GroupService;
import mops.gruppen2.service.UserService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Ein Beispiel für eine API mit Swagger.
 */
@RestController
@RequestMapping("/gruppen2/api")
public class APIController {

    private final EventService eventService;
    private final GroupService groupService;
    private final UserService userService;

    public APIController(EventService eventService, GroupService groupService, UserService userService) {
        this.eventService = eventService;
        this.groupService = groupService;
        this.userService = userService;
    }

    @GetMapping("/updateGroups/{status}")
    @Secured("ROLE_api_user")
    @ApiOperation("Gibt alle Gruppen zurück in denen sich etwas geändert hat")
    public GroupRequestWrapper updateGroup(@ApiParam("Letzter Status des Anfragestellers") @PathVariable Long status) throws EventException {
        List<Event> events = eventService.getNewEvents(status);

        return APIFormatterService.wrap(eventService.getMaxEvent_id(), groupService.projectEventList(events));
    }

    @GetMapping("/getGroupIdsOfUser/{teilnehmer}")
    @Secured("ROLE_api_user")
    @ApiOperation("Gibt alle Gruppen zurück in denen sich ein Teilnehmer befindet")
    public List<String> getGroupsOfUser(@ApiParam("Teilnehmer dessen groupIds zurückgegeben werden sollen") @PathVariable String teilnehmer) {
        return userService.getUserGroups(teilnehmer).stream()
                          .map(group -> group.getId().toString())
                          .collect(Collectors.toList());
    }

    @GetMapping("/getGroup/{groupId}")
    @Secured("ROLE_api_user")
    @ApiOperation("Gibt die Gruppe mit der als Parameter mitgegebenden groupId zurück")
    public Group getGroupFromId(@ApiParam("GruppenId der gefordeten Gruppe") @PathVariable String groupId) throws EventException {
        List<Event> eventList = eventService.getEventsOfGroup(UUID.fromString(groupId));
        List<Group> groups = groupService.projectEventList(eventList);

        return groups.get(0);
    }

}
