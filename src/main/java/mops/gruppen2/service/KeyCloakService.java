package mops.gruppen2.service;

import mops.gruppen2.domain.Account;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public final class KeyCloakService {

    private KeyCloakService() {}

    /**
     * Creates an Account.
     *
     * @param token Ein toller token
     *
     * @return Account with current userdata
     */
    public static Account createAccountFromPrincipal(KeycloakAuthenticationToken token) {
        KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
        return new Account(
                principal.getName(),
                principal.getKeycloakSecurityContext().getIdToken().getEmail(),
                null,
                principal.getKeycloakSecurityContext().getIdToken().getGivenName(),
                principal.getKeycloakSecurityContext().getIdToken().getFamilyName(),
                token.getAccount().getRoles());
    }
}
