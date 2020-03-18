package mops.gruppen2.service;

import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.DeleteGroupEvent;
import mops.gruppen2.domain.event.DeleteUserEvent;
import mops.gruppen2.domain.event.UpdateGroupDescriptionEvent;
import mops.gruppen2.domain.event.UpdateGroupTitleEvent;
import mops.gruppen2.domain.event.UpdateRoleEvent;
import mops.gruppen2.domain.exception.EventException;
import mops.gruppen2.security.Account;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class ControllerService {

    private final EventService eventService;
    private final UserService userService;
    private final InviteLinkRepositoryService inviteLinkRepositoryService;

    public ControllerService(EventService eventService, UserService userService, InviteLinkRepositoryService inviteLinkRepositoryService) {
        this.eventService = eventService;
        this.userService = userService;
        this.inviteLinkRepositoryService = inviteLinkRepositoryService;
    }

    public void createGroup(Account account, String title, String description, Boolean visibility) throws EventException {
        Visibility visibility1;
        Long groupId = eventService.checkGroup();

        if (visibility) {
            visibility1 = Visibility.PUBLIC;
        } else {
            visibility1 = Visibility.PRIVATE;
            createInviteLink(groupId);
        }

        CreateGroupEvent createGroupEvent = new CreateGroupEvent(groupId, account.getName(), null, GroupType.SIMPLE, visibility1);
        eventService.saveEvent(createGroupEvent);

        addUser(account, groupId);
        updateTitle(account, groupId, title);
        updateDescription(account, groupId, description);
        updateRole(account.getName(), groupId);
    }

    private void createInviteLink(Long groupId) {
        inviteLinkRepositoryService.saveInvite(groupId, UUID.randomUUID());
    }


    public void addUser(Account account, Long groupId) {
        AddUserEvent addUserEvent = new AddUserEvent(groupId, account.getName(), account.getGivenname(), account.getFamilyname(), account.getEmail());
        eventService.saveEvent(addUserEvent);
    }

    public void addUserList(List<User> users, Long groupId) {
        for (User user : users) {
            AddUserEvent addUserEvent = new AddUserEvent(groupId, user.getId(), user.getGivenname(), user.getFamilyname(), user.getEmail());
            eventService.saveEvent(addUserEvent);
        }
    }

    public void updateTitle(Account account, Long groupId, String title) {
        UpdateGroupTitleEvent updateGroupTitleEvent = new UpdateGroupTitleEvent(groupId, account.getName(), title);
        eventService.saveEvent(updateGroupTitleEvent);
    }

    public void updateDescription(Account account, Long groupId, String description) {
        UpdateGroupDescriptionEvent updateGroupDescriptionEvent = new UpdateGroupDescriptionEvent(groupId, account.getName(), description);
        eventService.saveEvent(updateGroupDescriptionEvent);
    }

    public void updateRole(String userId, Long groupId) throws EventException {
        UpdateRoleEvent updateRoleEvent;
        Group group = userService.getGroupById(groupId);
        User user = null;
        for (User member : group.getMembers()) {
            if (member.getId().equals(userId)) {
                user = member;
            }
        }
        assert user != null;
        if (group.getRoles().get(user.getId()) == Role.ADMIN) {
            updateRoleEvent = new UpdateRoleEvent(groupId, user.getId(), Role.MEMBER);
        } else {
            updateRoleEvent = new UpdateRoleEvent(groupId, user.getId(), Role.ADMIN);
        }
        eventService.saveEvent(updateRoleEvent);
    }

    public void deleteUser(String userId, Long groupId) throws EventException {
        Group group = userService.getGroupById(groupId);
        User user = null;
        for (User member : group.getMembers()) {
            if (member.getId().equals(userId)) {
                user = member;
            }
        }
        assert user != null;
        DeleteUserEvent deleteUserEvent = new DeleteUserEvent(groupId, user.getId());
        eventService.saveEvent(deleteUserEvent);
    }

    public void deleteGroupEvent(User user, Long groupId) {
        DeleteGroupEvent deleteGroupEvent = new DeleteGroupEvent(groupId, user.getId());
        eventService.saveEvent(deleteGroupEvent);
    }

    public void createLecture(Account account, String title, String description, Boolean visibility, List<User> users) throws EventException {
        Visibility visibility1;
        Long groupId = eventService.checkGroup();

        if (visibility) {
            visibility1 = Visibility.PUBLIC;
        } else {
            visibility1 = Visibility.PRIVATE;
        }

        CreateGroupEvent createGroupEvent = new CreateGroupEvent(groupId, account.getName(), null, GroupType.LECTURE, visibility1);
        eventService.saveEvent(createGroupEvent);

        addUser(account, groupId);
        updateTitle(account, groupId, title);
        updateDescription(account, groupId, description);
        updateRole(account.getName(), groupId);
        addUserList(users, groupId);
    }
}
