package mops.gruppen2.controller;

import mops.gruppen2.config.Gruppen2Config;
import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.security.Account;
import mops.gruppen2.service.*;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        model.addAttribute("user",user);
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
    public String findGroup(KeycloakAuthenticationToken token, Model model, @RequestParam(value = "suchbegriff", required = false) String suchbegriff) throws EventException {
        List<Group> groupse = new ArrayList<>();
        if(suchbegriff!=null) {
            groupse = groupService.findGroupWith(suchbegriff);
        }
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        model.addAttribute("gruppen",groupse);
        return "search";
    }


    @PostMapping("/createGroup")
    public String pCreateGroup(KeycloakAuthenticationToken token,
                               @RequestParam(value = "title") String title,
                               @RequestParam(value = "beschreibung") String beschreibung,
                               @RequestParam(value = "visibility", required = false) Boolean visibility) {

        Account account = keyCloakService.createAccountFromPrincipal(token);
        if (visibility == null) {
            visibility = true;
        }else{
            visibility = false;
        }
        controllerService.createGroup(account, title, beschreibung, visibility);

        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @GetMapping("/details")
    public String showGroupDetails(KeycloakAuthenticationToken token, Model model, @RequestParam (value="id") Long id) throws EventException, ResponseStatusException {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        Group group = userService.getGroupById(id);
        Account account = keyCloakService.createAccountFromPrincipal (token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        if(group!= null) {
            model.addAttribute("group", group);
            model.addAttribute("role", group.getRoles().get(user.getUser_id()));
            return "detailsMember";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
    }

}
