package mops.gruppen2.controller;

import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.service.EventService;
import mops.gruppen2.service.GroupService;
import mops.gruppen2.service.KeyCloakService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.swing.*;

@Controller
@RequestMapping("/gruppen2")
public class Gruppen2Controller {

    private final KeyCloakService keyCloakService;
    private final EventService eventService;
    private final GroupService groupService;

    public Gruppen2Controller(KeyCloakService keyCloakService, EventService eventService, GroupService groupService) {
        this.keyCloakService = keyCloakService;
        this.eventService = eventService;
        this.groupService = groupService;
    }

    /**Zeigt die index.html an.
     *
     * @param token toller token
     * @param model tolles model
     * @return index.html
     */
    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @GetMapping("")
    public String index(KeycloakAuthenticationToken token, Model model) {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        return "index";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @GetMapping("/createGroup")
    public String createGroup(KeycloakAuthenticationToken token, Model model) {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        return "create";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @GetMapping("/findGroup")
    public String findGroup(KeycloakAuthenticationToken token, Model model) {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        return "search";
    }

    @PostMapping("/createGroup")
    public String pCreateGroup(KeycloakAuthenticationToken token,
                               @RequestParam(value = "title") String title,
                               @RequestParam(value = "beschreibung") String beschreibung) {

        //Hier muss alles in eine separate Klasse
        CreateGroupEvent createGroupEvent = new CreateGroupEvent(eventService.checkGroup(), "faker", title, beschreibung);
        eventService.saveEvent(createGroupEvent);
        groupService.buildGroupFromEvent(createGroupEvent);

        return "redirect:/";
    }

}
