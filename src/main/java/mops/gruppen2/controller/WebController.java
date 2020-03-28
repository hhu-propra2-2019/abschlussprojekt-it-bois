package mops.gruppen2.controller;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.exception.PageNotFoundException;
import mops.gruppen2.security.Account;
import mops.gruppen2.service.ControllerService;
import mops.gruppen2.service.GroupService;
import mops.gruppen2.service.InviteService;
import mops.gruppen2.service.KeyCloakService;
import mops.gruppen2.service.UserService;
import mops.gruppen2.service.ValidationService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.cache.annotation.CacheEvict;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//TODO: Remove duplicate Injections
@Controller
@SessionScope
@RequestMapping("/gruppen2")
public class WebController {

    private final GroupService groupService;
    private final UserService userService;
    private final ControllerService controllerService;
    private final ValidationService validationService;
    private final InviteService inviteService;

    public WebController(GroupService groupService,
                         UserService userService,
                         ControllerService controllerService,
                         ValidationService validationService,
                         InviteService inviteService) {
        this.groupService = groupService;
        this.userService = userService;
        this.controllerService = controllerService;
        this.validationService = validationService;
        this.inviteService = inviteService;
    }

    /**
     * Zeigt die index.html an.
     *
     * @param token toller token
     * @param model tolles model
     *
     * @return index.html
     */
    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("")
    public String index(KeycloakAuthenticationToken token,
                        Model model) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        //TODO: new Contructor/method
        User user = new User(account.getName(),
                             account.getGivenname(),
                             account.getFamilyname(),
                             account.getEmail());

        model.addAttribute("account", account);
        model.addAttribute("gruppen", userService.getUserGroups(user));
        model.addAttribute("user", user);

