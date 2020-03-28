package mops.gruppen2.controller;

import mops.gruppen2.domain.Group;
import mops.gruppen2.security.Account;
import mops.gruppen2.service.InviteService;
import mops.gruppen2.service.KeyCloakService;
import mops.gruppen2.service.ValidationService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

@Controller
@SessionScope
@RequestMapping("/gruppen2")
public class SearchController {

    private final ValidationService validationService;
    private final InviteService inviteService;

    public SearchController(ValidationService validationService, InviteService inviteService) {
        this.validationService = validationService;
        this.inviteService = inviteService;
    }

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
}
