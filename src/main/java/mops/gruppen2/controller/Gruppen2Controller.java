package mops.gruppen2.controller;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import mops.gruppen2.config.Gruppen2Config;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.event.UpdateGroupTitleEvent;
import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.domain.exception.GroupNotFoundException;
import mops.gruppen2.domain.exception.WrongFileException;
import mops.gruppen2.domain.exception.NoAdminAfterActionException;
import mops.gruppen2.security.Account;
import mops.gruppen2.service.ControllerService;
import mops.gruppen2.service.CsvService;
import mops.gruppen2.service.GroupService;
import mops.gruppen2.service.InviteLinkRepositoryService;
import mops.gruppen2.service.KeyCloakService;
import mops.gruppen2.service.UserService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.CharConversionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Controller
@SessionScope
@RequestMapping("/gruppen2")
public class Gruppen2Controller {

    private final KeyCloakService keyCloakService;
    private final GroupService groupService;
    private final UserService userService;
    private final ControllerService controllerService;
    private final InviteLinkRepositoryService inviteLinkRepositoryService;
    private final Gruppen2Config gruppen2Config;
    private final Logger logger;

    public Gruppen2Controller(KeyCloakService keyCloakService, GroupService groupService, UserService userService, ControllerService controllerService, InviteLinkRepositoryService inviteLinkRepositoryService, Gruppen2Config gruppen2Config) {
        this.keyCloakService = keyCloakService;
        this.groupService = groupService;
        this.userService = userService;
        this.controllerService = controllerService;
        this.inviteLinkRepositoryService = inviteLinkRepositoryService;
        logger = Logger.getLogger("Gruppen2ControllerLogger");
        this.gruppen2Config = gruppen2Config;
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
                              @RequestParam(value = "userMaximum", required = false) Long userMaximum,
                              @RequestParam(value = "maxInfiniteUsers", required = false) Boolean maxInfiniteUsers,
                              @RequestParam(value = "parent", required = false) Long parent,
                              @RequestParam(value = "file", required = false) MultipartFile file) throws IOException, EventException {

        Account account = keyCloakService.createAccountFromPrincipal(token);
        List<User> userList = new ArrayList<>();
        if(userMaximum == null){
            userMaximum = 100000L;
        }
        if (!file.isEmpty()) {
            try {
                userList = CsvService.read(file.getInputStream());
                if(userList.size() > userMaximum){
                    userMaximum =  Long.valueOf(userList.size()) + userMaximum;
                }
            } catch (UnrecognizedPropertyException | CharConversionException ex) {
                logger.warning("File konnte nicht gelesen werden");
                throw new WrongFileException(file.getOriginalFilename());
            }
        }
        visibility = visibility == null;
        lecture = lecture != null;
        maxInfiniteUsers = maxInfiniteUsers != null;

        if (lecture) parent = null;

        controllerService.createOrga(account, title, description, visibility, lecture, maxInfiniteUsers, userMaximum, parent, userList);

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
                                 @RequestParam(value = "userMaximum", required = false) Long userMaximum,
                                 @RequestParam(value = "maxInfiniteUsers", required = false) Boolean maxInfiniteUsers,
                                 @RequestParam(value = "parent", required = false) Long parent) throws EventException {

        Account account = keyCloakService.createAccountFromPrincipal(token);
        visibility = visibility == null;
        maxInfiniteUsers = maxInfiniteUsers != null;
        controllerService.createGroup(account, title, description, visibility, maxInfiniteUsers, userMaximum, parent);

        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_actuator)"})
    @PostMapping("/details/members/addUsersFromCsv")
    public String addUsersFromCsv(KeycloakAuthenticationToken token,
                                  @RequestParam("group_id") Long groupId,
                                  @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        List<User> userList = new ArrayList<>();
        Group group = userService.getGroupById(groupId);
        if (!file.isEmpty()) {
            try {
                userList = CsvService.read(file.getInputStream());
                if(userList.size()+group.getMembers().size()>group.getUserMaximum()){
                    controllerService.updateMaxUser(account, groupId, Long.valueOf(userList.size()) + group.getMembers().size());
                }
            } catch (UnrecognizedPropertyException | CharConversionException ex) {
                throw new WrongFileException(file.getOriginalFilename());
            }
        }
        controllerService.addUserList(userList, groupId);
        return "redirect:/gruppen2/details/members/" + groupId;
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/details/changeMetadata/{id}")
    public String changeMetadata(KeycloakAuthenticationToken token, Model model, @PathVariable("id") Long groupId) {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        Group group = userService.getGroupById(groupId);
        model.addAttribute("account", account);
        Long parentId = group.getParent();
        Group parent = new Group();
        if (!group.getMembers().contains(user)) {
            if (group.getVisibility() == Visibility.PRIVATE) {
                return "privateGroupNoMember";
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
                                  @RequestParam("groupId") Long groupId) throws EventException {

        Account account = keyCloakService.createAccountFromPrincipal(token);
        controllerService.updateTitle(account, groupId, title);
        controllerService.updateDescription(account, groupId, description);

        return "redirect:/gruppen2/details/" + groupId;
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

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @GetMapping("/details/{id}")
    public String showGroupDetails(KeycloakAuthenticationToken token, Model model, @PathVariable("id") Long groupId) throws EventException {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        Group group = userService.getGroupById(groupId);
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        Long parentId = group.getParent();
        Group parent = new Group();
        if(group.getTitle() == null){
            throw new GroupNotFoundException(this.getClass().toString());
        }
        if (!group.getMembers().contains(user)){
            if (group.getVisibility() == Visibility.PRIVATE){
                return "privateGroupNoMember";
            }
            if (group != null) {
                model.addAttribute("group", group);
                model.addAttribute("parentId", parentId);
                model.addAttribute("parent", parent);
                return "detailsNoMember";
            }
            return "detailsNoMember";
        }
        if (parentId != null) {
            parent = userService.getGroupById(parentId);
        }
        if (group != null) {
            model.addAttribute("parentId", parentId);
            model.addAttribute("parent", parent);
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
    public String joinGroup(KeycloakAuthenticationToken token, Model model, @RequestParam("id") Long groupId) throws EventException {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));

        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        Group group = userService.getGroupById(groupId);
        if (group.getMembers().contains(user)) {
            return "error"; //hier soll eigentlich auf die bereits beigetretene Gruppe weitergeleitet werden
        }
        if (group.getUserMaximum() < group.getMembers().size()) return "error";
        controllerService.addUser(account, groupId);
        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @GetMapping("/detailsSearch")
    public String showGroupDetailsNoMember(KeycloakAuthenticationToken token, Model model, @RequestParam("id") Long groupId) throws EventException {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        Group group = userService.getGroupById(groupId);
        Long parentId = group.getParent();
        Group parent = new Group();
        if (parentId != null) {
            parent = userService.getGroupById(parentId);
        }
        if (group != null && group.getUserMaximum() > group.getMembers().size()) {
            model.addAttribute("group", group);
            model.addAttribute("parentId", parentId);
            model.addAttribute("parent", parent);
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
    public String pLeaveGroup(KeycloakAuthenticationToken token, @RequestParam("group_id") Long groupId) throws EventException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        controllerService.passIfLastAdmin(account, groupId);
        controllerService.deleteUser(user.getId(), groupId);
        if(userService.getGroupById(groupId).getMembers().size() == 0){
            controllerService.deleteGroupEvent(user.getId(), groupId);
        }
        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
    @PostMapping("/deleteGroup")
    public String pDeleteGroup(KeycloakAuthenticationToken token, @RequestParam("group_id") Long groupId){
        Account account = keyCloakService.createAccountFromPrincipal(token);
        User user = new User(account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        Group group = userService.getGroupById(groupId);
        if(group.getRoles().get(user.getId()) != Role.ADMIN ){
            return "error";
        }
        controllerService.deleteGroupEvent(user.getId(), groupId);
        return "redirect:/gruppen2/";
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @GetMapping("/details/members/{id}")
    public String editMembers(Model model, KeycloakAuthenticationToken token, @PathVariable("id") Long groupId) throws EventException {
        Account account = keyCloakService.createAccountFromPrincipal(token);
        Group group = userService.getGroupById(groupId);
        User user = new User(account.getName(),"", "", "");
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
        }else {
            return "privateGroupNoMember";
        }
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @PostMapping("/details/members/changeRole")
    public String changeRole(KeycloakAuthenticationToken token, @RequestParam("group_id") Long groupId,
                             @RequestParam("user_id") String userId) throws EventException {


        Account account = keyCloakService.createAccountFromPrincipal(token);
        if (userId.equals(account.getName())) {
            if (controllerService.passIfLastAdmin(account, groupId)){
                throw new NoAdminAfterActionException("Du otto bist letzter Admin");
            }
            controllerService.updateRole(userId, groupId);
            return "redirect:/gruppen2/details/" + groupId;
        }
        controllerService.updateRole(userId, groupId);
        return "redirect:/gruppen2/details/members/" + groupId;
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @PostMapping("/details/members/changeMaximum")
    public String changeMaxSize(@RequestParam("maximum") Long maximum,
                                @RequestParam("group_id") Long groupId,
                                KeycloakAuthenticationToken token){
        Account account = keyCloakService.createAccountFromPrincipal(token);
        controllerService.updateMaxUser(account, groupId, maximum);
        return "redirect:/gruppen2/details/members/" + groupId;
    }

    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    @PostMapping("/details/members/deleteUser")
    public String deleteUser(@RequestParam("group_id") Long groupId,
                             @RequestParam("user_id") String userId) throws EventException {
        controllerService.deleteUser(userId, groupId);
        if(userService.getGroupById(groupId).getMembers().size() == 0){
            controllerService.deleteGroupEvent(userId ,groupId);
        }
        return "redirect:/gruppen2/details/members/" + groupId;
    }
}
