package mops.gruppen2.controllers;

import javax.annotation.security.RolesAllowed;

import mops.gruppen2.entities.Teilnehmer;
import mops.gruppen2.security.Account;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.annotation.SessionScope;

@SessionScope
@Controller
public class Gruppen2Controller {
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

    @PostMapping("/")
    public String addTeilnehmer(@ModelAttribute Teilnehmer teilnehmer) {
        System.out.println(teilnehmer);
        return "redirect:/";
    }
}
