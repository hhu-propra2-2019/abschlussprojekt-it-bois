package mops.gruppen2.service;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.exception.*;
import mops.gruppen2.security.Account;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.StyledEditorKit;
import javax.validation.ValidationException;
import java.io.CharConversionException;
import java.io.File;
import java.io.IOException;
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

    public void checkTitleAndDescription(String title, String description, Account account, String groupId) {
        if (title == null || description == null) {
            throw new NoValueException("Titel und Beschreibung müssen ausgefüllt werden");
        }
        controllerService.updateTitle(account, UUID.fromString(groupId), title);
        controllerService.updateDescription(account, UUID.fromString(groupId), description);
    }

    public List<Group> checkSearch(String search, List<Group> groups, Account account) {
        if (search != null) {
            groups = groupService.findGroupWith(search, account);
        }
        return groups;
    }

    public void checkGroup(String title) {
        if (title == null) throw new GroupNotFoundException("@details");
    }

    public boolean checkIfUserInGroup(Group group, User user) {
        if (!group.getMembers().contains(user) && group.getVisibility() == Visibility.PRIVATE) {
            throw new NoAccessException("");
        } else return group.getMembers().contains(user);
    }

    public Group checkParent(UUID parentId) {
        Group parent = new Group();
        if (!controllerService.idIsEmpty(parentId)) {
            parent = userService.getGroupById(parentId);
        }
        return parent;
    }

    public void checkIfUserInGroupJoin(Group group, User user) {
        if (group.getMembers().contains(user)) {
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

    public void checkIfAdmin(Group group, User user) {
        checkIfUserInGroup(group, user);
        if (group.getRoles().get(user.getId()) != Role.ADMIN) {
            throw new NoAccessException("");
        }
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

    public List<User> checkFile(MultipartFile file, List<User> userList, String groupId, Group group, Account account) {
        if (!file.isEmpty()) {
            try {
                userList = CsvService.read(file.getInputStream());
                if (userList.size() + group.getMembers().size() > group.getUserMaximum()) {
                    controllerService.updateMaxUser(account, UUID.fromString(groupId), (long) userList.size() + group.getMembers().size());
                }
            } catch (IOException ex) {
                throw new WrongFileException(file.getOriginalFilename());
            }
        }
        return userList;
    }

    /**
     * Überprüft ob alle Felder richtig gesetzt sind.
     * @param description
     * @param title
     * @param userMaximum
     */
    public void checkFields(String description, String title, Long userMaximum, Boolean maxInfiniteUsers) {
        if (description == null) {
            throw new BadParameterException("Die Beschreibung wurde nicht korrekt angegeben");
        }

        if (title == null) {
            throw new BadParameterException("Der Titel wurde nicht korrekt angegeben");
        }

        if (userMaximum == null && maxInfiniteUsers == null) {
            throw new BadParameterException("Teilnehmeranzahl wurde nicht korrekt angegeben");
        }
    }
}
