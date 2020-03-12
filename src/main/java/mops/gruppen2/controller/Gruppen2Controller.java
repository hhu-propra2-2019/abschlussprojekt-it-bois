package mops.gruppen2.controller;

import mops.gruppen2.config.Gruppen2Config;
import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.UpdateGroupDescriptionEvent;
import mops.gruppen2.domain.event.UpdateGroupTitleEvent;
import mops.gruppen2.security.Account;
import mops.gruppen2.service.*;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

@Controller
@RequestMapping("/gruppen2")
public class Gruppen2Controller {

    @Autowired
    Gruppen2Config gruppen2Config;

    private final KeyCloakService keyCloakService;
    private final EventService eventService;
    private final GroupService groupService;
    private final UserService userService;
    private final ControllerService controllerService;

    public Gruppen2Controller(KeyCloakService keyCloakService, EventService eventService, GroupService groupService, UserService userService, ControllerService controllerService) {
        this.keyCloakService = keyCloakService;
        this.eventService = eventService;
        this.groupService = groupService;
        this.userService = userService;
        this.controllerService = controllerService;
    }

    /**
     * Zeigt die index.html an.
     *
     * @param token toller token
     * @param model tolles model
     * @return index.html
     */
    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @GetMapping("")
    public String index(KeycloakAuthenticationToken token, Model model) throws EventException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());

        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        model.addAttribute("gruppen", userService.getUserGroups(user.getUser_id()));
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

        Account account = keyCloakService.createAccountFromPrincipal(token);
        controllerService.createGroup(account, title, beschreibung);

        return "redirect:/gruppen2";
    }

}
