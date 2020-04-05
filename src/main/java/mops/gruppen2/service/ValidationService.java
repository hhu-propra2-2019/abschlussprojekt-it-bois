package mops.gruppen2.service;

import mops.gruppen2.domain.Account;
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
import mops.gruppen2.domain.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static mops.gruppen2.domain.Role.ADMIN;

@Service
public class ValidationService {

    private final UserService userService;
    private final GroupService groupService;

    public ValidationService(UserService userService, GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }

    //TODO: make static or change return + assignment
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

    public void throwIfUserAlreadyInGroup(Group group, User user) {
        if (checkIfUserInGroup(group, user)) {
            throw new UserAlreadyExistsException("@details");
        }
    }

    void throwIfNotInGroup(Group group, User user) {
        if (!checkIfUserInGroup(group, user)) {
            throw new UserNotFoundException(getClass().toString());
        }
    }

    public boolean checkIfUserInGroup(Group group, User user) {
        return group.getMembers().contains(user);
    }

    public void throwIfGroupFull(Group group) {
        if (group.getUserMaximum() < group.getMembers().size() + 1) {
            throw new GroupFullException("Du kannst der Gruppe daher leider nicht beitreten.");
        }
    }

    boolean checkIfGroupEmpty(UUID groupId) {
        return userService.getGroupById(groupId).getMembers().isEmpty();
    }

    public void throwIfNoAdmin(Group group, User user) {
        throwIfNoAccessToPrivate(group, user);
        if (group.getRoles().get(user.getId()) != ADMIN) {
            throw new NoAccessException("");
        }
    }

    public void throwIfNoAccessToPrivate(Group group, User user) {
        if (!checkIfUserInGroup(group, user) && group.getVisibility() == Visibility.PRIVATE) {
            throw new NoAccessException("");
        }
    }

    public boolean checkIfAdmin(Group group, User user) {
        if (checkIfUserInGroup(group, user)) {
            return group.getRoles().get(user.getId()) == ADMIN;
        }
        return false;
    }

    void throwIfLastAdmin(Account account, Group group) {
        if (checkIfLastAdmin(account, group)) {
            throw new NoAdminAfterActionException("Du bist letzter Admin!");
        }
    }

    boolean checkIfLastAdmin(Account account, Group group) {
        for (Map.Entry<String, Role> entry : group.getRoles().entrySet()) {
            if (entry.getValue() == ADMIN && !(entry.getKey().equals(account.getName()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Überprüft ob alle Felder richtig gesetzt sind.
     *
     * @param description Die Beschreibung der Gruppe
     * @param title       Der Titel der Gruppe
     * @param userMaximum Das user Limit der Gruppe
     */
    public void checkFields(String title, String description, Long userMaximum, Boolean maxInfiniteUsers) {
        if (description == null || description.trim().isEmpty()) {
            throw new BadParameterException("Die Beschreibung wurde nicht korrekt angegeben");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new BadParameterException("Der Titel wurde nicht korrekt angegeben");
        }

        if (userMaximum == null && maxInfiniteUsers == null) {
            throw new BadParameterException("Teilnehmeranzahl wurde nicht korrekt angegeben");
        }

        if (userMaximum != null && (userMaximum < 1 || userMaximum > 10000L)) {
            throw new BadParameterException("Teilnehmeranzahl wurde nicht korrekt angegeben");
        }
    }

    public void checkFields(String title, String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new BadParameterException("Die Beschreibung wurde nicht korrekt angegeben");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new BadParameterException("Der Titel wurde nicht korrekt angegeben");
        }
    }

    public void throwIfNewMaximumIsValid(Long newUserMaximum, Group group) {
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
