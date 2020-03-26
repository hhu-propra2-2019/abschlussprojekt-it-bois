package mops.gruppen2.service;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.exception.BadParameterException;
import mops.gruppen2.domain.exception.GroupFullException;
import mops.gruppen2.domain.exception.GroupNotFoundException;
import mops.gruppen2.domain.exception.NoAccessException;
import mops.gruppen2.domain.exception.NoAdminAfterActionException;
import mops.gruppen2.domain.exception.UserAlreadyExistsException;
import mops.gruppen2.security.Account;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ValidationService {

    private final ControllerService controllerService;
    private final UserService userService;
    private final GroupService groupService;

    public ValidationService(ControllerService controllerService, UserService userService, GroupService groupService) {
        this.controllerService = controllerService;
        this.userService = userService;
        this.groupService = groupService;
    }

    public List<Group> checkSearch(String search, List<Group> groups, Account account) {
        if (search != null) {
            groups = groupService.findGroupWith(search, account);
        }
        return groups;
    }

    public void throwIfGroupNotExisting(String title) {
        if (title == null) {
            throw new GroupNotFoundException("@details");
        }
    }

    public void throwIfNoAccessToPrivate(Group group, User user) {
        if (!checkIfUserInGroup(group, user) && group.getVisibility() == Visibility.PRIVATE) {
            throw new NoAccessException("");
        }
    }

    public boolean checkIfUserInGroup(Group group, User user) {
        return group.getMembers().contains(user);
    }

    public void throwIfUserAlreadyInGroup(Group group, User user) {
        if (checkIfUserInGroup(group, user)) {
            throw new UserAlreadyExistsException("@details");
        }
    }

    public void checkIfGroupFull(Group group) {
        if (group.getUserMaximum() < group.getMembers().size() + 1) {
            throw new GroupFullException("Du kannst der Gruppe daher leider nicht beitreten.");
        }
    }


    public void checkIfGroupEmpty(String groupId, User user) {
        if (userService.getGroupById(UUID.fromString(groupId)).getMembers().isEmpty()) {
            controllerService.deleteGroupEvent(user.getId(), UUID.fromString(groupId));
        }
    }

    public void throwIfNoAdmin(Group group, User user) {
        throwIfNoAccessToPrivate(group, user);
        if (group.getRoles().get(user.getId()) != Role.ADMIN) {
            throw new NoAccessException("");
        }
    }

    public boolean checkIfAdmin(Group group, User user) {
        if (checkIfUserInGroup(group, user)) {
            return group.getRoles().get(user.getId()) == Role.ADMIN;
        }
        return false;
    }

    public boolean checkIfDemotingSelf(String userId, String groupId, Account account) {
        if (userId.equals(account.getName())) {
            if (controllerService.passIfLastAdmin(account, UUID.fromString(groupId))) {
                throw new NoAdminAfterActionException("Du Otto bist letzter Admin");
            }
            controllerService.updateRole(userId, UUID.fromString(groupId));
            return true;
        }
        controllerService.updateRole(userId, UUID.fromString(groupId));
        return false;
    }

    /**
     * Überprüft ob alle Felder richtig gesetzt sind.
     *
     * @param description Die Beschreibung der Gruppe
     * @param title Der Titel der Gruppe
     * @param userMaximum Das user Limit der Gruppe
     */
    public void checkFields(String title, String description, Long userMaximum, Boolean maxInfiniteUsers) {
        if (description == null || description.trim().length() == 0) {
            throw new BadParameterException("Die Beschreibung wurde nicht korrekt angegeben");
        }

        if (title == null || title.trim().length() == 0) {
            throw new BadParameterException("Der Titel wurde nicht korrekt angegeben");
        }

        if (userMaximum == null && maxInfiniteUsers == null) {
            throw new BadParameterException("Teilnehmeranzahl wurde nicht korrekt angegeben");
        }

        if (userMaximum != null) {
            if (userMaximum < 1 || userMaximum > 10000L) {
                throw new BadParameterException("Teilnehmeranzahl wurde nicht korrekt angegeben");
            }
        }
    }

    public void checkFields(String title, String description) {
        if (description == null || description.trim().length() == 0) {
            throw new BadParameterException("Die Beschreibung wurde nicht korrekt angegeben");
        }

        if (title == null || title.trim().length() == 0) {
            throw new BadParameterException("Der Titel wurde nicht korrekt angegeben");
        }
    }

    public void checkIfNewMaximumIsValid(Long newUserMaximum, String groupId) {
        Group group = userService.getGroupById(UUID.fromString(groupId));
        if (newUserMaximum == null) {
            throw new BadParameterException("Es wurde keine neue maximale Teilnehmeranzahl angegeben!");
        }

        if (newUserMaximum < 1 || newUserMaximum > 10000L) {
            throw new BadParameterException("Die neue maximale Teilnehmeranzahl wurde nicht korrekt angegeben!");
        }

        if (group.getMembers().size() > newUserMaximum) {
            throw new BadParameterException("Die neue maximale Teilnehmeranzahl ist kleiner als die aktuelle Teilnehmeranzahl!");
        }
    }
}
