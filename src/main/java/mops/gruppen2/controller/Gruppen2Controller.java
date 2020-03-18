package mops.gruppen2.controller;

import mops.gruppen2.config.Gruppen2Config;
import mops.gruppen2.domain.Exceptions.EventException;
import mops.gruppen2.domain.Exceptions.GroupNotFoundException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.User;
import mops.gruppen2.security.Account;
import mops.gruppen2.service.*;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.security.RolesAllowed;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@SessionScope
@RequestMapping("/gruppen2")
public class Gruppen2Controller {

    @Autowired
    Gruppen2Config gruppen2Config;

    private final KeyCloakService keyCloakService;
    private final GroupService groupService;
    private final UserService userService;
    private final ControllerService controllerService;
    private final InviteLinkRepositoryService inviteLinkRepositoryService;

    public Gruppen2Controller(KeyCloakService keyCloakService, GroupService groupService, UserService userService, ControllerService controllerService, InviteLinkRepositoryService inviteLinkRepositoryService) {
        this.keyCloakService = keyCloakService;
        this.groupService = groupService;
        this.userService = userService;
        this.controllerService = controllerService;
        this.inviteLinkRepositoryService = inviteLinkRepositoryService;
    }

    /**
     * Zeigt die index.html an.
     *
     * @param token toller token
     * @param model tolles model
     * @return index.html
     */
    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("")
    public String index(KeycloakAuthenticationToken token, Model model) throws EventException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        model.addAttribute("gruppen", userService.getUserGroups(user));
        model.addAttribute("user", user);
        return "index";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_actuator)"})
    @GetMapping("/createLecture")
    public String createLecture(KeycloakAuthenticationToken token, Model model) {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        return "createLecture";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_actuator)"})
    @PostMapping("/createLecture")
    public String pCreateLecture(KeycloakAuthenticationToken token,
                               @RequestParam(value = "title") String title,
                               @RequestParam(value = "beschreibung") String beschreibung,
                               @RequestParam(value = "visibility", required = false) Boolean visibility,
                               @RequestParam(value = "file") MultipartFile file) throws IOException, EventException {

        Account account = keyCloakService.createAccountFromPrincipal(token);
        List<User> userList = CsvService.read(file.getInputStream());
        visibility = visibility == null;
        controllerService.createLecture(account, title, beschreibung, visibility, userList);

        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @GetMapping("/createGroup")
    public String createGroup(KeycloakAuthenticationToken token, Model model) {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        return "create";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/findGroup")
    public String findGroup(KeycloakAuthenticationToken token, Model model, @RequestParam(value = "suchbegriff", required = false) String suchbegriff) throws EventException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        List<Group> groupse = new ArrayList<>();
        if (suchbegriff != null) {
            groupse = groupService.findGroupWith(suchbegriff,account);
        }
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        model.addAttribute("gruppen", groupse);
        return "search";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/createGroup")
    public String pCreateGroup(KeycloakAuthenticationToken token,
                               @RequestParam(value = "title") String title,
                               @RequestParam(value = "beschreibung") String beschreibung,
                               @RequestParam(value = "visibility", required = false) Boolean visibility) throws EventException {

        Account account = keyCloakService.createAccountFromPrincipal(token);
        visibility = visibility == null;
        controllerService.createGroup(account, title, beschreibung, visibility);

        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @GetMapping("/details/{id}")
    public String showGroupDetails(KeycloakAuthenticationToken token, Model model, @PathVariable (value="id") Long id) throws EventException, ResponseStatusException {

        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        Group group = userService.getGroupById(id);
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        if (group != null) {
            model.addAttribute("group", group);
            model.addAttribute("roles", group.getRoles());
            model.addAttribute("user", user);
            model.addAttribute("admin", Role.ADMIN);
            return "detailsMember";
        }

        throw new GroupNotFoundException(this.getClass().toString());
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/detailsBeitreten")
    public String joinGroup(KeycloakAuthenticationToken token, Model model, @RequestParam(value = "id") Long id) throws EventException {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        Account account = keyCloakService.createAccountFromPrincipal (token);
        User user = new User(account.getName(),account.getGivenname(),account.getFamilyname(),account.getEmail());
        Group group = userService.getGroupById(id);
        if(group.getMembers().contains(user)) return "errorRenameLater"; //hier soll eigentlich auf die bereits beigetretene Gruppe weitergeleitet werden
        controllerService.addUser(account,id);
        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/detailsSearch")
    public String showGroupDetailsNoMember(KeycloakAuthenticationToken token, Model model, @RequestParam(value = "id") Long id) throws EventException {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        Group group = userService.getGroupById(id);
        if (group != null) {
            model.addAttribute("group", group);
            return "detailsNoMember";
        }
        throw new GroupNotFoundException(this.getClass().toString());
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/acceptinvite/{link}")
    public String acceptInvite(KeycloakAuthenticationToken token, Model model, @PathVariable String link) throws EventException {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        Group group = userService.getGroupById(inviteLinkRepositoryService.findGroupIdByInvite(link));
        if (group != null) {
            model.addAttribute("group", group);
            return "redirect:/gruppen2/detailsSearch?id=" + group.getId();
        }
        throw new GroupNotFoundException(this.getClass().toString());
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/leaveGroup")
    public String pLeaveGroup(KeycloakAuthenticationToken token, @RequestParam (value="group_id") Long id) throws EventException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        controllerService.deleteUser(user.getUser_id(), id);
        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @GetMapping("/details/members/{id}")
    public String editMembers(Model model, KeycloakAuthenticationToken token, @PathVariable (value="id") Long id)  throws  EventException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        Group group = userService.getGroupById(id);
        if(group.getRoles().get(account.getName()) == Role.ADMIN) {
            model.addAttribute("members", group.getMembers());
            model.addAttribute("group", group);
            model.addAttribute("admin", Role.ADMIN);
            return "editMembers";
        } else {
            return "redirect:/details/";
        }
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @PostMapping("/details/members/changeRole")
    public String changeRole(KeycloakAuthenticationToken token, @RequestParam (value = "group_id") Long group_id,
                             @RequestParam (value = "user_id") String user_id) throws EventException {
        controllerService.updateRole(user_id, group_id);
        return "redirect:/gruppen2/details/members/" + group_id;
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @PostMapping("/details/members/deleteUser")
    public String deleteUser(KeycloakAuthenticationToken token,@RequestParam (value = "group_id") Long group_id,
                             @RequestParam (value = "user_id") String user_id) throws EventException {
        controllerService.deleteUser(user_id, group_id);
        return "redirect:/gruppen2/details/members/" + group_id;
    }

    @GetMapping("*")
    public String defaultLink() {
        return "error";
    }
}
