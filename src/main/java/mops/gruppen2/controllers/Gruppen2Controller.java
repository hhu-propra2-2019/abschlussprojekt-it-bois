package mops.gruppen2.controllers;

import javax.annotation.security.RolesAllowed;
import mops.gruppen2.security.Account;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.annotation.SessionScope;

@SessionScope
@Controller
public class Gruppen2Controller {
    /**
     * Creates an Account.
     *
     * @param token
     * @return Account with current userdata
     */
    private Account createAccountFromPrincipal(KeycloakAuthenticationToken token) {
        KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        return new Account(
                principal.getName(),
                principal.getKeycloakSecurityContext().getIdToken().getEmail(),
                principal.getKeycloakSecurityContext().getIdToken().getGivenName(),
                principal.getKeycloakSecurityContext().getIdToken().getFamilyName(),
                token.getAccount().getRoles());
    }

    /**
     *
     * @param token
     * @param model
     * @return index.html
     */
    @GetMapping("/")
    @RolesAllowed({"ROLE_Orga", "ROLE_studentin", "ROLE_actuator)"})
    public String index(KeycloakAuthenticationToken token, Model model) {
        if (token != null) {
            model.addAttribute("account", createAccountFromPrincipal(token));
        }
        return "index";
    }
}
