package mops.gruppen2.service;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.exception.GroupNotFoundException;
import mops.gruppen2.domain.exception.NoValueException;
import mops.gruppen2.domain.exception.UserAlreadyExistsException;
import java.util.UUID;

public class ValidationService {

    private final ControllerService controllerService;
    private final UserService userService;

    public ValidationService(ControllerService controllerService, UserService userService) {
        this.controllerService = controllerService;
        this.userService = userService;
    }

    public boolean checkTitleAndDescription(String title, String description) {
        if(title != null && description != null) return true;
        throw new NoValueException("Titel und Beschreibung müssen ausgefüllt werden");
    }

    public boolean checkSearch(String search) {
        return search != null;
    }

    public void checkGroup(String title){
        if(title == null) throw new GroupNotFoundException("@details");
    }

    public boolean checkIfUserInGroup(Group group, User user) {
        if (group.getVisibility() == Visibility.PRIVATE) {
            throw new GroupNotFoundException("Du hast keine Zugriffsberechtigung");
        }
        return group.getMembers().contains(user);
    }

    public Group checkParent(UUID parentId) {
        Group parent = null;
        if (!controllerService.idIsEmpty(parentId)) {
            parent = userService.getGroupById(parentId);
        }
        return parent;
    }

    public void checkIfUserInGroupJoin(Group group, User user) {
        if(!group.getMembers().contains(user)){
            throw new UserAlreadyExistsException("@details");
        }
    }
}
