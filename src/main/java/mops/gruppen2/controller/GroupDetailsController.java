package mops.gruppen2.controller;

import mops.gruppen2.domain.Account;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.service.ControllerService;
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
import java.util.UUID;

@Controller
@SessionScope
@RequestMapping("/gruppen2")
public class GroupDetailsController {

    private final ControllerService controllerService;
    private final UserService userService;
    private final ValidationService validationService;
    private final InviteService inviteService;

    public GroupDetailsController(ControllerService controllerService, UserService userService, ValidationService validationService, InviteService inviteService) {
        this.controllerService = controllerService;
        this.userService = userService;
        this.validationService = validationService;
        this.inviteService = inviteService;
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/details/{id}")
    public String showGroupDetails(KeycloakAuthenticationToken token,
                                   Model model,
                                   HttpServletRequest request,
                                   @PathVariable("id") String groupId) {

        Group group = userService.getGroupById(UUID.fromString(groupId));
        Account account = KeyCloakService.createAccountFromPrincipal(token);
        User user = new User(account);
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
        model.addAttribute("public", Visibility.PUBLIC);
        model.addAttribute("private", Visibility.PRIVATE);

        if (validationService.checkIfAdmin(group, user)) {
            model.addAttribute("link", serverURL + "gruppen2/acceptinvite/" + inviteService.getLinkByGroupId(group.getId()));
        }

        return "detailsMember";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/details/changeMetadata/{id}")
    public String changeMetadata(KeycloakAuthenticationToken token,
                                 Model model,
                                 @PathVariable("id") String groupId) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        User user = new User(account);
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

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/details/changeMetadata")
    @CacheEvict(value = "groups", allEntries = true)
    public String postChangeMetadata(KeycloakAuthenticationToken token,
                                     @RequestParam("title") String title,
                                     @RequestParam("description") String description,
                                     @RequestParam("groupId") String groupId) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        User user = new User(account);
        Group group = userService.getGroupById(UUID.fromString(groupId));

        validationService.throwIfNoAdmin(group, user);
        validationService.checkFields(title, description);

        controllerService.changeMetaData(account, group, title, description);

        return "redirect:/gruppen2/details/" + groupId;
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/details/members/{id}")
    public String editMembers(KeycloakAuthenticationToken token,
                              Model model,
                              @PathVariable("id") String groupId) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        Group group = userService.getGroupById(UUID.fromString(groupId));
        User user = new User(account);

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
        User principle = new User(account);
        User user = new User(userId, "", "", "");

        validationService.throwIfNoAdmin(group, principle);

        //TODO: checkIfAdmin checkt nicht, dass die rolle geändert wurde. oder die rolle wird nicht geändert

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
        User principle = new User(account);
        User user = new User(userId, "", "", "");
        Group group = userService.getGroupById(UUID.fromString(groupId));

        validationService.throwIfNoAdmin(group, principle);

        controllerService.deleteUser(account, user, group);

        if (!validationService.checkIfUserInGroup(group, principle)) {
            return "redirect:/gruppen2";
        }

        return "redirect:/gruppen2/details/members/" + groupId;
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/detailsBeitreten")
    @CacheEvict(value = "groups", allEntries = true)
    public String joinGroup(KeycloakAuthenticationToken token,
                            Model model,
                            @RequestParam("id") String groupId) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        User user = new User(account);
        Group group = userService.getGroupById(UUID.fromString(groupId));

        validationService.throwIfUserAlreadyInGroup(group, user);
        validationService.throwIfGroupFull(group);

        controllerService.addUser(account, UUID.fromString(groupId));

        model.addAttribute("account", account);

        return "redirect:/gruppen2";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/leaveGroup")
    @CacheEvict(value = "groups", allEntries = true)
    public String leaveGroup(KeycloakAuthenticationToken token,
                             @RequestParam("group_id") String groupId) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        User user = new User(account);
        Group group = userService.getGroupById(UUID.fromString(groupId));

        controllerService.deleteUser(account, user, group);

        return "redirect:/gruppen2";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/deleteGroup")
    @CacheEvict(value = "groups", allEntries = true)
    public String deleteGroup(KeycloakAuthenticationToken token,
                              @RequestParam("group_id") String groupId) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);
        User user = new User(account);
        Group group = userService.getGroupById(UUID.fromString(groupId));

        validationService.throwIfNoAdmin(group, user);

        controllerService.deleteGroupEvent(user.getId(), UUID.fromString(groupId));

        return "redirect:/gruppen2";
    }

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
}
