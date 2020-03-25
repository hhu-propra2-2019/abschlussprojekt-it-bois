package mops.gruppen2.controller;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.domain.exception.PageNotFoundException;
import mops.gruppen2.security.Account;
import mops.gruppen2.service.*;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@SessionScope
@RequestMapping("/gruppen2")
public class WebController {

    private final KeyCloakService keyCloakService;
    private final GroupService groupService;
    private final UserService userService;
    private final ControllerService controllerService;
    private final ValidationService validationService;

    public WebController(KeyCloakService keyCloakService, GroupService groupService, UserService userService, ControllerService controllerService, ValidationService validationService) {
        this.keyCloakService = keyCloakService;
        this.groupService = groupService;
        this.userService = userService;
        this.controllerService = controllerService;
        this.validationService = validationService;
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
    @GetMapping("/createOrga")
    public String createOrga(KeycloakAuthenticationToken token, Model model) {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        model.addAttribute("account", account);
        model.addAttribute("lectures", groupService.getAllLecturesWithVisibilityPublic());
        return "createOrga";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_actuator)"})
    @PostMapping("/createOrga")
    public String pCreateOrga(KeycloakAuthenticationToken token,
                              @RequestParam("title") String title,
                              @RequestParam("description") String description,
                              @RequestParam(value = "visibility", required = false) Boolean visibility,
                              @RequestParam(value = "lecture", required = false) Boolean lecture,
                              @RequestParam("userMaximum") Long userMaximum,
                              @RequestParam(value = "maxInfiniteUsers", required = false) Boolean maxInfiniteUsers,
                              @RequestParam(value = "parent", required = false) String parent,
                              @RequestParam(value = "file", required = false) MultipartFile file) throws IOException, EventException {

        Account account = keyCloakService.createAccountFromPrincipal(token);
        UUID parentUUID = controllerService.getUUID(parent);
        List<User> userList = new ArrayList<>();
        validationService.checkFields(description, title, userMaximum, maxInfiniteUsers);
        Group group = userService.getGroupById(controllerService.createOrga(account, title, description, visibility, lecture, maxInfiniteUsers, userMaximum, parentUUID, file));
        userList = validationService.checkFile(file, userList, group.getId().toString(), group, account);
        controllerService.addUserList(userList, group.getId());
        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_studentin"})
    @GetMapping("/createStudent")
    public String createStudent(KeycloakAuthenticationToken token, Model model) {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        model.addAttribute("account", account);
        model.addAttribute("lectures", groupService.getAllLecturesWithVisibilityPublic());
        return "createStudent";
    }

    @RolesAllowed({"ROLE_studentin"})
    @PostMapping("/createStudent")
    public String pCreateStudent(KeycloakAuthenticationToken token,
                                 @RequestParam("title") String title,
                                 @RequestParam("description") String description,
                                 @RequestParam(value = "visibility", required = false) Boolean visibility,
                                 @RequestParam("userMaximum") Long userMaximum,
                                 @RequestParam(value = "maxInfiniteUsers", required = false) Boolean maxInfiniteUsers,
                                 @RequestParam(value = "parent", required = false) String parent) throws EventException {

        Account account = keyCloakService.createAccountFromPrincipal(token);
        UUID parentUUID = controllerService.getUUID(parent);
        validationService.checkFields(description, title, userMaximum, maxInfiniteUsers);
        controllerService.createGroup(account, title, description, visibility, maxInfiniteUsers, userMaximum, parentUUID);
        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_actuator)"})
    @PostMapping("/details/members/addUsersFromCsv")
    public String addUsersFromCsv(KeycloakAuthenticationToken token,
                                  @RequestParam("group_id") String groupId,
                                  @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        List<User> userList = new ArrayList<>();
        Group group = userService.getGroupById(UUID.fromString(groupId));
        userList = validationService.checkFile(file, userList, groupId, group, account);
        UUID groupUUID = controllerService.getUUID(groupId);
        controllerService.addUserList(userList, groupUUID);
        return "redirect:/gruppen2/details/members/" + groupId;
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/details/changeMetadata/{id}")
    public String changeMetadata(KeycloakAuthenticationToken token, Model model, @PathVariable("id") String groupId) {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        Group group = userService.getGroupById(UUID.fromString(groupId));
        validationService.checkIfAdmin(group, user);
        model.addAttribute("account", account);
        UUID parentId = group.getParent();
        Group parent = new Group();
        if (!validationService.checkIfUserInGroup(group, user)) {
            model.addAttribute("group", group);
            model.addAttribute("parentId", parentId);
            model.addAttribute("parent", parent);
            return "detailsNoMember";
        }
        model.addAttribute("title", group.getTitle());
        model.addAttribute("description", group.getDescription());
        model.addAttribute("admin", Role.ADMIN);
        model.addAttribute("roles", group.getRoles());
        model.addAttribute("groupId", group.getId());
        model.addAttribute("user", user);
        return "changeMetadata";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/details/changeMetadata")
    public String pChangeMetadata(KeycloakAuthenticationToken token,
                                  @RequestParam("title") String title,
                                  @RequestParam("description") String description,
                                  @RequestParam("groupId") String groupId) throws EventException {

        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        Group group = userService.getGroupById(UUID.fromString(groupId));
        validationService.checkIfAdmin(group, user);
        validationService.checkTitleAndDescription(title, description, account, groupId);
        return "redirect:/gruppen2/details/" + groupId;
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/findGroup")
    public String findGroup(KeycloakAuthenticationToken token,
                            Model model,
                            @RequestParam(value = "suchbegriff", required = false) String search) throws EventException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        List<Group> groups = new ArrayList<>();
        groups = validationService.checkSearch(search, groups, account);
        model.addAttribute("account", account);
        model.addAttribute("gruppen", groups);
        return "search";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @GetMapping("/details/{id}")
    public String showGroupDetails(KeycloakAuthenticationToken token, Model model, HttpServletRequest request, @PathVariable("id") String groupId) throws EventException {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));

        Group group = userService.getGroupById(UUID.fromString(groupId));
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        UUID parentId = group.getParent();

        validationService.checkGroup(group.getTitle());
        Group parent = validationService.checkParent(parentId);

        if (!validationService.checkIfUserInGroup(group, user)) {
            model.addAttribute("group", group);
            model.addAttribute("parentId", parentId);
            model.addAttribute("parent", parent);
            return "detailsNoMember";
        }

        model.addAttribute("parentId", parentId);
        model.addAttribute("parent", parent);
        model.addAttribute("group", group);
        model.addAttribute("roles", group.getRoles());
        model.addAttribute("user", user);
        model.addAttribute("admin", Role.ADMIN);

        String URL = request.getRequestURL().toString();
        String serverURL = URL.substring(0, URL.indexOf("gruppen2/"));
        model.addAttribute("link", serverURL + "gruppen2/acceptinvite/" + groupId);

        return "detailsMember";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/detailsBeitreten")
    public String joinGroup(KeycloakAuthenticationToken token,
                            Model model, @RequestParam("id") String groupId) throws EventException {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        Group group = userService.getGroupById(UUID.fromString(groupId));
        validationService.checkIfUserInGroupJoin(group, user);
        validationService.checkIfGroupFull(group);
        controllerService.addUser(account, UUID.fromString(groupId));
        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/detailsSearch")
    public String showGroupDetailsNoMember(KeycloakAuthenticationToken token,
                                           Model model,
                                           @RequestParam("id") String groupId) throws EventException {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        Group group = userService.getGroupById(UUID.fromString(groupId));
        UUID parentId = group.getParent();
        Group parent = validationService.checkParent(parentId);

        validationService.checkIfGroupFull(group);
        model.addAttribute("group", group);
        model.addAttribute("parentId", parentId);
        model.addAttribute("parent", parent);

        return "detailsNoMember";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/acceptinvite/{groupId}")
    public String acceptInvite(KeycloakAuthenticationToken token,
                               Model model, @PathVariable String groupId) throws EventException {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        Group group = userService.getGroupById(UUID.fromString(groupId));
        validationService.checkGroup(group.getTitle());
        model.addAttribute("group", group);
        return "redirect:/gruppen2/detailsSearch?id=" + group.getId();
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/leaveGroup")
    public String pLeaveGroup(KeycloakAuthenticationToken token,
                              @RequestParam("group_id") String groupId) throws EventException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        controllerService.passIfLastAdmin(account, UUID.fromString(groupId));
        controllerService.deleteUser(user.getId(), UUID.fromString(groupId));
        validationService.checkIfGroupEmpty(groupId, user);
        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/deleteGroup")
    public String pDeleteGroup(KeycloakAuthenticationToken token,
                               @RequestParam("group_id") String groupId) {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        Group group = userService.getGroupById(UUID.fromString(groupId));
        validationService.checkIfAdmin(group, user);
        controllerService.deleteGroupEvent(user.getId(), UUID.fromString(groupId));
        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @GetMapping("/details/members/{id}")
    public String editMembers(Model model,
                              KeycloakAuthenticationToken token,
                              @PathVariable("id") String groupId) throws EventException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        Group group = userService.getGroupById(UUID.fromString(groupId));
        User user = new User(account.getName(), "", "", "");
        validationService.checkIfAdmin(group, user);
        model.addAttribute("account", account);
        model.addAttribute("members", group.getMembers());
        model.addAttribute("group", group);
        model.addAttribute("admin", Role.ADMIN);
        return "editMembers";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @PostMapping("/details/members/changeRole")
    public String changeRole(KeycloakAuthenticationToken token,
                             @RequestParam("group_id") String groupId,
                             @RequestParam("user_id") String userId) throws EventException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        if (validationService.checkIfDemotingSelf(userId, groupId, account)) {
            return "redirect:/gruppen2/details/" + groupId;
        }
        return "redirect:/gruppen2/details/members/" + groupId;
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @PostMapping("/details/members/changeMaximum")
    public String changeMaxSize(@RequestParam("maximum") Long maximum,
                                @RequestParam("group_id") String groupId,
                                KeycloakAuthenticationToken token) {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        controllerService.updateMaxUser(account, UUID.fromString(groupId), maximum);
        return "redirect:/gruppen2/details/members/" + groupId;
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @PostMapping("/details/members/deleteUser")
    public String deleteUser(@RequestParam("group_id") String groupId,
                             @RequestParam("user_id") String userId) throws EventException {
        User user = new User(userId, "", "", "");
        controllerService.deleteUser(userId, UUID.fromString(groupId));
        validationService.checkIfGroupEmpty(groupId, user);
        return "redirect:/gruppen2/details/members/" + groupId;
    }

    @GetMapping("*")
    public String defaultLink() throws EventException {
        throw new PageNotFoundException("\uD83D\uDE41");
    }
}
