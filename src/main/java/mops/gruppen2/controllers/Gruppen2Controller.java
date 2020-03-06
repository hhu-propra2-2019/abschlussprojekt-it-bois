package mops.gruppen2.controllers;

import javax.annotation.security.RolesAllowed;
import mops.gruppen2.security.Account;
import mops.gruppen2.services.GruppenService;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.annotation.SessionScope;

@SessionScope
@Controller
public class Gruppen2Controller {
    @Autowired
    GruppenService gruppenService;
    /**
     * Creates an Account.
     *
     * @param token Ein toller token
     * @return Account with current userdata
     */
    private Account createAccountFromPrincipal(KeycloakAuthenticationToken token) {
        KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        return new Account(
                principal.getName(),
                principal.getKeycloakSecurityContext().getIdToken().getEmail(),
                null,
                principal.getKeycloakSecurityContext().getIdToken().getGivenName(),
                principal.getKeycloakSecurityContext().getIdToken().getFamilyName(),
                token.getAccount().getRoles());
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
        model.addAttribute("account", createAccountFromPrincipal(token));
        return "index";
    }
}