        return "index";
    }

    //TODO: CreateController
    @RolesAllowed({"ROLE_orga", "ROLE_actuator"})
    @GetMapping("/createOrga")
    public String createGroupAsOrga(KeycloakAuthenticationToken token,
                                    Model model) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);

        model.addAttribute("account", account);
        model.addAttribute("lectures", groupService.getAllLecturesWithVisibilityPublic());

        return "createOrga";
    }

    //TODO: CreateController
    @RolesAllowed({"ROLE_orga", "ROLE_actuator"})
    @PostMapping("/createOrga")
    @CacheEvict(value = "groups", allEntries = true)
    public String postCrateGroupAsOrga(KeycloakAuthenticationToken token,
                                       @RequestParam("title") String title,
                                       @RequestParam("description") String description,
                                       @RequestParam(value = "visibility", required = false) Boolean visibility,
                                       @RequestParam(value = "lecture", required = false) Boolean lecture,
                                       @RequestParam("userMaximum") Long userMaximum,
                                       @RequestParam(value = "maxInfiniteUsers", required = false) Boolean maxInfiniteUsers,
                                       @RequestParam(value = "parent", required = false) String parent,
                                       @RequestParam(value = "file", required = false) MultipartFile file) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        UUID parentUUID = controllerService.getUUID(parent);

        validationService.checkFields(description, title, userMaximum, maxInfiniteUsers);

        controllerService.createGroupAsOrga(account,
                                            title,
                                            description,
                                            visibility,
                                            lecture,
                                            maxInfiniteUsers,
                                            userMaximum,
                                            parentUUID,
                                            file);
        return "redirect:/gruppen2";
    }

    //TODO: CreateController
    @RolesAllowed("ROLE_studentin")
    @GetMapping("/createStudent")
    public String createGroupAsStudent(KeycloakAuthenticationToken token,
                                       Model model) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);

        model.addAttribute("account", account);
        model.addAttribute("lectures", groupService.getAllLecturesWithVisibilityPublic());

        return "createStudent";
    }

    //TODO: CreateController
    @RolesAllowed("ROLE_studentin")
    @PostMapping("/createStudent")
    @CacheEvict(value = "groups", allEntries = true)
    public String postCreateGroupAsStudent(KeycloakAuthenticationToken token,
                                           @RequestParam("title") String title,
                                           @RequestParam("description") String description,
                                           @RequestParam("userMaximum") Long userMaximum,
                                           @RequestParam(value = "visibility", required = false) Boolean visibility,
                                           @RequestParam(value = "maxInfiniteUsers", required = false) Boolean maxInfiniteUsers,
                                           @RequestParam(value = "parent", required = false) String parent) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        UUID parentUUID = controllerService.getUUID(parent);

        validationService.checkFields(description, title, userMaximum, maxInfiniteUsers);

        controllerService.createGroup(account,
                                      title,
                                      description,
                                      visibility,
                                      null,
                                      maxInfiniteUsers,
                                      userMaximum,
                                      parentUUID);

        return "redirect:/gruppen2";
    }

    //TODO: CreateController
    @RolesAllowed({"ROLE_orga", "ROLE_actuator"})
    @PostMapping("/details/members/addUsersFromCsv")
    @CacheEvict(value = "groups", allEntries = true)
    public String addUsersFromCsv(KeycloakAuthenticationToken token,
                                  @RequestParam("group_id") String groupId,
                                  @RequestParam(value = "file", required = false) MultipartFile file) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        controllerService.addUsersFromCsv(account, file, groupId);

        return "redirect:/gruppen2/details/members/" + groupId;
    }

    //TODO: DetailsController
    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/details/changeMetadata/{id}")
    public String changeMetadata(KeycloakAuthenticationToken token,
                                 Model model,
                                 @PathVariable("id") String groupId) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(),
                             account.getGivenname(),
                             account.getFamilyname(),
                             account.getEmail());
        Group group = userService.getGroupById(UUID.fromString(groupId));

        validationService.throwIfNoAdmin(group, user);

        model.addAttribute("account", account);
        model.addAttribute("title", group.getTitle());
        model.addAttribute("description", group.getDescription());
        model.addAttribute("admin", Role.ADMIN);
        model.addAttribute("roles", group.getRoles());
        model.addAttribute("groupId", group.getId());
        model.addAttribute("user", user);

        return "changeMetadata";
    }

    //TODO: DetailsController
    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/details/changeMetadata")
    @CacheEvict(value = "groups", allEntries = true)
    public String postChangeMetadata(KeycloakAuthenticationToken token,
                                     @RequestParam("title") String title,
                                     @RequestParam("description") String description,
                                     @RequestParam("groupId") String groupId) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        //TODO: new Constructor/Method
        User user = new User(account.getName(), "", "", "");
        Group group = userService.getGroupById(UUID.fromString(groupId));

        validationService.throwIfNoAdmin(group, user);
        validationService.checkFields(title, description);

        controllerService.changeMetaData(account, group, title, description);

        return "redirect:/gruppen2/details/" + groupId;
    }

    //TODO: SearchController
    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/findGroup")
    public String findGroup(KeycloakAuthenticationToken token,
                            Model model,
                            @RequestParam(value = "suchbegriff", required = false) String search) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        List<Group> groups = new ArrayList<>();
        groups = validationService.checkSearch(search, groups, account);

        model.addAttribute("account", account);
        model.addAttribute("gruppen", groups);
        model.addAttribute("inviteService", inviteService);

        return "search";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/details/{id}")
    public String showGroupDetails(KeycloakAuthenticationToken token,
                                   Model model,
                                   HttpServletRequest request,
                                   @PathVariable("id") String groupId) {

        Group group = userService.getGroupById(UUID.fromString(groupId));
        Account account = KeyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(),
                             account.getGivenname(),
                             account.getFamilyname(),
                             account.getEmail());
        UUID parentId = group.getParent();
        String actualURL = request.getRequestURL().toString();
        String serverURL = actualURL.substring(0, actualURL.indexOf("gruppen2/"));
        Group parent = controllerService.getParent(parentId);

        validationService.throwIfGroupNotExisting(group.getTitle());

        model.addAttribute("account", account);
        if (!validationService.checkIfUserInGroup(group, user)) {
            validationService.throwIfNoAccessToPrivate(group, user);
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

        if (validationService.checkIfAdmin(group, user)) {
            model.addAttribute("link", serverURL + "gruppen2/acceptinvite/" + inviteService.getLinkByGroupId(group.getId()));
        }

        return "detailsMember";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/detailsBeitreten")
    @CacheEvict(value = "groups", allEntries = true)
    public String joinGroup(KeycloakAuthenticationToken token,
                            Model model,
                            @RequestParam("id") String groupId) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(),
                             account.getGivenname(),
                             account.getFamilyname(),
                             account.getEmail());
        Group group = userService.getGroupById(UUID.fromString(groupId));

        validationService.throwIfUserAlreadyInGroup(group, user);
        validationService.throwIfGroupFull(group);

        controllerService.addUser(account, UUID.fromString(groupId));

        model.addAttribute("account", account);

        return "redirect:/gruppen2";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/detailsSearch")
    public String showGroupDetailsNoMember(KeycloakAuthenticationToken token,
                                           Model model,
                                           @RequestParam("id") String groupId) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        Group group = userService.getGroupById(UUID.fromString(groupId));
        UUID parentId = group.getParent();
        Group parent = controllerService.getParent(parentId);
        //TODO: Replace
        User user = new User(account.getName(), "", "", "");

        model.addAttribute("account", account);
        if (validationService.checkIfUserInGroup(group, user)) {
            return "redirect:/gruppen2/details/" + groupId;
        }

        model.addAttribute("group", group);
        model.addAttribute("parentId", parentId);
        model.addAttribute("parent", parent);

        return "detailsNoMember";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/acceptinvite/{link}")
    public String acceptInvite(KeycloakAuthenticationToken token,
                               Model model,
                               @PathVariable("link") String link) {

        Group group = userService.getGroupById(inviteService.getGroupIdFromLink(link));

        validationService.throwIfGroupNotExisting(group.getTitle());

        model.addAttribute("account", KeyCloakService.createAccountFromPrincipal(token));
        model.addAttribute("group", group);

        if (group.getVisibility() == Visibility.PUBLIC) {
            return "redirect:/gruppen2/details/" + group.getId();
        }

        return "joinprivate";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/acceptinvite")
    @CacheEvict(value = "groups", allEntries = true)
    public String postAcceptInvite(KeycloakAuthenticationToken token,
                                   @RequestParam("id") String groupId) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);

        User user = new User(account.getName(),
                             account.getGivenname(),
                             account.getFamilyname(),
                             account.getEmail());

        if (!validationService.checkIfUserInGroup(userService.getGroupById(UUID.fromString(groupId)), user)) {
            controllerService.addUser(KeyCloakService.createAccountFromPrincipal(token), UUID.fromString(groupId));
        }

        return "redirect:/gruppen2";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/leaveGroup")
    @CacheEvict(value = "groups", allEntries = true)
    public String pLeaveGroup(KeycloakAuthenticationToken token,
                              @RequestParam("group_id") String groupId) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), "", "", "");
        Group group = userService.getGroupById(UUID.fromString(groupId));

        controllerService.deleteUser(account, user, group);

        return "redirect:/gruppen2";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/deleteGroup")
    @CacheEvict(value = "groups", allEntries = true)
    public String pDeleteGroup(KeycloakAuthenticationToken token,
                               @RequestParam("group_id") String groupId) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(),
                             account.getGivenname(),
                             account.getFamilyname(),
                             account.getEmail());
        Group group = userService.getGroupById(UUID.fromString(groupId));

        validationService.throwIfNoAdmin(group, user);

        controllerService.deleteGroupEvent(user.getId(), UUID.fromString(groupId));

        return "redirect:/gruppen2";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/details/members/{id}")
    public String editMembers(KeycloakAuthenticationToken token,
                              Model model,
                              @PathVariable("id") String groupId) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        Group group = userService.getGroupById(UUID.fromString(groupId));
        User user = new User(account.getName(), "", "", "");

        validationService.throwIfNoAdmin(group, user);

        model.addAttribute("account", account);
        model.addAttribute("members", group.getMembers());
        model.addAttribute("group", group);
        model.addAttribute("admin", Role.ADMIN);

        return "editMembers";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/details/members/changeRole")
    @CacheEvict(value = "groups", allEntries = true)
    public String changeRole(KeycloakAuthenticationToken token,
                             @RequestParam("group_id") String groupId,
                             @RequestParam("user_id") String userId) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        Group group = userService.getGroupById(UUID.fromString(groupId));
        User principle = new User(account.getName(), "", "", "");
        User user = new User(userId, "", "", "");

        validationService.throwIfNoAdmin(group, principle);

        controllerService.changeRole(account, user, group);

        if (!validationService.checkIfAdmin(group, principle)) {
            return "redirect:/gruppen2/details/" + groupId;
        }

        return "redirect:/gruppen2/details/members/" + groupId;
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/details/members/changeMaximum")
    @CacheEvict(value = "groups", allEntries = true)
    public String changeMaxSize(KeycloakAuthenticationToken token,
                                @RequestParam("maximum") Long maximum,
                                @RequestParam("group_id") String groupId) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        Group group = userService.getGroupById(UUID.fromString(groupId));

        validationService.throwIfNewMaximumIsValid(maximum, group);

        controllerService.updateMaxUser(account, UUID.fromString(groupId), maximum);

        return "redirect:/gruppen2/details/members/" + groupId;
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/details/members/deleteUser")
    @CacheEvict(value = "groups", allEntries = true)
    public String deleteUser(KeycloakAuthenticationToken token,
                             @RequestParam("group_id") String groupId,
                             @RequestParam("user_id") String userId) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        User principle = new User(account.getName(), "", "", "");
        User user = new User(userId, "", "", "");
        Group group = userService.getGroupById(UUID.fromString(groupId));

        validationService.throwIfNoAdmin(group, principle);

        controllerService.deleteUser(account, user, group);

        if (!validationService.checkIfUserInGroup(group, principle)) {
            return "redirect:/gruppen2";
        }

        return "redirect:/gruppen2/details/members/" + groupId;
    }

    @GetMapping("*")
    public String defaultLink() throws PageNotFoundException {
        throw new PageNotFoundException("\uD83D\uDE41");
    }
}
