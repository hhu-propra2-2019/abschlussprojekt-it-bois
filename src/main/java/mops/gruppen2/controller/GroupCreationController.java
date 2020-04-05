package mops.gruppen2.controller;

import mops.gruppen2.domain.Account;
import mops.gruppen2.service.ControllerService;
import mops.gruppen2.service.GroupService;
import mops.gruppen2.service.KeyCloakService;
import mops.gruppen2.service.ValidationService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.util.UUID;

@Controller
@SessionScope
@RequestMapping("/gruppen2")
public class GroupCreationController {

    private final GroupService groupService;
    private final ControllerService controllerService;
    private final ValidationService validationService;

    public GroupCreationController(GroupService groupService, ControllerService controllerService, ValidationService validationService) {
        this.groupService = groupService;
        this.controllerService = controllerService;
        this.validationService = validationService;
    }

    @RolesAllowed({"ROLE_orga", "ROLE_actuator"})
    @GetMapping("/createOrga")
    public String createGroupAsOrga(KeycloakAuthenticationToken token,
                                    Model model) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);

        model.addAttribute("account", account);
        model.addAttribute("lectures", groupService.getAllLecturesWithVisibilityPublic());

        return "createOrga";
    }

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

    @RolesAllowed("ROLE_studentin")
    @GetMapping("/createStudent")
    public String createGroupAsStudent(KeycloakAuthenticationToken token,
                                       Model model) {

        Account account = KeyCloakService.createAccountFromPrincipal(token);

        model.addAttribute("account", account);
        model.addAttribute("lectures", groupService.getAllLecturesWithVisibilityPublic());

        return "createStudent";
    }

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
}
