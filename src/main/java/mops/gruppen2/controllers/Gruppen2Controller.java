package mops.gruppen2.controllers;

import mops.gruppen2.services.KeyCloakService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.security.RolesAllowed;

@SessionScope
@Controller
public class Gruppen2Controller {

    private final KeyCloakService keyCloakService;

    public Gruppen2Controller(KeyCloakService keyCloakService) {
        this.keyCloakService = keyCloakService;
    }

    /**Zeigt die index.html an.
     *
     * @param token toller token
     * @param model tolles model
     * @return index.html
     */
    @GetMapping("/")
    @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator)"})
    public String index(KeycloakAuthenticationToken token, Model model) {
        model.addAttribute("account", keyCloakService.createAccountFromPrincipal(token));
        return "index";
    }
}
