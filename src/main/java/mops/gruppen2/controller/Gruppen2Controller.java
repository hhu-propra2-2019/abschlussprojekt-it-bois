package mops.gruppen2.controller;

import mops.gruppen2.config.Gruppen2Config;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.security.Account;
import mops.gruppen2.service.ControllerService;
import mops.gruppen2.service.CsvService;
import mops.gruppen2.service.GroupService;
import mops.gruppen2.service.InviteLinkRepositoryService;
import mops.gruppen2.service.KeyCloakService;
import mops.gruppen2.service.UserService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    private final KeyCloakService keyCloakService;
    private final GroupService groupService;
    private final UserService userService;
    private final ControllerService controllerService;
    private final InviteLinkRepositoryService inviteLinkRepositoryService;
    @Autowired
    Gruppen2Config gruppen2Config;

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
                                 @RequestParam("title") String title,
                                 @RequestParam("beschreibung") String beschreibung,
                                 @RequestParam(value = "visibility", required = false) Boolean visibility,
                                 @RequestParam(value = "file", required = false) MultipartFile file) throws IOException, EventException {

        Account account = keyCloakService.createAccountFromPrincipal(token);
        List<User> userList = new ArrayList<>();
        if (!file.isEmpty()) {
            userList = CsvService.read(file.getInputStream());
        }
        visibility = visibility == null;
        controllerService.createLecture(account, title, beschreibung, visibility, userList);

        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_actuator)"})
    @PostMapping("/details/members/addUsersFromCsv")
    public String addUsersFromCsv(@RequestParam("group_id") Long groupId,
                                  @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        List<User> userList = new ArrayList<>();
        if (!file.isEmpty()) {
            userList = CsvService.read(file.getInputStream());
        }
        controllerService.addUserList(userList, groupId);
        return "redirect:/gruppen2/details/members/" + groupId;
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @GetMapping("/createGroup")
    public String createGroup(KeycloakAuthenticationToken token, Model model) {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        return "create";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/findGroup")
    public String findGroup(KeycloakAuthenticationToken token, Model model, @RequestParam(value = "suchbegriff", required = false) String search) throws EventException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        List<Group> groupse = new ArrayList<>();
        if (search != null) {
            groupse = groupService.findGroupWith(search, account);
        }
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        model.addAttribute("gruppen", groupse);
        return "search";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/createGroup")
    public String pCreateGroup(KeycloakAuthenticationToken token,
                               @RequestParam("title") String title,
                               @RequestParam("beschreibung") String beschreibung,
                               @RequestParam(value = "visibility", required = false) Boolean visibility) throws EventException {

        Account account = keyCloakService.createAccountFromPrincipal(token);
        visibility = visibility == null;
        controllerService.createGroup(account, title, beschreibung, visibility);

        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @GetMapping("/details/{id}")
    public String showGroupDetails(KeycloakAuthenticationToken token, Model model, @PathVariable("id") Long groupId) throws EventException, ResponseStatusException {

        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        Group group = userService.getGroupById(groupId);
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        if (group != null) {
            model.addAttribute("group", group);
            model.addAttribute("roles", group.getRoles());
            model.addAttribute("user", user);
            model.addAttribute("admin", Role.ADMIN);
            return "detailsMember";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/detailsBeitreten")
    public String joinGroup(KeycloakAuthenticationToken token, Model model, @RequestParam("id") Long groupId) throws EventException {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        Group group = userService.getGroupById(groupId);
        if (group.getMembers().contains(user)) {
            return "errorRenameLater"; //hier soll eigentlich auf die bereits beigetretene Gruppe weitergeleitet werden
        }
        controllerService.addUser(account, groupId);
        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/detailsSearch")
    public String showGroupDetailsNoMember(KeycloakAuthenticationToken token, Model model, @RequestParam("id") Long groupId) throws EventException {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        Group group = userService.getGroupById(groupId);
        if (group != null) {
            model.addAttribute("group", group);
            return "detailsNoMember";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
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
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found");
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/leaveGroup")
    public String pLeaveGroup(KeycloakAuthenticationToken token, @RequestParam("group_id") Long groupId) throws EventException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        controllerService.deleteUser(user.getId(), groupId);
        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @GetMapping("/details/members/{id}")
    public String editMembers(Model model, KeycloakAuthenticationToken token, @PathVariable("id") Long groupId) throws EventException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        Group group = userService.getGroupById(groupId);
        if (group.getRoles().get(account.getName()) == Role.ADMIN) {
            model.addAttribute("account", account);
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
    public String changeRole(KeycloakAuthenticationToken token, @RequestParam("group_id") Long groupId,
                             @RequestParam("user_id") String userId) throws EventException {
        controllerService.updateRole(userId, groupId);
        return "redirect:/gruppen2/details/members/" + groupId;
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @PostMapping("/details/members/deleteUser")
    public String deleteUser(KeycloakAuthenticationToken token, @RequestParam("group_id") Long groupId,
                             @RequestParam("user_id") String userId) throws EventException {
        controllerService.deleteUser(userId, groupId);
        return "redirect:/gruppen2/details/members/" + groupId;
    }
}
