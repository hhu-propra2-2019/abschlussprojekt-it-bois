package mops.gruppen2.controller;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.domain.exception.GroupFullException;
import mops.gruppen2.domain.exception.GroupNotFoundException;
import mops.gruppen2.domain.exception.NoAccessException;
import mops.gruppen2.domain.exception.NoAdminAfterActionException;
import mops.gruppen2.domain.exception.PageNotFoundException;
import mops.gruppen2.domain.exception.UserAlreadyExistsException;
import mops.gruppen2.domain.exception.WrongFileException;
import mops.gruppen2.security.Account;
import mops.gruppen2.service.ControllerService;
import mops.gruppen2.service.CsvService;
import mops.gruppen2.service.GroupService;
import mops.gruppen2.service.KeyCloakService;
import mops.gruppen2.service.UserService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.io.CharConversionException;
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

    public WebController(KeyCloakService keyCloakService, GroupService groupService, UserService userService, ControllerService controllerService) {
        this.keyCloakService = keyCloakService;
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
        controllerService.createOrga(account, title, description, visibility, lecture, maxInfiniteUsers, userMaximum, parentUUID, file);
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
        visibility = visibility == null;
        maxInfiniteUsers = maxInfiniteUsers != null;
        UUID parentUUID = controllerService.getUUID(parent);
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
        if (!file.isEmpty()) {
            try {
                userList = CsvService.read(file.getInputStream());
                if (userList.size() + group.getMembers().size() > group.getUserMaximum()) {
                    controllerService.updateMaxUser(account, UUID.fromString(groupId), (long) userList.size() + group.getMembers().size());
                }
            } catch (UnrecognizedPropertyException | CharConversionException ex) {
                throw new WrongFileException(file.getOriginalFilename());
            }
        }

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
        model.addAttribute("account", account);
        UUID parentId = group.getParent();
        Group parent = new Group();
        if (!group.getMembers().contains(user)) {
            if (group.getVisibility() == Visibility.PRIVATE) {
                throw new NoAccessException("Die Gruppe ist privat");
            }
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
        controllerService.updateTitle(account, UUID.fromString(groupId), title);
        controllerService.updateDescription(account, UUID.fromString(groupId), description);

        return "redirect:/gruppen2/details/" + groupId;
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/findGroup")
    public String findGroup(KeycloakAuthenticationToken token,
                            Model model,
                            @RequestParam(value = "suchbegriff", required = false) String search) throws EventException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        List<Group> groupse = new ArrayList<>();
        if (search != null) {
            groupse = groupService.findGroupWith(search, account);
        }
        model.addAttribute("account", account);
        model.addAttribute("gruppen", groupse);
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
        Group parent = new Group();

        if (group.getTitle() == null) {
            throw new GroupNotFoundException("@details");
        }

        if (!group.getMembers().contains(user)) {
            if (group.getVisibility() == Visibility.PRIVATE) {
                throw new NoAccessException("Die Gruppe ist privat");
            }
            model.addAttribute("group", group);
            model.addAttribute("parentId", parentId);
            model.addAttribute("parent", parent);
            return "detailsNoMember";
        }

        if (!controllerService.idIsEmpty(parentId)) {
            parent = userService.getGroupById(parentId);
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
        if (group.getMembers().contains(user)) {
            throw new UserAlreadyExistsException("Du bist bereits in dieser Gruppe.");
        }
        controllerService.addUser(account, UUID.fromString(groupId));
        if (group.getUserMaximum() < group.getMembers().size()) {
            throw new GroupFullException("Du kannst der Gruppe daher leider nicht beitreten.");
        }
        //controllerService.addUser(account, groupId);
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
        Group parent = new Group();

        if (!controllerService.idIsEmpty(parentId)) {
            parent = userService.getGroupById(parentId);
        }

        if (group.getUserMaximum() > group.getMembers().size()) {
            model.addAttribute("group", group);
            model.addAttribute("parentId", parentId);
            model.addAttribute("parent", parent);

            return "detailsNoMember";
        }
        throw new GroupNotFoundException("@search");
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/acceptinvite/{groupId}")
    public String acceptInvite(KeycloakAuthenticationToken token,
                               Model model, @PathVariable String groupId) throws EventException {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        Group group = userService.getGroupById(UUID.fromString(groupId));
        if (group != null) {
            model.addAttribute("group", group);
            return "redirect:/gruppen2/detailsSearch?id=" + group.getId();
        }
        throw new GroupNotFoundException("@accept");
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/leaveGroup")
    public String pLeaveGroup(KeycloakAuthenticationToken token,
                              @RequestParam("group_id") String groupId) throws EventException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        controllerService.passIfLastAdmin(account, UUID.fromString(groupId));
        controllerService.deleteUser(user.getId(), UUID.fromString(groupId));

        if (userService.getGroupById(UUID.fromString(groupId)).getMembers().isEmpty()) {
            controllerService.deleteGroupEvent(user.getId(), UUID.fromString(groupId));
        }

        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/deleteGroup")
    public String pDeleteGroup(KeycloakAuthenticationToken token,
                               @RequestParam("group_id") String groupId) {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        Group group = userService.getGroupById(UUID.fromString(groupId));
        if (group.getRoles().get(user.getId()) != Role.ADMIN) {
            throw new NoAccessException("");
        }
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
        if (group.getMembers().contains(user)) {
            if (group.getRoles().get(account.getName()) == Role.ADMIN) {
                model.addAttribute("account", account);
                model.addAttribute("members", group.getMembers());
                model.addAttribute("group", group);
                model.addAttribute("admin", Role.ADMIN);
                return "editMembers";
            } else {
                return "redirect:/details/";
            }
        } else {
            throw new NoAccessException("Die Gruppe ist privat");
        }
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @PostMapping("/details/members/changeRole")
    public String changeRole(KeycloakAuthenticationToken token,
                             @RequestParam("group_id") String groupId,
                             @RequestParam("user_id") String userId) throws EventException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        if (userId.equals(account.getName())) {
            if (controllerService.passIfLastAdmin(account, UUID.fromString(groupId))) {
                throw new NoAdminAfterActionException("Du otto bist letzter Admin");
            }
            controllerService.updateRole(userId, UUID.fromString(groupId));
            return "redirect:/gruppen2/details/" + groupId;
        }
        controllerService.updateRole(userId, UUID.fromString(groupId));
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
        controllerService.deleteUser(userId, UUID.fromString(groupId));
        if (userService.getGroupById(UUID.fromString(groupId)).getMembers().isEmpty()) {
            controllerService.deleteGroupEvent(userId, UUID.fromString(groupId));
        }
        return "redirect:/gruppen2/details/members/" + groupId;
    }

    @GetMapping("*")
    public String defaultLink() throws EventException {
        throw new PageNotFoundException("\uD83D\uDE41");
    }
}
