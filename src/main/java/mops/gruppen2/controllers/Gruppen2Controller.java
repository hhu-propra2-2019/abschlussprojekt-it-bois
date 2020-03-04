package mops.gruppen2.controllers;

import mops.gruppen2.Security.Account;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Gruppen2Controller {
    private Account createAccountFromPrincipal(KeycloakAuthenticationToken token) {
        KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        return new Account(
                principal.getName(),
                principal.getKeycloakSecurityContext().getIdToken().getEmail(),
                principal.getKeycloakSecurityContext().getIdToken().getGivenName(),
                principal.getKeycloakSecurityContext().getIdToken().getFamilyName(),
                token.getAccount().getRoles());
    }

    @GetMapping("/")
    @Secured("ROLE_Orga")
    public String index(KeycloakAuthenticationToken token, Model model) {
        if (token != null) {

            model.addAttribute("account", createAccountFromPrincipal(token));

        }
        return "index";
    }
}
